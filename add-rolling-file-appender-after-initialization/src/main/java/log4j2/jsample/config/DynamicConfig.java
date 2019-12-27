package log4j2.jsample.config;

import java.util.Map.Entry;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.action.Action;
import org.apache.logging.log4j.core.appender.rolling.action.DeleteAction;
import org.apache.logging.log4j.core.appender.rolling.action.Duration;
import org.apache.logging.log4j.core.appender.rolling.action.IfLastModified;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

public class DynamicConfig {

    public static synchronized void configureNewAppender(String basePath) {

        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();

        Appender appender = createRollingFileAppender(basePath, config);

        config.addAppender(appender);
        appender.start();

        updateLoggers(appender, config);

        ctx.updateLoggers();

    }

    private static void updateLoggers(Appender appender, Configuration config) {
        Level level = null;
        Filter filter = null;
        for (Entry<String, LoggerConfig> entry : config.getLoggers().entrySet()) {
            entry.getValue().addAppender(appender, level, filter);
        }
        config.getRootLogger().addAppender(appender, level, filter);
    }

    private static Appender createRollingFileAppender(String basePath, Configuration config) {
        PatternLayout patternLayout = PatternLayout.newBuilder()
                .withPattern("%d{yyyy-MM-dd HH:mm:ss.SSS}	%level [%t] [%c] [%M] [%l] - %msg%n").build();

        TimeBasedTriggeringPolicy timeBasedTriggeringPolicy = TimeBasedTriggeringPolicy.newBuilder().withInterval(1)
                .withModulate(true).build();

        IfLastModified ifLastModified = IfLastModified.createAgeCondition(Duration.parse("2d"));

        DeleteAction deleteAction = DeleteAction.createDeleteAction(basePath, false, 1, false, null,
                new IfLastModified[]{ifLastModified}, null, config);

        DefaultRolloverStrategy defaultRolloverStrategy = DefaultRolloverStrategy.newBuilder().withConfig(config)
                .withCustomActions(new Action[]{deleteAction}).build();

        return RollingFileAppender.newBuilder().setName("fileAppender")
                .withFileName(basePath + "dynamic_logs.log")
                .withFilePattern(basePath + "dynamic_logs_%d{yyyyMMdd}.log.gz").setLayout(patternLayout)
                .withPolicy(timeBasedTriggeringPolicy).withStrategy(defaultRolloverStrategy).build();

    }
}