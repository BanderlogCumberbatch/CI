package org.helpers;

import lombok.Getter;

import java.io.InputStream;
import java.util.Properties;

public class PropertyProvider {

    private static PropertyProvider instance;

    @Getter
    private final Properties properties = new Properties();

    public static PropertyProvider getInstance() {
        if (instance == null) {
            instance = new PropertyProvider();
        }
        return instance;
    }

    // Загрузка .properties-файла
    private PropertyProvider() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("env_local.properties")) {
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load properties file", e);
        }
    }

    public String getProperty(String key) { return  properties.getProperty(key); }
}
