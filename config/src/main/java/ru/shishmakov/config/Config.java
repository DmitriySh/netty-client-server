package ru.shishmakov.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.URL;

/**
 * Configuration class for Client and Server classes.
 *
 * @author Dmitriy Shishmakov
 */
public class Config extends XMLConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles
            .lookup().lookupClass());
    private static final String CONFIG_XML = "/config.xml";

    private static volatile Config instance;

    private Config() throws ConfigurationException {
        final URL configResource = this.getClass().getResource(CONFIG_XML);
        this.load(configResource);
        logger.warn("Configuration loaded from {}", configResource);
    }

    public static Config getInstance() throws ConfigurationException {
        Config result = instance;
        if (result == null) {
            synchronized (Config.class) {
                result = instance;
                if (result == null) {
                    logger.warn("Initialise configuration ...");
                    instance = result = new Config();
                }
            }
        }
        return result;
    }
}
