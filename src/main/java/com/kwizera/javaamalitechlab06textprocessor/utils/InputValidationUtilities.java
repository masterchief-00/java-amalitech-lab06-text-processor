package com.kwizera.javaamalitechlab06textprocessor.utils;

public class InputValidationUtilities {
    public boolean invalidFileName(String names) {
        return (!names.matches("[A-Za-z0-9 ]*") || names.length() < 2);
    }
}
