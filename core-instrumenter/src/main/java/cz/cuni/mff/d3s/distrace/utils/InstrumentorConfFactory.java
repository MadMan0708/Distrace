package cz.cuni.mff.d3s.distrace.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import java.io.File;
import java.net.URI;

/**
 * This class is used to create logger configuration at runtime
 */
public class InstrumentorConfFactory extends ConfigurationFactory {

    private Level logLevel;
    private String logDir;

    public InstrumentorConfFactory(String logLevel, String logDir) {
        this.logLevel = determineLogLevel(logLevel);
        this.logDir = logDir;
    }

    private Level determineLogLevel(String logLevel) {
        switch (logLevel) {
            case "trace": return Level.TRACE;
            case "debug": return Level.DEBUG;
            case "info": return Level.INFO;
            case "warn": return Level.WARN;
            case "error": return Level.ERROR;
            case "fatal": return Level.FATAL;
            case "off": return Level.OFF;
            default:
                // no other logger type is supported
                assert false;
        }
        return Level.ERROR;
    }

    private Configuration createConfiguration(final String name, ConfigurationBuilder<BuiltConfiguration> builder) {
        builder.setConfigurationName(name);
        builder.setStatusLevel(logLevel);

        LayoutComponentBuilder layoutComponentBuilder = builder.newLayout("PatternLayout").
                addAttribute("pattern", "[%d{yyyy-mm-dd HH:mm:ss.SSS}] [%C] [%level{lowerCase=true}] %msg%n%throwable");
        // console appender
        AppenderComponentBuilder consoleAppenderBuilder = builder.newAppender("console", "CONSOLE").
                addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
        consoleAppenderBuilder.add(layoutComponentBuilder);
        builder.add(consoleAppenderBuilder);

        // file appender
        AppenderComponentBuilder fileAppenderBuilder = builder.newAppender("file", "FILE").
                addAttribute("fileName", logDir + File.separator + "instrumentor.log");
        fileAppenderBuilder.add(layoutComponentBuilder);
        builder.add(fileAppenderBuilder);

        builder.add(builder.newRootLogger(logLevel).
                add(builder.newAppenderRef("console")).
                add(builder.newAppenderRef("file")).
                addAttribute("additivity", false));

        return builder.build();
    }

    @Override
    protected String[] getSupportedTypes() {
        return new String[]{"*"};
    }

    @Override
    public Configuration getConfiguration(ConfigurationSource source) {
        return getConfiguration(source.toString(), null);
    }

    @Override
    public Configuration getConfiguration(String name, URI configLocation) {
        ConfigurationBuilder<BuiltConfiguration> builder = newConfigurationBuilder();
        return createConfiguration(name, builder);
    }


}
