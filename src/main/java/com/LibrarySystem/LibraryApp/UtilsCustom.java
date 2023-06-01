package com.LibrarySystem.LibraryApp;

import java.util.regex.Pattern;

public class UtilsCustom {
    
    public static boolean isNumeric(String string) {
        // Checks if the provided string
        // is a numeric by applying a regular
        // expression on it.
        String regex = "[0-9]+[\\.]?[0-9]*";
        return Pattern.matches(regex, string);
    }

    static final String numberMatchRegex = "\\d+";

    public static Long tryParseNumberL(String numberStr) throws NullPointerException, NumberFormatException
    {
        long number;

        if (numberStr == null)
            throw new NullPointerException("Given string is null");

        if (!UtilsCustom.isNumeric(numberStr))
            throw new NumberFormatException("Given string is not appropriate to convert number");

        try {
            number = Long.parseLong(numberStr);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Given string is not appropriate to convert number");
        }

        return number;
    }

    public static Double tryParseNumberD(String numberStr) throws NullPointerException, NumberFormatException
    {
        double number;

        if (numberStr == null)
            throw new NullPointerException("Given string is null");

        if (!UtilsCustom.isNumeric(numberStr))
            throw new NumberFormatException("Given string is appropriate to convert number");

        try {
            number = Long.parseLong(numberStr);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Given string is appropriate to convert number");
        }

        return number;
    } 
}
