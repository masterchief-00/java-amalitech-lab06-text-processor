package com.kwizera.javaamalitechlab06textprocessor.Exceptions;

import com.kwizera.javaamalitechlab06textprocessor.utils.CustomLogger;

public class InvalidFilenameException extends Exception {
    public InvalidFilenameException(String message) {
        super(message);
        CustomLogger.log(CustomLogger.LogLevel.ERROR, message);
    }
}
