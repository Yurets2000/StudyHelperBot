package com.yube.utils;

import com.yube.exceptions.ConfigurationException;

import java.io.IOException;
import java.util.Properties;

public final class PropertiesHandler {

    public static Properties getProperties(String path) throws ConfigurationException {
        Properties props = new Properties();
        try{
            props.load(PropertiesHandler.class.getResourceAsStream("/" + path));
        } catch (IOException e) {
            throw new ConfigurationException("Couldn't retrieve configuration properties", e);
        }
        return props;
    }
}
