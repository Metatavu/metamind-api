package fi.metatavu.metamind.bot.slots;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabidgremlin.mutters.core.SlotMatch;

import fi.metatavu.metamind.slot.models.NumberSlotTestModels;

public class LocalizedNumberSlotTest {

  @Test
  public void testFinnishWordStringToNumber() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    NumberSlotTestModels testModels = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream("fi/metatavu/metamind/bot/slots/number-slot-tests.json"), NumberSlotTestModels.class);
    LocalizedNumberSlot localizedNumberSlot = new LocalizedNumberSlot("test");
    
    testModels.getFi().forEach((testModel) -> {
      SlotMatch match = localizedNumberSlot.match(testModel.getInput(), null);
      assertNotNull(match);
      assertEquals(testModel.getExpect(), match.getValue());
    });
  }
  
}
