package fi.metatavu.metamind.nlp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import fi.metatavu.metamind.bot.KnotTrainingMaterialUpdateRequestEvent;
import fi.metatavu.metamind.persistence.dao.IntentDAO;
import fi.metatavu.metamind.persistence.dao.KnotDAO;
import fi.metatavu.metamind.persistence.dao.KnotIntentModelDAO;
import fi.metatavu.metamind.persistence.dao.TrainingMaterialDAO;
import fi.metatavu.metamind.persistence.models.Knot;
import fi.metatavu.metamind.persistence.models.KnotIntentModel;
import fi.metatavu.metamind.persistence.models.Story;
import fi.metatavu.metamind.persistence.models.TrainingMaterial;
import opennlp.tools.doccat.BagOfWordsFeatureGenerator;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.doccat.FeatureGenerator;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

/**
 * Training material controller
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class TrainingMaterialController {

  private static Pattern PREFIX_PATTERN = Pattern.compile("^", Pattern.MULTILINE);

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
  private Event<KnotTrainingMaterialUpdateRequestEvent> knotTrainingMaterialUpdateRequestEvent;

  /**
   * Creates new TrainingMaterial
   * 
   * @param name name
   * @param text text
   * @param story story
   * @param creatorId creator's id
   * @return created trainingMaterial
   */
  public TrainingMaterial createTrainingMaterial(String name, String text, Story story, UUID creatorId) {
    return trainingMaterialDAO.create(UUID.randomUUID(), name, text, story, creatorId, creatorId);
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
   * @return updated training material
   */
  public TrainingMaterial updateTrainingMaterial(TrainingMaterial trainingMaterial, String name, String text, UUID lastModifierId) {
    trainingMaterial = trainingMaterialDAO.updateText(trainingMaterial, text, lastModifierId);
    trainingMaterial = trainingMaterialDAO.updateName(trainingMaterial, name, lastModifierId);
    requestKnotsTrainingMaterialUpdate(trainingMaterial);
    return trainingMaterial;
  }

  /**
   * Updates training material for a knot
   * 
   * @param knot knot
   */
  public void updateKnotTrainingMaterial(Knot knot) {
    String knotLines = intentDAO.listBySourceKnot(knot).stream()
      .filter(intent -> intent != null && intent.getTrainingMaterial() != null && StringUtils.isNotBlank(intent.getTrainingMaterial().getText()))
      .map(intent -> {
        String materialLines = intent.getTrainingMaterial().getText();      
        Matcher prefixMatcher = PREFIX_PATTERN.matcher(materialLines);
        return prefixMatcher.replaceAll(String.format("%s ", intent.getId().toString()));
      }).collect(Collectors.joining("\n"));

    Story story = knot.getStory();
    Locale locale = story.getLocale();
    String language = locale.getLanguage();
    
    updateTrainingMaterial(knot, knotLines, language);
  }

  /**
   * Finds training material by id
   * 
   * @param trainingMaterialId training material id
   * @return found training material or null if not found
   */
  public TrainingMaterial findTrainingMaterialById(UUID trainingMaterialId) {
    return trainingMaterialDAO.findById(trainingMaterialId);
  }

  /**
   * Lists training materials
   * 
   * @param story filter materials by story
   * @return training materials
   */
  public List<TrainingMaterial> listTrainingMaterials(Story story) {
    if (story == null) {
      return trainingMaterialDAO.listAll();
    }

    return trainingMaterialDAO.listByStoryOrStoryNull(story);
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
   * Updates intent training material for a knot
   * 
   * @param knot knot
   * @param knotIntentLines intent training data
   * @param language language
   */
  private void updateTrainingMaterial(Knot knot, String knotIntentLines, String language) {
    try (ByteArrayInputStream lineStream = new ByteArrayInputStream(knotIntentLines.getBytes("UTF-8"))) {
      byte[] doccatModelData = createDoccatModelData(language, lineStream);
      
      KnotIntentModel knotIntentModel = knotIntentModelDAO.findByKnot(knot);
      if (knotIntentModel == null) {
        knotIntentModelDAO.create(doccatModelData, knot);
      } else {      
        knotIntentModelDAO.updateData(knotIntentModel, doccatModelData);
      }
      
    } catch (IOException e) {
      logger.error(String.format("Failed to create training material for knot %s", knot.getId()), e);
    }
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
      }
 
      return outputStream.toByteArray();
    }
  }

  /**
   * Requests training material update for knots related to a training material
   * 
   * @param trainingMaterial training material
   */
  private void requestKnotsTrainingMaterialUpdate(fi.metatavu.metamind.persistence.models.TrainingMaterial trainingMaterial) {
    for (Knot knot : knotDAO.listByTargetIntentTrainingMaterial(trainingMaterial)) {
      knotTrainingMaterialUpdateRequestEvent.fire(new KnotTrainingMaterialUpdateRequestEvent(knot.getId()));
    }
  }
  
}
