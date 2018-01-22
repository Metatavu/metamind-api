package fi.metatavu.metamind.bot;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Set;
import java.util.SortedMap;

import com.rabidgremlin.mutters.core.SlotMatcher;
import com.rabidgremlin.mutters.core.Tokenizer;
import com.rabidgremlin.mutters.core.ml.AbstractMachineLearningIntentMatcher;

import fi.metatavu.metamind.persistence.models.IntentModel;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;

public class OpenNLPIntentMatcher extends AbstractMachineLearningIntentMatcher {

  private DoccatModel model;

  /**
   * Constructor. Sets up the matcher to use the specified model and specifies the minimum and maybe match score.
   * 
   * @param intentModel intent model.
   * @param minMatchScore The minimum match score for an intent match to be considered good.
   * @param maybeMatchScore The maybe match score. Use -1 to disable maybe matching.
   * @param tokenizer The tokenizer to use when tokenizing an utterance.
   * @param slotMatcher The slot matcher to use to extract slots from the utterance.
   */
  public OpenNLPIntentMatcher(IntentModel intentModel, Tokenizer tokenizer, SlotMatcher slotMatcher, float minMatchScore, float maybeMatchScore) {
    super(tokenizer, slotMatcher, minMatchScore, maybeMatchScore);

    try (InputStream inputStream = new ByteArrayInputStream(intentModel.getData())) {
      model = new DoccatModel(inputStream);
    } catch (Exception e) {
      throw new IllegalArgumentException("Unable to load intent model", e);
    }
  }

  @Override
  protected SortedMap<Double, Set<String>> generateSortedScoreMap(String[] utteranceTokens) {
    DocumentCategorizerME intentCategorizer = new DocumentCategorizerME(model);
    return intentCategorizer.sortedScoreMap(utteranceTokens);
  }

}
