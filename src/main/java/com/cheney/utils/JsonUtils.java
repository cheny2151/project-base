package com.cheney.utils;

import com.cheney.exception.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.boot.jackson.JsonComponentModule;

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
        private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper()
                .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false)
                .registerModules(new JavaTimeModule(),
//                        add if in jdk8 new Jdk8Module(),
                        new JsonComponentModule(), new ParameterNamesModule())
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    }

    public static ObjectMapper objectMapper() {
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

    public static byte[] toJsonBytes(Object obj) {
        try {
            return objectMapper().writeValueAsBytes(obj);
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
