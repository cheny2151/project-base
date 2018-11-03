package com.cheney.utils;

import com.cheney.exception.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Json工具类
 */
@Slf4j
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
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new JsonParseException();
        }
    }

    public static String toJson(Object obj) {
        try {
            return objectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new JsonParseException();
        }
    }

    public static JsonNode readTree(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("json file is null");
        }
        return objectMapper().readTree(file);
    }

    public static Map<String, ?> object2Map(Object obj) {
        return toObject(toJson(obj), Map.class);
    }

    public static <V> V map2Object(Map<String, Object> map, Class<V> clazz) {
        return toObject(toJson(map), clazz);
    }

}
