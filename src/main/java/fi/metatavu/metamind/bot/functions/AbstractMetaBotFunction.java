package fi.metatavu.metamind.bot.functions;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.bladecoder.ink.runtime.AbstractValue;
import com.bladecoder.ink.runtime.RTObject;
import com.bladecoder.ink.runtime.Story;
import com.bladecoder.ink.runtime.Value;
import com.bladecoder.ink.runtime.ValueType;
import com.bladecoder.ink.runtime.VariableAssignment;
import com.bladecoder.ink.runtime.VariablesState;

public abstract class AbstractMetaBotFunction implements MetaBotFunction {

  @Inject
  private Logger logger;

  /**
   * Parses param string as map. 
   * 
   * Format is (param1:value1,param2:value)
   * 
   * @param paramString parameter string
   * @return parsed map
   */
  protected Map<String, String> getParsedParam(String paramString) {
    Map<String, String> result = new HashMap<>();
    
    String[] params = StringUtils.split(StringUtils.stripStart(StringUtils.stripEnd(paramString, ")"), "("), ",");
    for (String param : params) {
      String[] parts = StringUtils.split(param, ":", 2);
      if (parts.length == 2) {
        result.put(StringUtils.trim(parts[0]), parts[1]);
      }
    }
    
    return result;
  }

  /**
   * Returns variable value as float or null if variable is not set or is not a float or int
   * 
   * @param story story
   * @param variableName variable name
   * @return variable value as float or null if variable is not set or is not a float or int
   */
  protected Float getVariableNumber(Story story, String variableName) {
    Value<?> variableValue = getVariableValue(story, variableName);
    if (variableValue != null) {
      if (variableValue.getValueType() == ValueType.Float) {
        return (Float) variableValue.getValue();
      } else if (variableValue.getValueType() == ValueType.Int) {
        try {
          @SuppressWarnings("unchecked") Value<Float> floatValue = (Value<Float>) variableValue.cast(ValueType.Float);
          if (floatValue != null) {
            return floatValue.getValue();
          }
        } catch (Exception e) {
          logger.error(String.format("Failed to cast %s value into float", variableName), e);
        }
      } else {
        if (logger.isErrorEnabled()) {
          logger.error(String.format("Expected %s to be float but was %s", variableName, variableValue.getValueType()));
        }
      }
    }
    
    return null;
  }
  
  /**
   * Returns variable value as float or null if variable is not set or is not a float
   * 
   * @param story story
   * @param variableName variable name
   * @return variable value as float or null if variable is not set or is not a float
   */
  protected Float getVariableFloat(Story story, String variableName) {
    Value<?> variableValue = getVariableValue(story, variableName);
    if (variableValue != null) {
      if (variableValue.getValueType() == ValueType.Float) {
        return (Float) variableValue.getValue();
      } else {
        if (logger.isErrorEnabled()) {
          logger.error(String.format("Expected %s to be float but was %s", variableName, variableValue.getValueType()));
        }
      }
    }
    
    return null;
  }
  
  /**
   * Returns variable value as float or null if variable is not set or is not a float
   * 
   * @param story story
   * @param variableName variable name
   * @return variable value as float or null if variable is not set or is not a float
   */
  protected String getVariableString(Story story, String variableName) {
    Value<?> variableValue = getVariableValue(story, variableName);
    if (variableValue != null) {
      if (variableValue.getValueType() == ValueType.String) {
        return (String) variableValue.getValue();
      } else {
        try {
          @SuppressWarnings("unchecked") Value<String> stringValue = (Value<String>) variableValue.cast(ValueType.String);
          if (stringValue != null) {
            return stringValue.getValue();
          }
        } catch (Exception e) {
          if (logger.isErrorEnabled()) {
            logger.error(String.format("Failed to cast %s into String", variableName), e);
          }
        }
      }
    }
    
    return null;
  }

  /**
   * Sets value into a variable
   * 
   * @param story story
   * @param variableName variable name
   * @param value value
   */
  protected void setVariable(Story story, String variableName, Object value) {
    try {
      VariableAssignment variableAssignment = new VariableAssignment(variableName, false);
      VariablesState variablesState = story.getVariablesState();
      variablesState.assign(variableAssignment, AbstractValue.create(value));
    } catch (Exception e) {
      logger.error("Failed to assign variable", e);
    }
  }
  
  private Value<?> getVariableValue(Story story, String variableName) {
    VariablesState variablesState = story.getVariablesState();
    
    try {
      RTObject rtObject = variablesState.getVariableWithName(variableName);
      if (rtObject instanceof Value<?>) {
        return (Value<?>) rtObject;
      }
    } catch (Exception e) {
      logger.error("Could not get variable state", e);
    }
    
    return null;
  }
  
}
