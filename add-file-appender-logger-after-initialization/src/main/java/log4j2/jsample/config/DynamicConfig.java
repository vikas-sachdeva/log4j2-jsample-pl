package log4j2.jsample.config;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

public class DynamicConfig {

    public static synchronized void configureNewAppenderAndLogger(String basePath) {

        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();

        Appender appender = createFileAppender(basePath);

        config.addAppender(appender);
        appender.start();

        addLogger(appender, config);

        ctx.updateLoggers();

    }

    private static void addLogger(Appender appender, Configuration config) {

        AppenderRef ref = AppenderRef.createAppenderRef("fileAppender", null, null);
        AppenderRef[] refs = new AppenderRef[]{ref};

        Level appenderLogLevel = null;
        Filter appenderFilter = null;

        LoggerConfig dynamicLogger = LoggerConfig.createLogger(false, Level.INFO, "log4j2.jsample", null, refs, null,
                config, null);

        dynamicLogger.addAppender(appender, appenderLogLevel, appenderFilter);
        config.addLogger("log4j2.jsample", dynamicLogger);

    }

    private static Appender createFileAppender(String basePath) {
        PatternLayout patternLayout = PatternLayout.newBuilder()
                .withPattern("%d{yyyy-MM-dd HH:mm:ss.SSS}	%level [%t] [%c] [%M] [%l] - %msg%n").build();

        return FileAppender.newBuilder().setName("fileAppender")
                .withFileName(basePath + "dynamic_logs.log").setLayout(patternLayout)
                .build();

    }
}