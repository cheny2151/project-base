package com.cheney.utils.http;

import com.alibaba.fastjson.JSON;
import com.cheney.utils.JsonUtils;

/**
 * @author cheney
 * @date 2019-10-21
 */
public class LogUtils {

    public static String cutLog(Object obj) {
        String baseData;
        try {
            baseData = JsonUtils.toJson(obj);
        } catch (Exception e) {
            baseData = JSON.toJSONString(obj);
        }
        return cut(baseData, 300);
    }

    public static String cut(String str, int length) {
        return str.length() <= length ? str : str.substring(0, length) + "...";
    }

}
