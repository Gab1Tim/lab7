package common.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfig {
    private final Properties properties = new Properties();

    public AppConfig(String fileName) {
        try (FileInputStream fis = new FileInputStream(fileName)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Could not load config file: " + fileName, e);
        }
    }

    public String getString(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing config key: " + key);
        }
        return value;
    }

    public int getInt(String key) {
        return Integer.parseInt(getString(key));
    }
}