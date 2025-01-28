package com.example.lunaproject.global.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobalMethods {
    public static String getUUID64(String url) {
        String regex = "([A-Z0-9]{64})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url.toUpperCase());

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new IllegalArgumentException("UUID를 찾을 수 없습니다.");
        }
    }
    public static boolean isSameUUID(String url1, String url2) {
        if (url1 == null || url2 == null) {
            return false;
        }
        return getUUID64(url1).equals(getUUID64(url2));
    }
}
