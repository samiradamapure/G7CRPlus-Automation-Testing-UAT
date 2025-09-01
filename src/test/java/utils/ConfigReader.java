package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Properties properties = new Properties();

    static {
        try {
            // Read env from JVM system property, fallback to "dev"
            String env = System.getProperty("env", "dev"); // default = dev

            // Build file name based on env
            String fileName = "src/test/resources/config." + env + ".properties";

            try (InputStream fis = new FileInputStream(fileName)) {
                properties.load(fis);
                System.out.println("Loaded config file: " + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load environment-specific config file.");
        }
    }

    public static String get(String key) {
        String systemValue = System.getProperty(key);
        return (systemValue != null && !systemValue.isEmpty())
                ? systemValue
                : properties.getProperty(key);
    }
}
