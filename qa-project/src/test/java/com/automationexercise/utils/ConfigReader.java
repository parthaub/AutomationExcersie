package com.automationexercise.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigReader.java
 *
 * PURPOSE:
 *   Reads values from config.properties so we never hardcode
 *   URLs, browser names, or timeouts directly in test code.
 *
 * HOW IT WORKS:
 *   - The Properties class is a built-in Java class for reading .properties files
 *   - We load the file ONCE when the class is first used (static block)
 *   - After that, any class can call ConfigReader.getProperty("key")
 *
 * BEGINNER TIP:
 *   A static {} block runs ONCE when the class is first loaded by Java.
 *   Think of it as "setup code that runs before anything else".
 */
public class ConfigReader {

    // Properties object stores key=value pairs from the file
    // Example: base.url=https://automationexercise.com
    private static Properties properties = new Properties();

    // Static initializer block — runs once when ConfigReader class is first used
    static {
        try {
            // getResourceAsStream looks for the file in src/test/resources/
            // The "/" at the start means "look from the root of the classpath"
            InputStream inputStream = ConfigReader.class
                    .getResourceAsStream("/config/config.properties");

            // If the file wasn't found, throw an error to let us know
            if (inputStream == null) {
                throw new FileNotFoundException(
                    "config.properties not found in src/test/resources/config/"
                );
            }

            // Load the file contents into our Properties object
            properties.load(inputStream);

            System.out.println("[ConfigReader] config.properties loaded successfully.");

        } catch (IOException e) {
            // If anything goes wrong reading the file, stop everything
            throw new RuntimeException(
                "[ConfigReader] Failed to load config.properties: " + e.getMessage()
            );
        }
    }

    /**
     * Gets a value from config.properties by its key.
     *
     * EXAMPLE USAGE:
     *   String url = ConfigReader.getProperty("base.url");
     *   // url will be "https://automationexercise.com"
     *
     * @param key  the key name (left side of = in the .properties file)
     * @return     the value (right side of =), or null if key not found
     */
    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            System.err.println("[ConfigReader] WARNING: Key '" + key + "' not found in config.properties");
        }
        return value;
    }
}
