package com.cheney.utils;

import com.cheney.exception.JsonParseException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
                .setSerializationInclusion(JsonInclude.Include.NON_NULL).registerModule((new SimpleModule())
                        .addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
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
