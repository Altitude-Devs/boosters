package com.alttd.boosterapi.util;

public class StringModifier {

    public static String capitalize(String string) {
        if (string.length() <= 1)
            return string.toUpperCase();
        string = string.toLowerCase();
        return string.substring(0, 1).toUpperCase() + string.toLowerCase().substring(1);
    }
}
