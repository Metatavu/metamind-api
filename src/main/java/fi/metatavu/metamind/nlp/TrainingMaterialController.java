package fi.metatavu.metamind.nlp;

import fi.metatavu.metamind.bot.KnotTrainingMaterialUpdateRequestEvent;
import fi.metatavu.metamind.bot.StoryGlobalTrainingMaterialUpdateRequestEvent;
import fi.metatavu.metamind.persistence.dao.*;
import fi.metatavu.metamind.persistence.models.*;
import fi.metatavu.metamind.api.spec.model.ExportedStoryTrainingMaterial;
import fi.metatavu.metamind.api.spec.model.TrainingMaterialType;
import fi.metatavu.metamind.api.spec.model.TrainingMaterialVisibility;
import fi.metatavu.metamind.utils.RegexUtils;
import one.util.streamex.StreamEx;
import opennlp.tools.doccat.*;
import opennlp.tools.namefind.BioCodec;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Training material controller
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class TrainingMaterialController {

  private static Pattern STRIP_EMPTY_LINES_PATTERN = Pattern.compile("^[ \t]*\r?\n", Pattern.MULTILINE);
  private static Pattern OPENNLP_DOCCAT_PREFIX_PATTERN = Pattern.compile("^", Pattern.MULTILINE);
  private static Pattern OPENNLP_NER_REPLACE_PATTERN = Pattern.compile("(<START:)(.*?)(>)");
  private static Pattern OPENNLP_REGEX_PATTERN = Pattern.compile("^[a-zA-Z]*", Pattern.MULTILINE);

  @Inject
  private Logger logger;

  @Inject
  private TrainingMaterialDAO trainingMaterialDAO;
  
  @Inject
  private IntentDAO intentDAO;
  
  @Inject
  private KnotDAO knotDAO;

  @Inject
  private KnotIntentModelDAO knotIntentModelDAO;

  @Inject
  private StoryGlobalIntentModelDAO storyGlobalIntentModelDAO;

  @Inject
  private IntentTrainingMaterialDAO intentTrainingMaterialDAO;

  @Inject
  private VariableDAO variableDAO; 

  @Inject
  private Event<KnotTrainingMaterialUpdateRequestEvent> knotTrainingMaterialUpdateRequestEvent;

  @Inject
  private Event<StoryGlobalTrainingMaterialUpdateRequestEvent> storyGlobalTrainingMaterialUpdateRequestEvent;

  /**
   * Creates new TrainingMaterial
   * 
   * @param type type
   * @param name name
   * @param text text
   * @param story story
   * @param creatorId creator's id
   * @param visibility TrainingMaterialVisibility
   * @return created trainingMaterial
   */
  public TrainingMaterial createTrainingMaterial(TrainingMaterialType type, String name, String text, Story story, UUID creatorId, TrainingMaterialVisibility visibility) {
    return trainingMaterialDAO.create(UUID.randomUUID(), type, name, text, story, creatorId, creatorId, visibility);
  }
  
  /**
   * Updates training material. 
   * 
   * Method requests for knot material updates
   * 
   * @param trainingMaterial training material
   * @param name name
   * @param text text
   * @param lastModifierId last modifier id
   * @param visibility TrainingMaterialVisibility
   * @return updated training material
   */
  public TrainingMaterial updateTrainingMaterial(TrainingMaterial trainingMaterial, String name, String text, UUID lastModifierId, TrainingMaterialVisibility visibility) {
    trainingMaterial = trainingMaterialDAO.updateText(trainingMaterial, text, lastModifierId);
    trainingMaterial = trainingMaterialDAO.updateName(trainingMaterial, name, lastModifierId);
    trainingMaterial = trainingMaterialDAO.updateVisibility(trainingMaterial, visibility, trainingMaterial.getId());
    requestTrainingMaterialUpdates(trainingMaterial);

    return trainingMaterial;
  }
  
  /**
   * Lists training materials for an intent
   * 
   * @param intent intent
   * @return training materials
   */
  public List<TrainingMaterial> listTrainingMaterialByIntent(Intent intent) {
    return intentTrainingMaterialDAO.listTrainingMaterialByIntent(intent);
  }
  
  /**
   * Sets intent training material
   * 
   * @param intent intent
   * @param type type
   * @param trainingMaterial training material
   * @return updated intent material or null if material is not defined
   */
  public IntentTrainingMaterial setIntentTrainingMaterial(Intent intent, TrainingMaterialType type, TrainingMaterial trainingMaterial) {
    List<Intent> updateIntents = new ArrayList<>(2);
    
    try {
      IntentTrainingMaterial existing = intentTrainingMaterialDAO.findByIntentAndType(intent, type);

      if (trainingMaterial == null) {
        if (existing != null) {
          updateIntents.add(existing.getIntent());
          intentTrainingMaterialDAO.delete(existing);
        }
        
        return null;
      } else {
        if (existing != null) {
          if (trainingMaterial.getId().equals(existing.getTrainingMaterial().getId())) {
            return existing;
          } else {
            updateIntents.add(existing.getIntent());
            intentTrainingMaterialDAO.delete(existing);
          }
        }

        updateIntents.add(intent);
        
        return intentTrainingMaterialDAO.create(intent, trainingMaterial);
      }
    } finally {
      requestTrainingMaterialUpdates(updateIntents);
    }
  }

  /**
   * Updates training material for a knot
   * 
   * @param knot knot
   */
  public void updateKnotTrainingMaterial(Knot knot) {
    List<Intent> knotIntents = intentDAO.listBySourceKnot(knot);
    List<Intent> storyGlobalIntents = intentDAO.listByStoryAndGlobal(knot.getStory(), true);
    List<Intent> intents = new ArrayList<>(knotIntents);
    intents.addAll(storyGlobalIntents);
    
    Map<TrainingMaterialType, String> lineDatas = Arrays.stream(TrainingMaterialType.values()).collect(Collectors.toMap(type -> type, (type) -> {
      return getTrainingMaterialLines(knot.getStory(), intents, type);
    }));
    
    Story story = knot.getStory();
    Locale locale = story.getLocale();
    String language = locale.getLanguage();
    
    updateKnotTrainingMaterial(knot, lineDatas, language);
  }

  /**
   * Updates training material for given story
   * 
   * @param story story
   */
  public void updateStoryTrainingMaterial(Story story) {
    String knotLines = getTrainingMaterialLines(story, intentDAO.listByStoryAndGlobal(story, true), TrainingMaterialType.INTENTOPENNLPDOCCAT);
    Locale locale = story.getLocale();
    String language = locale.getLanguage();
    
    updateStoryTrainingMaterial(story, knotLines, language);
  }

  /**
   * Finds training material by id
   * 
   * @param trainingMaterialId training material id
   * @return found training material or null if not found
   */
  public TrainingMaterial findTrainingMaterialById(UUID trainingMaterialId) {
    if (trainingMaterialId == null) {
      return  null;
    }

    return trainingMaterialDAO.findById(trainingMaterialId);
  }

  /**
   * Lists training materials
   * 
   * @param story filter materials by story
   * @param type type
   * @param visibility TrainingMaterialVisibility
   * @return training materials
   */
  public List<TrainingMaterial> listTrainingMaterials(Story story, TrainingMaterialType type, TrainingMaterialVisibility visibility) {
    return trainingMaterialDAO.list(true, story, type, visibility);
  }
  
  /**
   * Deletes a training material entity
   * 
   * @param trainingMaterial training material entity
   */
  public void deleteTrainingMaterial(TrainingMaterial trainingMaterial) {
    trainingMaterialDAO.delete(trainingMaterial);
  }
  
  /**
   * Exports a training material
   * 
   * @param trainingMaterial material to be exported
   * @return exported training material
   */
  public ExportedStoryTrainingMaterial exportTrainingMaterial(TrainingMaterial trainingMaterial) {
    ExportedStoryTrainingMaterial exportedMaterial = new ExportedStoryTrainingMaterial();
    exportedMaterial.setId(trainingMaterial.getId());
    exportedMaterial.setName(trainingMaterial.getName());
    exportedMaterial.setType(trainingMaterial.getType());
    exportedMaterial.setText(trainingMaterial.getText());
    exportedMaterial.setVisibility(trainingMaterial.getVisibility().toString());
    return exportedMaterial;
  }
   
  /**
   * Resolves training materials for given list of intents and training material type
   * 
   * @param story story
   * @param intents intents
   * @param type training material type
   * @return training material lines
   */
  private String getTrainingMaterialLines(Story story, List<Intent> intents, TrainingMaterialType type) {
    switch (type) {    
      case INTENTREGEX:
      case INTENTOPENNLPDOCCAT:
        return getTrainingMaterialLines(intents, type, (intentTrainingMaterial) -> {
          TrainingMaterial trainingMaterial = intentTrainingMaterial.getTrainingMaterial();
          Intent intent = intentTrainingMaterial.getIntent();
          String materialLines = stripEmptyLines(trainingMaterial.getText());
          Matcher prefixMatcher = OPENNLP_DOCCAT_PREFIX_PATTERN.matcher(materialLines);
          return prefixMatcher.replaceAll(String.format("%s ", intent.getId().toString()));
        });
      case VARIABLEOPENNLPNER:
        return getVarialeOpenNlpNerTrainingMaterialLines(story, intents, type);
      case VARIABLEOPENNLPREGEX:
        return getVarialeOpenNlpRegexTrainingMaterialLines(story, intents, type);
    }
    
    return null;
  }

  /**
   * Returns variable OpenNLP NER training material lines 
   * 
   * @param story story
   * @param intents intents
   * @param type type
   * @return variable openNLP NER training material lines
   */
  private String getVarialeOpenNlpNerTrainingMaterialLines(Story story, List<Intent> intents, TrainingMaterialType type) {
    Map<String, String> variableMap = new HashMap<>();
    
    return getTrainingMaterialLines(intents, type, (intentTrainingMaterial) -> {
      TrainingMaterial trainingMaterial = intentTrainingMaterial.getTrainingMaterial();
      String materialLines = trainingMaterial.getText();
      
      return RegexUtils.replaceAll(OPENNLP_NER_REPLACE_PATTERN, materialLines, match -> {
        String variableName = match.group(2);
        String cacheKey = String.format("%s-%s", story.getId(), variableName);
        
        if (!variableMap.containsKey(cacheKey)) {
          Variable variable = variableDAO.findByStoryNameName(story, variableName);
          if (variable != null) {
            variableMap.put(cacheKey, variable.getId().toString());
          } else {
            logger.warn("Variable {} not found from story", variableName, story.getId());
            variableMap.put(cacheKey, new UUID(0l, 0l).toString());
          }
        }
        
        return String.format("%s%s%s", match.group(1), variableMap.get(cacheKey), match.group(3));
      });
    });
  }

  /**
   * Returns variable OpenNLP Regex training material lines 
   * 
   * @param story story
   * @param intents intents
   * @param type type
   * @return variable OpenNLP Regex training material lines
   */
  private String getVarialeOpenNlpRegexTrainingMaterialLines(Story story, List<Intent> intents, TrainingMaterialType type) {
    Map<String, String> variableMap = new HashMap<>();
    
    return getTrainingMaterialLines(intents, type, (intentTrainingMaterial) -> {
      TrainingMaterial trainingMaterial = intentTrainingMaterial.getTrainingMaterial();
      String materialLines = trainingMaterial.getText();      
      return RegexUtils.replaceAll(OPENNLP_REGEX_PATTERN, materialLines, match -> {
        String variableName = match.group();
        String cacheKey = String.format("%s-%s", story.getId(), variableName);
        
        if (!variableMap.containsKey(cacheKey)) {
          Variable variable = variableDAO.findByStoryNameName(story, variableName);
          if (variable != null) {
            variableMap.put(cacheKey, variable.getId().toString());
          } else {
            logger.warn("Variable {} not found from story", variableName, story.getId());
            variableMap.put(cacheKey, new UUID(0l, 0l).toString());
          }
        }
        
        return variableMap.get(cacheKey);
      });
    });
  }
  
  /**
   * Strips empty lines from a text
   * 
   * @param text text
   * @return text with empty lines stripped
   */
  private String stripEmptyLines(String text) {
    Matcher matcher = STRIP_EMPTY_LINES_PATTERN.matcher(text);
    return matcher.replaceAll("");
  }

  /**
   * Resolves training material lines for given list of intents
   * 
   * @param intents intents
   * @return training material lines
   */
  private String getTrainingMaterialLines(List<Intent> intents, TrainingMaterialType type, Function<IntentTrainingMaterial, String> translator) {
    return getIntentsTrainingMaterials(intents, type).stream()
      .map(intentTrainingMaterial -> {
        return translator.apply(intentTrainingMaterial);        
      })
      .distinct()
      .collect(Collectors.joining("\n"));
  }
  
  /**
   * Returns training materials for given intents without duplicate training materials
   * 
   * @param intents intents
   * @param type type
   * @return training materials for given intents 
   */
  private List<IntentTrainingMaterial> getIntentsTrainingMaterials(List<Intent> intents, TrainingMaterialType type) {
    return StreamEx.of(intents)
      .map(intent -> {
        return intentTrainingMaterialDAO.findByIntentAndType(intent, type);
      })
      .filter(Objects::nonNull)
      .distinct(intent -> intent.getTrainingMaterial().getId())
      .filter(intentTrainingMaterial -> {
        TrainingMaterial trainingMaterial = intentTrainingMaterial.getTrainingMaterial();
        return trainingMaterial != null && StringUtils.isNotBlank(trainingMaterial.getText()); 
      })
      .collect(Collectors.toList());
  }
  
  /**
   * Updates intent training material for a knot
   * 
   * @param story story
   * @param lines intent training data
   * @param language language
   */
  private void updateStoryTrainingMaterial(Story story, String lines, String language) {
    try {
      byte[] doccatModelData = createModelData(TrainingMaterialType.INTENTOPENNLPDOCCAT, language, lines);
      
      StoryGlobalIntentModel storyGlobalIntentModel = storyGlobalIntentModelDAO.findByStory(story);
      if (storyGlobalIntentModel == null) {
        storyGlobalIntentModelDAO.create(TrainingMaterialType.INTENTOPENNLPDOCCAT, doccatModelData, story);
      } else {
        storyGlobalIntentModelDAO.updateData(storyGlobalIntentModel, doccatModelData);
      }
      
    } catch (IOException e) {
      logger.error(String.format("Failed to create training material for story %s", story.getId()), e);
    }
  }

  /**
   * Updates intent training material for a knot
   * 
   * @param knot knot
   * @param lineDatas intent training data
   * @param language language
   */
  private void updateKnotTrainingMaterial(Knot knot, Map<TrainingMaterialType, String> lineDatas, String language) {
    for (TrainingMaterialType type : lineDatas.keySet()) {
      try {
        KnotIntentModel knotIntentModel = knotIntentModelDAO.findByKnotAndType(knot, type);

        String lines = lineDatas.get(type);
        if (StringUtils.isNotBlank(lines)) {
          byte[] modelData = createModelData(type, language, lines);
          
          if (knotIntentModel == null) {
            knotIntentModelDAO.create(type, modelData, knot);
          } else {      
            knotIntentModelDAO.updateData(knotIntentModel, modelData);
          }
        } else {
          if (knotIntentModel != null) {
            knotIntentModelDAO.delete(knotIntentModel);
          }
        }
      } catch (IOException e) {
        logger.error(String.format("Failed to create training material for knot %s", knot.getId()), e);
      }
    }
  }

  /**
   * Creates document categorization model from lines
   *
   * @param type type
   * @param language language
   * @param lines lines
   * @return document categorization data
   * @throws IOException thrown when training data building fails
   */
  private byte[] createModelData(TrainingMaterialType type, String language, String lines) throws IOException {
    try (ByteArrayInputStream lineStream = new ByteArrayInputStream(lines.getBytes("UTF-8"))) {
      switch (type) {
        case INTENTREGEX:
        case VARIABLEOPENNLPREGEX:
          return createRegexModelData(language, lineStream);      
        case INTENTOPENNLPDOCCAT:
          return createDoccatModelData(language, lineStream);      
        case VARIABLEOPENNLPNER:
          return createTokenNameFinderModelData(language, lineStream);
      }  
    }
    
    return new byte[0];
  }
  
  /**
   * Serializes Regex model data
   * 
   * @param language language
   * @param lineStream line
   * @return serialized data
   * @throws IOException thrown when training data building fails
   */
  private byte[] createRegexModelData(String language, ByteArrayInputStream lineStream) throws IOException {
    return IOUtils.toByteArray(lineStream);
  }

  /**
   * Creates document categorization model from line stream
   * 
   * @param languageCode language
   * @param lineStream line stream
   * @return document categorization data
   * @throws IOException thrown when training data building fails
   */
  private byte[] createDoccatModelData(String languageCode, InputStream lineStream) throws IOException {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      InputStreamFactory streamFactory = new DefaultInputStreamFactory(lineStream);
      
      try (ObjectStream<String> lineObjectStream = new PlainTextByLineStream(streamFactory, "UTF-8"); ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineObjectStream)) {
        TrainingParameters trainingParameters = new TrainingParameters();
        
        trainingParameters.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
        trainingParameters.put(TrainingParameters.ITERATIONS_PARAM, 500);
        trainingParameters.put(TrainingParameters.CUTOFF_PARAM, 0);
        
        DoccatFactory doccatFactory = DoccatFactory.create(null, new FeatureGenerator[] { new BagOfWordsFeatureGenerator() });
        DoccatModel model = DocumentCategorizerME.train(languageCode, sampleStream, trainingParameters, doccatFactory);
        model.serialize(outputStream);
      } catch (InsufficientTrainingDataException e) {
        logger.info("Insufficient training data for Doccat model");
      }
 
      return outputStream.toByteArray();
    }
  }
  
  /**
   * Creates token name finder model from line stream
   * 
   * @param languageCode language
   * @param lineStream line stream
   * @return document categorization data
   * @throws IOException thrown when training data building fails
   */
  private byte[] createTokenNameFinderModelData(String languageCode, InputStream lineStream) throws IOException {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      InputStreamFactory streamFactory = new DefaultInputStreamFactory(lineStream);
      
      try (ObjectStream<String> lineObjectStream = new PlainTextByLineStream(streamFactory, "UTF-8"); NameSampleDataStream sampleStream = new NameSampleDataStream(lineObjectStream)) {
        SequenceCodec<String> seqCodec = new BioCodec();
        TrainingParameters trainingParameters = new TrainingParameters();
        byte[] featureGeneratorBytes = null;
        Map<String, Object> resources = new HashMap<>();
        String factory = null;
        trainingParameters.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
        trainingParameters.put(TrainingParameters.ITERATIONS_PARAM, 500);
        trainingParameters.put(TrainingParameters.CUTOFF_PARAM, 0);
        
        TokenNameFinderFactory tokenNameFinderFactory = TokenNameFinderFactory.create(factory, featureGeneratorBytes, resources, seqCodec);
        TokenNameFinderModel model = opennlp.tools.namefind.NameFinderME.train(languageCode, null, sampleStream, trainingParameters, tokenNameFinderFactory);
        
        model.serialize(outputStream);
      } catch (InsufficientTrainingDataException e) {
        logger.info("Insufficient training data for NER model");
      }
 
      return outputStream.toByteArray();
    }
  }

  /**
   * Requests training material updates
   * 
   * @param intents intents list
   */
  private void requestTrainingMaterialUpdates(List<Intent> intents) {
    List<Intent> globalIntents = new ArrayList<>();
    List<Intent> localIntents = new ArrayList<>();
    
    for (Intent intent : intents) {
      if (Boolean.TRUE.equals(intent.getGlobal())) {
        globalIntents.add(intent);
      } else {
        localIntents.add(intent);
      }
    }
    
    Set<Story> stories = StreamEx.of(globalIntents)
      .map(Intent::getTargetKnot).map(Knot::getStory)
      .distinct(Story::getId)
      .collect(Collectors.toSet());
    
    Set<UUID> storyIds = stories.stream().map(Story::getId).collect(Collectors.toSet());
    
    localIntents.stream()
      .filter(intent -> !storyIds.contains(intent.getTargetKnot().getStory().getId()))
      .map(Intent::getSourceKnot)
      .forEach(this::requestKnotTrainingMaterialUpdate);
    
    stories.stream()
      .forEach(this::requestStoryTrainingMaterialUpdate);
  }
  
  /**
   * Requests training material updates
   * 
   * @param trainingMaterial training material
   */
  private void requestTrainingMaterialUpdates(TrainingMaterial trainingMaterial) {
    requestKnotsTrainingMaterialUpdate(trainingMaterial);
    requestStoryGlobalTrainingMaterialUpdate(trainingMaterial);
  }

  /**
   * Requests training material update for knots related to a training material
   *
   * @param trainingMaterial training material
   */
  private void requestKnotsTrainingMaterialUpdate(TrainingMaterial trainingMaterial) {
    List<Story> stories = intentTrainingMaterialDAO.listStoriesByTrainingMaterialAndGlobal(trainingMaterial, Boolean.TRUE);
    if (stories.isEmpty()) {
      // Material is not attached into global intents, no need to update everything

      for (Knot knot : intentTrainingMaterialDAO.listByTargetIntentTrainingMaterial(trainingMaterial)) {
        requestKnotTrainingMaterialUpdate(knot);
      }
    } else {
      // Material is attached into global intents, we need to update all knots in related stories

      List<Knot> knots = stories.stream().map(story -> knotDAO.listByStory(story)).flatMap(List::stream).collect(Collectors.toList());
      for (Knot knot : knots) {
        requestKnotTrainingMaterialUpdate(knot);
      }
    }
  }

  /**
   * Requests training material update for knots related to a training material
   *
   * @param trainingMaterial training material
   */
  private void requestStoryGlobalTrainingMaterialUpdate(TrainingMaterial trainingMaterial) {
    List<Story> stories = intentTrainingMaterialDAO.listStoriesByTrainingMaterialAndGlobal(trainingMaterial, Boolean.TRUE);
    for (Story story : stories) {
      requestStoryTrainingMaterialUpdate(story);
    }
  }

  /**
   * Requests training material update for a knot
   * 
   * @param knot knot
   */
  private void requestKnotTrainingMaterialUpdate(Knot knot) {
    knotTrainingMaterialUpdateRequestEvent.fire(new KnotTrainingMaterialUpdateRequestEvent(knot.getId()));
  }

  /**
   * Requests training material update for a story
   * 
   * @param story story
   */
  private void requestStoryTrainingMaterialUpdate(Story story) {
    storyGlobalTrainingMaterialUpdateRequestEvent.fire(new StoryGlobalTrainingMaterialUpdateRequestEvent(story.getId()));
  }
  
}
