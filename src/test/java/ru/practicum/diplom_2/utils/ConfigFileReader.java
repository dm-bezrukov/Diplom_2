package ru.practicum.diplom_2.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ConfigFileReader {
    private final Properties properties;

    public ConfigFileReader() {
        BufferedReader reader;
        String propertyFilePath = "src/test/java/application.properties";
        try {
            reader = new BufferedReader(new FileReader(propertyFilePath));
            properties = new Properties();
            try {
                properties.load(reader);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Configuration.properties not found at " + propertyFilePath);
        }
    }

    public String getApplicationUrl() {
        String url = properties.getProperty("test.application.uri");
        if(url != null) return url;
        else throw new RuntimeException("url not specified in the Configuration.properties file.");
    }

    public String getTestUserEmail() {
        String email = properties.getProperty("test.user.email");
        if (email != null) return email;
        else throw new RuntimeException("email not specified in the Configuration.properties file.");
    }

    public String getTestUserPassword() {
        String password = properties.getProperty("test.user.password");
        if (password != null) return password;
        else throw new RuntimeException("password not specified in the Configuration.properties file.");
    }

    public String getTestUserName() {
        String name = properties.getProperty("test.user.name");
        if (name != null) return name;
        else throw new RuntimeException("name not specified in the Configuration.properties file.");
    }
}
