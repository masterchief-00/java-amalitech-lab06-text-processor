package com.kwizera.javaamalitechlab06textprocessor.Exceptions;

import com.kwizera.javaamalitechlab06textprocessor.utils.CustomLogger;

public class InvalidActiveDirectoryException extends Exception {
    public InvalidActiveDirectoryException(String message) {
        super(message);
        CustomLogger.log(CustomLogger.LogLevel.ERROR, message);
    }
}
