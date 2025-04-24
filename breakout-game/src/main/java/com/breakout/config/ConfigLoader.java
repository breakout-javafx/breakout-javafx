package com.breakout.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static ConfigLoader instance;
    private final Properties properties = new Properties();

    private ConfigLoader() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("No se encontró config.properties en el classpath");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar config.properties", e);
        }
    }

    public static ConfigLoader getInstance() {
        if (instance == null) {
            synchronized (ConfigLoader.class) {
                if (instance == null) {
                    instance = new ConfigLoader();
                }
            }
        }
        return instance;
    }

    public int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    public double getDouble(String key) {
        return Double.parseDouble(properties.getProperty(key));
    }

    public String get(String key) {
        return properties.getProperty(key);
    }
}

