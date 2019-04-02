package fi.metatavu.metamind.bot.functions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.bladecoder.ink.runtime.Story;
import com.rabidgremlin.mutters.bot.ink.CurrentResponse;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.session.Session;

import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;

import fi.metatavu.metamind.persistence.models.Script;
import fi.metatavu.metamind.polyglot.XMLHttpRequest;
import fi.metatavu.metamind.scripts.ScriptController;

/**
 * Metabot function for running scripts
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class RunScriptMetaBotFunction extends AbstractMetaBotFunction {

  private static final Set<String> RESERVED_PARAMS = new HashSet<String>(Arrays.asList(
    new String[] { "name", "version", "module", "function", "target" }
  ));

  @Inject
  private Logger logger;

  @Inject
  private ScriptController scriptController;

  @Override
  public String getFunctionName() {
    return "RUN_SCRIPT";
  }
  
  @Override
  public void execute(CurrentResponse currentResponse, Session session, IntentMatch intentMatch, Story story, String paramString) {
    Map<String, String> params = getParsedParam(paramString);
    
    String name = params.get("name");
    String version = params.get("version");
    String moduleName = params.get("module");
    String functionName = params.get("function");
    
    if (StringUtils.isBlank(name)) {
      logger.error("Could not run script without name");
      return;
    }

    if (StringUtils.isBlank(version)) {
      logger.error("Could not run script without version");
      return;
    }

    if (StringUtils.isBlank(moduleName)) {
      logger.error("Could not run script without module");
      return;
    }

    if (StringUtils.isBlank(functionName)) {
      logger.error("Could not run script without function");
      return;
    }

    Script script = scriptController.findScriptByNameAndVersion(name, version);
    if (script == null) {
      logger.error(String.format("Could not find script with name %s and version %s", name, version));
      return;
    }
    
    try (Context scriptingContext = Context.create(script.getLanguage())) {
      Map<String, String> scriptArgs = new HashMap<>();
      params.keySet().stream().forEach(param -> {
        if (!RESERVED_PARAMS.contains(param)) {
          scriptArgs.put(param, params.get(param));
        }
      });
      scriptingContext.getBindings(script.getLanguage()).putMember("XMLHttpRequest", XMLHttpRequest.class);
      scriptingContext.getPolyglotBindings().putMember("storyVariablesState", story.getVariablesState());

      Source source = Source.newBuilder(script.getLanguage(), script.getContent(), script.getName()).build();
      scriptingContext.eval(source);

      Value modules = scriptingContext.getPolyglotBindings();
      if (!modules.hasMember(moduleName)) {
        logger.error(String.format("Module with name %s not found. Available modules %s", moduleName, StringUtils.join(modules.getMemberKeys(), ", ")));
        return;
      }

      Value module = modules.getMember(moduleName);
      if (!module.hasMember(functionName) || !module.getMember(functionName).canExecute()) {
        logger.error(String.format("Function with name %s does not exits or is not executable", functionName));
        return;
      }

      Value returnValue = module.getMember(functionName).execute(scriptArgs);
      if (params.containsKey("target")) {
        String targetVariableName = params.get("target");
        story.getVariablesState().set(targetVariableName, returnValue.asString());
      }

    } catch (Exception e) {
      logger.error("Error running script", e);
    }
    
  }

}
