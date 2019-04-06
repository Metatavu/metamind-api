package fi.metatavu.metamind.bot.script;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;

import fi.metatavu.metamind.bot.BotRuntimeContext;
import fi.metatavu.metamind.persistence.models.Knot;
import fi.metatavu.metamind.persistence.models.Script;
import fi.metatavu.metamind.polyglot.XMLHttpRequest;
import fi.metatavu.metamind.scripts.ScriptController;

/**
 * Script processor 
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class ScriptProcessor {
  
  private static final Pattern INLINE_PATTERN = Pattern.compile("(<script.*>)((.|\\n)*?)(<\\/script>)");
  private static final Pattern NAMED_PATTERN = Pattern.compile("(<script)(.*)(\\/>)");
  private static final Pattern ATTRIBUTES_PATTERN = Pattern.compile("([a-z]{1,})\\s{0,}\\=\\s{0,}\\\"((.|\\\\n\\s)*?)\\\""); 

  private static final Set<String> RESERVED_PARAMS = new HashSet<String>(Arrays.asList(
    new String[] { "name", "version", "module", "function", "target" }
  ));

  @Inject
  private Logger logger;

  @Inject
  private BotScriptBinding botScriptBinding;
  
  @Inject
  private BotRuntimeContext botRuntimeContext;

  @Inject
  private ScriptController scriptController; 
  
  /**
   * Processes texts in current knot
   */
  public void processScripts() {
    Knot currentKnot = botRuntimeContext.getCurrentKnot();
    botRuntimeContext.setResponse(processScripts(currentKnot.getContent()));
  }

  /**
   * Processes scripts in text
   * 
   * @param text text
   * @return processed text
   */
  public String processScripts(String text) {
    String result = processInlineScripts(text);
    result = processNamedScripts(result);
    return result;
  }
  
  /**
   * Processes inline scripts from text
   * 
   * @param text text
   * @return processed text
   */
  private String processInlineScripts(String text) {
    Matcher matcher = INLINE_PATTERN.matcher(text);   
    return matcher.replaceAll((result) -> {
      String content = result.group(2);
      return processScript(new RunnableScript("js", content, "inline"), Collections.emptyMap());
    });
  }

  /**
   * Processes named scripts from text
   * 
   * @param text text
   * @return processed text
   */
  private String processNamedScripts(String text) {
    Matcher matcher = NAMED_PATTERN.matcher(text);   
    return matcher.replaceAll((result) -> {
      Map<String, String> params = parseNamedAttributes(StringUtils.trim(result.group(2)));
      String name = params.get("name");
      String version = params.get("version");
      
      if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(version)) {
        Script script = scriptController.findScriptByNameAndVersion(name, version);
        if (script == null) {          
          logger.warn("Script with name {} and version {} could not be found, skipping", name, version);
        } else {
          return processScript(new RunnableScript(script.getLanguage(), script.getContent(), script.getName()), params);
        }
      } else {
        logger.warn("Script without name or version attribute, skipping");
      }
      
      return "";
    });
  }
  
  /**
   * Parses script tag attributes
   * 
   * @param string attribute string
   * @return parsed attributes
   */
  private Map<String, String> parseNamedAttributes(String string) {
    Map<String, String> result = new HashMap<>();
    
    Matcher matcher = ATTRIBUTES_PATTERN.matcher(string);
    
    while (matcher.find()) {
      String name = matcher.group(1);
      String value = matcher.group(2);
      result.put(name, value);
    }
    
    return result;
  }

  /**
   * Processes a script
   * 
   * @param script script
   * @param params parameters
   * @return processed output
   */
  private String processScript(RunnableScript script, Map<String, String> params) {
    try (Context scriptingContext = Context.create(script.getLanguage())) {
      Map<String, String> scriptArgs = new HashMap<>();
      
      params.keySet().stream().forEach(param -> {
        if (!RESERVED_PARAMS.contains(param)) {
          scriptArgs.put(param, params.get(param));
        }
      });
      
      Value bindings = scriptingContext.getBindings(script.getLanguage());
      
      bindings.putMember("XMLHttpRequest", XMLHttpRequest.class);
      bindings.putMember("bot", botScriptBinding);
     
      Source source = Source.newBuilder(script.getLanguage(), script.getContent(), script.getName()).build();
      Value returnValue = scriptingContext.eval(source);
      
      if (returnValue.isString()) {      
        return returnValue.asString();
      }

    } catch (Exception e) {
      logger.error("Error running script", e);
    }
    
    return "";
  }
  
  
}
