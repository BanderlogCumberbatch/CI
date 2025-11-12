package org.helpers;

import lombok.Getter;
import java.io.InputStream;
import java.util.Properties;

@Getter
public final class PropertyProvider {

    /**
     * Текущий экземпляр класса.
     */
    private static PropertyProvider instance;

    /**
     * Класс для загрузки properties.
     */
    private final Properties properties = new Properties();

    /**
     * Загрузить .properties-файл, если он уже не загружен.
     * @return текущий экземпляр класса
     */
    public static PropertyProvider getInstance() {
        if (instance == null) {
            instance = new PropertyProvider();
        }
        return instance;
    }

    /**
     * Загрузка .properties-файла.
     */
    private PropertyProvider() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("env_local.properties")) {
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load properties file", e);
        }
    }

    /**
     * Загрузить параметр из .properties-файла.
     * @param key ключ, для нахождения параметра
     * @return параметр, найденный по ключю
     */
    public String getProperty(final String key) {
        return properties.getProperty(key);
    }
}

