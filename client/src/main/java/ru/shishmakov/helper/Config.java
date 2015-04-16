package ru.shishmakov.helper;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URL;

public class Config extends XMLConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles
            .lookup().lookupClass());
    private static final String CONFIG_XML = "/config.xml";

    private static volatile Config instance;

    private Config() throws IOException, ConfigurationException {
        final URL configResource = this.getClass().getResource(CONFIG_XML);
        logger.warn("Configuration loaded from {}", configResource);
        this.load(configResource);
    }

    public static Config getInstance() throws IOException, ConfigurationException {
        Config result = instance;
        if (result == null) {
            synchronized (Config.class) {
                result = instance;
                if (result == null) {
                    instance = result = new Config();
                }
            }
        }
        return result;
    }
}
