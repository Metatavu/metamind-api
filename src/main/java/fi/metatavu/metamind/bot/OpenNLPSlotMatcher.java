package fi.metatavu.metamind.bot;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabidgremlin.mutters.core.Context;
import com.rabidgremlin.mutters.core.Intent;
import com.rabidgremlin.mutters.core.Slot;
import com.rabidgremlin.mutters.core.SlotMatch;
import com.rabidgremlin.mutters.core.SlotMatcher;
import com.rabidgremlin.mutters.core.Tokenizer;

import fi.metatavu.metamind.persistence.models.SlotModel;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

/**
 * Implements a SlotMatcher that uses OpenNLP's NER framework.
 * 
 * @author Antti Leppä
 */
public class OpenNLPSlotMatcher implements SlotMatcher {

  private Logger log = LoggerFactory.getLogger(OpenNLPSlotMatcher.class);

  private Map<String, TokenNameFinderModel> slotModels = new HashMap<>();

  private Tokenizer tokenizer;

  /**
   * Constructor. Allows tokenizer to be supplied because NER can use case etc as cues, so may require different
   * tokenizer than used for intent matching.
   * 
   * @param tokenizer The tokenizer to use on an utterance for NER.
   */
  public OpenNLPSlotMatcher(Tokenizer tokenizer) {
    this.tokenizer = tokenizer;
  }

  /**
   * This set the NER model to use for a slot.
   * 
   * @param slotModel The slot model.
   */
  public void addSlotModel(String slotName, SlotModel slotModel) {
    try (InputStream inputStream = new ByteArrayInputStream(slotModel.getData())) {
      slotModels.put(slotName.toLowerCase(), new TokenNameFinderModel(inputStream));
    } catch (Exception e) {
      throw new IllegalArgumentException("Unable to load NER model", e);
    }
  }

  @SuppressWarnings ("squid:S1319")
  @Override
  public HashMap<Slot, SlotMatch> match(Context context, Intent intent, String utterance) {
    // This method is a direct copy from rabidgremlin's version of the OpenNLPSlotMatcher
    
    String[] utteranceTokens = tokenizer.tokenize(utterance);

    HashMap<Slot, SlotMatch> matchedSlots = new HashMap<>();

    for (Slot slot : intent.getSlots())
    {
      log.debug("Looking for Slot {}", slot.getName());

      TokenNameFinderModel tnfModel = slotModels.get(slot.getName().toLowerCase());
      if (tnfModel == null)
      {
        log.warn("Could not find NER model for slot {}", slot.getName());
        continue;
      }

      NameFinderME nameFinder = new NameFinderME(tnfModel);
      Span[] spans = nameFinder.find(utteranceTokens);

      if (spans.length > 0)
      {
        String[] matches = Span.spansToStrings(spans, utteranceTokens);

        log.debug("Matching for {} against {}", slot.getName(), matches);

        // TODO what to do with multi matches?
        SlotMatch match = slot.match(matches[0], context);
        if (match != null)
        {
          matchedSlots.put(slot, match);
          log.debug("Match found {}", match);
        }
        else
        {
          log.debug("No Match found slot: {} text: {} ", slot.getName(), matches);
        }
      }
      else
      {
        log.debug("Did not find slot {} utteranceTokens {} ", slot.getName(), utteranceTokens);
      }
    }

    return matchedSlots;
  }

}
