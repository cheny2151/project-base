package com.cheney.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

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
            return null;
        }
    }

    public static String toJson(Object obj) {
        try {
            return objectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static JsonNode readTree(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("json file is null");
        }
        return objectMapper().readTree(file);
    }

}
