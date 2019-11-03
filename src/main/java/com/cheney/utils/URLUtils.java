package com.cheney.utils;

import java.util.HashMap;
import java.util.Map;

public class URLUtils {

    public static Map<String, String> getParamFromURL(String url) {
        String[] split = url.split("\\?");
        if (split.length <= 1) {
            return new HashMap<>();
        }
        String urlParam = split[1];
        urlParam = urlParam.replace("&", "=");
        String[] paramSplit = urlParam.split("=");
        HashMap<String, String> paramMap = new HashMap<>();
        for (int i = 0; i < paramSplit.length; ) {
            paramMap.put(paramSplit[i++], paramSplit[i++]);
        }
        return paramMap;
    }

    public static String addParam(String url, String param) {
        int lastCharIndex = getLastCharIndex(url, "#");
        if (lastCharIndex != -1) {
            if (url.substring(lastCharIndex + 1).contains("?")) {
                return url + "&" + "param";
            } else {
                return url + "?" + param;
            }
        } else if (url.contains("?")) {
            return url + "&" + "param";
        } else {
            return url + "?" + param;
        }
    }

    public static int getLastCharIndex(String src, String chat) {
        int index;
        int lastIndex = -1;
        while ((index = src.indexOf(chat)) != -1) {
            lastIndex = index;
            src = src.substring(index + 1);
        }
        return lastIndex;
    }

}
