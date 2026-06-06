package utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties;
    private static final String PROPERTY_FILE_PATH = "src/test/resources/config.properties";

    static {
        try (FileInputStream fileInputStream = new FileInputStream(PROPERTY_FILE_PATH)) {
            properties = new Properties();
            properties.load(fileInputStream);
        } catch (IOException e) {
            System.out.println("Critical Exception: Failed to load environment properties configuration mapping file at " + PROPERTY_FILE_PATH);
            e.printStackTrace();
            throw new RuntimeException("Configuration file initialization aborted due to critical I/O stream failure.");
        }
    }

    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            System.out.println("Warning: Requested configuration mapping key '" + key + "' was not found inside your properties structure.");
        }
        return value;
    }

    public static String getUrl() {
        return getProperty("url");
    }

    public static String getBrowser() {
        return getProperty("browser");
    }

    public static int getExplicitWaitTimeout() {
        return Integer.parseInt(getProperty("explicitWait"));
    }
}