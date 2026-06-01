package com.emoha.crm.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Logger logger =
            LoggerFactory.getLogger(ConfigReader.class);

    private static final Properties properties = new Properties();

    static {
        loadRequiredClasspathProperties("config.properties");
        loadOptionalClasspathProperties("config.local.properties");
        loadOptionalFileProperties("src/test/resources/config.local.properties");
    }

    private static void loadRequiredClasspathProperties(String fileName) {

        try (InputStream inputStream =
                     ConfigReader.class
                             .getClassLoader()
                             .getResourceAsStream(fileName)) {

            if (inputStream == null) {
                throw new IllegalStateException(
                        fileName + " not found in test resources"
                );
            }

            properties.load(inputStream);
            logger.info("Loaded required configuration from classpath: {}", fileName);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static void loadOptionalClasspathProperties(String fileName) {

        try (InputStream inputStream =
                     ConfigReader.class
                             .getClassLoader()
                             .getResourceAsStream(fileName)) {

            if (inputStream != null) {
                properties.load(inputStream);
                logger.info("Loaded optional configuration from classpath: {}", fileName);
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static void loadOptionalFileProperties(String filePath) {

        try (InputStream inputStream = new FileInputStream(filePath)) {

            properties.load(inputStream);
            logger.info("Loaded optional local configuration file: {}", filePath);
        } catch (IOException ignored) {
            logger.debug("Optional local configuration file not found: {}", filePath);
        }
    }

    public static String getProperty(String key) {
        String systemValue = System.getProperty(key);

        if (hasText(systemValue)) {
            return systemValue;
        }

        String envValue = System.getenv(toEnvironmentKey(key));

        if (hasText(envValue)) {
            return envValue;
        }

        String fileValue = properties.getProperty(key);

        if (hasText(fileValue)) {
            return fileValue;
        }

        throw new IllegalArgumentException(
                "Missing configuration value for '"
                        + key
                        + "'. Provide it with -D"
                        + key
                        + "=value or environment variable "
                        + toEnvironmentKey(key)
                        + ", or add it to ignored file "
                        + "src/test/resources/config.local.properties"
        );
    }

    public static String getOptionalProperty(String key) {

        String systemValue = System.getProperty(key);

        if (hasText(systemValue)) {
            return systemValue;
        }

        String envValue = System.getenv(toEnvironmentKey(key));

        if (hasText(envValue)) {
            return envValue;
        }

        return properties.getProperty(key, "");
    }

    private static boolean hasText(String value) {

        return value != null && !value.trim().isEmpty();
    }

    private static String toEnvironmentKey(String key) {

        return key
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .replaceAll("[^A-Za-z0-9]", "_")
                .toUpperCase();
    }
}
