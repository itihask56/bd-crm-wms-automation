package com.emoha.crm.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigReader {

    private static final Logger logger =
            LoggerFactory.getLogger(ConfigReader.class);

    private static Properties properties;

    static {

        try {

            FileInputStream fis =
                    new FileInputStream("src/test/resources/config.properties");

            properties = new Properties();

            properties.load(fis);
            logger.info("Loaded test configuration from src/test/resources/config.properties");

        } catch (IOException e) {

            logger.error("Unable to load test configuration", e);
        }
    }

    public static String getProperty(String key) {

        return properties.getProperty(key);
    }
}
