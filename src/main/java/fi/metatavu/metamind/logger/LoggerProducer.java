package fi.metatavu.metamind.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * Logger producer
 * 
 * @author Antti Leppä
 */
@Dependent
public class LoggerProducer {

  /**
   * Produces new logger instance
   * 
   * @param injectionPoint injection point
   * @return new logger instance
   */
	@Produces
	public Logger produceLog(InjectionPoint injectionPoint) {
	  return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
	}
	
}
