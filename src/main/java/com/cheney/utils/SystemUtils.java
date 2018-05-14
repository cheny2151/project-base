package com.cheney.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.cheney.exception.PropertyNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 读取系统设置
 */
public class SystemUtils {

    private final static Logger LOGGER = Logger.getLogger(SystemUtils.class);

    private static HashMap<String, Object> systemValue;

    static {
        systemValue = new HashMap<>();
        try {
            JsonNode jsonNode = JsonUtils.readTree(new ClassPathResource("/system.json").getFile());
            Iterator<String> stringIterator = jsonNode.fieldNames();
            JsonNode node;
            String nodeName;
            Object nodeValue;
            while (stringIterator.hasNext()) {
                nodeName = stringIterator.next();
                node = jsonNode.get(nodeName);
                nodeValue = readValue(node);
                systemValue.put(nodeName, nodeValue);
            }
        } catch (IOException e) {
            LOGGER.error("文件类型工具类初始化失败", e);
        }
    }

    /**
     * 读取json节点值
     */
    private static Object readValue(JsonNode node) {
        Object value;
        if (node.isArray()) {
            value = asArray(node);
        } else if (node.isInt()) {
            value = node.asInt();
        } else if (node.isBoolean()) {
            value = node.asBoolean();
        } else if (node.isDouble()) {
            value = node.asDouble();
        } else if (node.isNull()) {
            value = null;
        } else {
            value = node.asText();
        }
        return value;
    }

    /**
     * 解析数组
     */
    private static Object[] asArray(JsonNode jsonNode) {
        Iterator<JsonNode> iterator = jsonNode.iterator();
        Object[] objects = new Object[jsonNode.size()];
        int i = 0;
        while (iterator.hasNext()) {
            objects[i] = iterator.next();
            i++;
        }
        return objects;
    }

    /**
     * 获取系统设置
     */
    public static String getValue(String name) {
        Object val = systemValue.get(name);
        return val == null ? null : val.toString();
    }

    public static String getRequireValue(String name) {
        Object val = systemValue.get(name);
        if (val == null) throw new PropertyNotFoundException("require property \"" + name + "\" not found");
        return val.toString();
    }

    public static int getIntValue(String name) {
        return (int) systemValue.get(name);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(String name, Class<T> tClass) {
        return (T) systemValue.get(name);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(String name, Class<T> tClass, T defaultValue) {
        T value = (T) systemValue.get(name);
        return value == null ? defaultValue : value;
    }

    /**
     * 获取协议+站点ip/域名+端口
     */
    public static String getSite() {
        return getRequireValue("protocols") + "://" +
                (getValue("domainName") == null ? getRequireValue("ip") : getValue("domainName")) +
                (getIntValue("port") == 80 ? "" : ":" + getValue("port"));
    }

    /**
     * 获取静态资源目录
     */
    public static String getStatic() {
        return getRequireValue("static");
    }

}
