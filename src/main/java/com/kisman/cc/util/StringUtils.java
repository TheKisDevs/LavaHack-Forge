package com.kisman.cc.util;

public class StringUtils {
    public static String floatRegex = "^[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)$";

    public static CharSequence merge(CharSequence[] sequences, int off, int len){
        StringBuilder sb = new StringBuilder(sequences.length * 8);
        int max = Math.min(sequences.length, len);
        for(int i = off; i < max; i++){
            sb.append(sequences[i]);
            if(i < max - 1)
                sb.append(' ');
        }
        return sb;
    }

    public static int stringToInt(String text) {
        int result = -1;

        for (char c : text.toCharArray()) result -= c;

        return result;
    }

    public static Double parseNumber(String string, double prev) {
        if(string.matches(floatRegex)) {
            try {
                return Double.parseDouble(string);
            } catch(Exception ignored) { }
        }

        return prev;
    }
}