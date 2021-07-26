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

    /**
     * 为string restTemplate 添加url参数
     *
     * @param url url
     * @param param url参数
     * @return 完成添加参数的url
     */
    public static String addRestTemplateParam(String url, String param) {
        StringBuilder urlBuilder = new StringBuilder(url);
        if (!url.contains("?")) {
            urlBuilder.append("?");
        } else if (!url.endsWith("?")) {
            urlBuilder.append("&");
        }
        urlBuilder.append(param).append("=").append("{").append(param).append("}");
        return urlBuilder.toString();
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

    /**
     * 判断url是否匹配正则
     *
     * @param url 请求url
     * @return matches result
     */
    public static boolean matchesUrl(String url, String[] urlPatterns) {
        if (url == null || urlPatterns.length == 0) {
            return false;
        }
        for (String urlPattern : urlPatterns) {
            urlPattern = fixPattern(urlPattern);
            if (url.matches(urlPattern)) {
                return true;
            }
        }
        return false;
    }

    public static String fixPattern(String pattern) {
        if (!pattern.endsWith("*")) {
            return pattern;
        }
        int length = pattern.length();
        return "**".equals(pattern.substring(length - 2)) ?
                pattern.substring(0, length - 2) + ".*" :
                pattern.substring(0, length - 1) + ".*";
    }

}
