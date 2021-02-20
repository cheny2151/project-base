package com.cheney.utils;

import com.cheney.exception.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Json工具类
 */
public class JsonUtils {

    private JsonUtils() {
    }

    /**
     * 单例
     */
    private static final class JsonUtilsHolder {
        private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    }

    private static ObjectMapper objectMapper() {
        return JsonUtilsHolder.OBJECT_MAPPER;
    }

    public static <T> T toObject(String json, Class<T> type) {
        try {
            return objectMapper().readValue(json, type);
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }

    public static <T> T toObject(String json, TypeReference<T> type) {
        try {
            return objectMapper().readValue(json, type);
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }

    public static String toJson(Object obj) {
        try {
            return objectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }

    public static JsonNode readTree(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("json file is null");
        }
        return objectMapper().readTree(file);
    }

    public static Map<String, Object> object2Map(Object obj) {
        return toObject(toJson(obj), new TypeReference<>() {
        });
    }

    public static <V> V map2Object(Map<String, Object> map, Class<V> clazz) {
        return toObject(toJson(map), clazz);
    }

}
