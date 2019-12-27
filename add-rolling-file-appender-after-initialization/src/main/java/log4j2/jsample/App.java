package log4j2.jsample;

import log4j2.jsample.config.DynamicConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Name of the log configuration file must be log4j2.xml and It should be
 * present in the classpath of the program.
 */

public class App {
	private static final Logger LOGGER = LogManager.getLogger(App.class);

	public static void main(String[] args) {

		for (int i = 0; i < 10; i++) {
			if (i % 2 == 0)
				LOGGER.info("It is info log -  {}", i);
			else
				LOGGER.warn("It is warn log - {} ", i);
		}
		
		DynamicConfig.configureNewAppender("./log/");
		
		LOGGER.error("It is error log");
	}
}