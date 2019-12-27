package log4j2.jsample.config;

import java.net.URI;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

public class Log4j2ConfigurationFactory extends ConfigurationFactory {

	/**
	 * Supported type of "*" means it will override any configuration file provided.
	 */
	@Override
	protected String[] getSupportedTypes() {
		return new String[] { "*" };
	}

	@Override
	public Configuration getConfiguration(LoggerContext loggerContext, ConfigurationSource source) {
		return getConfiguration(loggerContext, source.toString(), null);
	}

	@Override
	public Configuration getConfiguration(final LoggerContext loggerContext, final String name,
			final URI configLocation) {
		ConfigurationBuilder<BuiltConfiguration> builder = newConfigurationBuilder();
		return createConfiguration(name, builder);
	}

	private static Configuration createConfiguration(String name, ConfigurationBuilder<BuiltConfiguration> builder) {

		builder.setStatusLevel(Level.WARN);
		builder.setConfigurationName("programmatic_configuration");

		builder.addProperty("basePath", "./log/");

		// create a rolling file appender

		// Specify the format of the log file content
		LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout").addAttribute("pattern",
				"%d{yyyy-MM-dd HH:mm:ss.SSS}	%level [%t] [%c] [%M] [%l] - %msg%n");

		// Specify when to rotate log file
		// internal="1" means rotate log file daily
		ComponentBuilder<?> timeBasedTriggeringPolicy = builder.newComponent("TimeBasedTriggeringPolicy")
				.addAttribute("interval", "1").addAttribute("modulate", true);
		ComponentBuilder<?> policies = builder.newComponent("Policies").addComponent(timeBasedTriggeringPolicy);

		// age="2d" means delete log files which are modified before 2 days
		ComponentBuilder<?> ifLastModified = builder.newComponent("IfLastModified").addAttribute("age", "2d");
		ComponentBuilder<?> delete = builder.newComponent("Delete").addAttribute("basePath", "${basePath}")
				.addAttribute("maxDepth", "1").addComponent(ifLastModified);

		ComponentBuilder<?> defaultRolloverStrategy = builder.newComponent("DefaultRolloverStrategy")
				.addComponent(delete);

		// Specify log file path and old log file naming convention.
		AppenderComponentBuilder appenderBuilder = builder.newAppender("fileAppender", RollingFileAppender.PLUGIN_NAME)
				.addAttribute("fileName", "${basePath}app.log")
				.addAttribute("filePattern", "${basePath}app_%d{yyyyMMdd}.log.gz").add(layoutBuilder)
				.addComponent(policies).addComponent(defaultRolloverStrategy);

		builder.add(appenderBuilder);

		// create a console appender
		appenderBuilder = builder.newAppender("consoleAppender", ConsoleAppender.PLUGIN_NAME).addAttribute("target",
				ConsoleAppender.Target.SYSTEM_OUT);

		appenderBuilder.add(builder.newLayout("PatternLayout").addAttribute("pattern",
				"%d{yyyy-MM-dd HH:mm:ss.SSS} %level [%t] [%c] [%M] [%l] - %msg%n"));
		builder.add(appenderBuilder);

		// create the new logger
		builder.add(builder.newLogger("log4j2.jsample", Level.INFO).add(builder.newAppenderRef("fileAppender"))
				.add(builder.newAppenderRef("consoleAppender")).addAttribute("additivity", false));

		builder.add(builder.newRootLogger(Level.ERROR).add(builder.newAppenderRef("fileAppender"))
				.add(builder.newAppenderRef("consoleAppender")).addAttribute("additivity", false));

		return builder.build();

	}
}
