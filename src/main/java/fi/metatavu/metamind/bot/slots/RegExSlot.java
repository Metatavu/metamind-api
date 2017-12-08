package fi.metatavu.metamind.bot.slots;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rabidgremlin.mutters.core.Context;
import com.rabidgremlin.mutters.core.Slot;
import com.rabidgremlin.mutters.core.SlotMatch;

@SuppressWarnings ("squid:S2160")
public class RegExSlot extends Slot {
  
  private Pattern pattern;
  private String name;
  
  public RegExSlot(String name, String pattern) {
    this(name, Pattern.compile(pattern));
  }

  public RegExSlot(String name, Pattern pattern) {
    super();
    this.name = name;
    this.pattern = pattern;
  }

  @Override
  public SlotMatch match(String token, Context context) {
    Matcher matcher = pattern.matcher(token);
    if (matcher.matches()) {
      return new SlotMatch(this, token, matcher.group(0));
    }
    
    return null;
  }

  @Override
  public String getName() {
    return name;
  }

}