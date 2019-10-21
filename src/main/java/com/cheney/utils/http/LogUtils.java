package com.cheney.utils.http;

import com.alibaba.fastjson.JSON;

/**
 * @author cheney
 * @date 2019-10-21
 */
public class LogUtils {

    public static String cutLog(Object obj) {
        String s = JSON.toJSONString(obj);
        return cut(s, 300);
    }

    public static String cut(String str, int length) {
        return str.length() <= length ? str : str.substring(0, length) + "...";
    }

}
