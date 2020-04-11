package com.yube.utils;

import org.apache.log4j.Logger;

public class ExceptionHandler {

    private static Logger log = Logger.getLogger(ExceptionHandler.class.getName());

    public static void processException(Exception e) {
        e.printStackTrace();
        //log.error(e.getMessage(), e);
    }
}
