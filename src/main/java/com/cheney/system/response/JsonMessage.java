package com.cheney.system.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * 便于返回指定属性json的工具类
 * Created by cheny on 2017/7/24.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JsonMessage implements Serializable {

    private static final long serialVersionUID = 4268904972338027637L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 存放数据
     */
    private Object data;

    /**
     * 存放错误信息
     */
    private String msg;

    /**
     * 禁止手动实例化
     */
    private JsonMessage(int code, Object data) {
        this.code = code;
        if (code == ResponseCode.SUCCESS.getStatus()) {
            this.data = data;
            this.msg = ResponseCode.SUCCESS.getMsg();
        } else {
            this.msg = (String) data;
        }
    }

    /**
     * 用于返回错误信息
     */
    public static JsonMessage error(int code, String message) {
        return new JsonMessage(code, message);
    }

    /**
     * 用于返回错误信息
     */
    public static JsonMessage error(String message) {
        return new JsonMessage(ResponseCode.ERROR.getStatus(), message);
    }

    /**
     * 用于返回错误信息
     */
    public static JsonMessage error(ResponseCode responseCode) {
        return new JsonMessage(responseCode.getStatus(), responseCode.getMsg());
    }

    /**
     * 用于成功时返回数据:{"name":data}
     *
     * @param args 返回的主数据
     */
    public static JsonMessage success(Object... args) {
        //单一数据
        if (args.length == 1) {
            return new JsonMessage(ResponseCode.SUCCESS.getStatus(), args[0]);
        } else {
            if ((args.length & 1) == 1) {
                throw new IllegalArgumentException("The args must be even");
            }
            //多数据
            HashMap<String, Object> map = new HashMap<>();
            for (int i = 0; i < args.length; i++) {
                map.put((String) args[i], args[++i]);
            }
            return new JsonMessage(ResponseCode.SUCCESS.getStatus(), map);
        }
    }

    /**
     * 解析一切对象成map，便于返回json数据;
     * 用$可以同时取出集合中的多个属性 例:List.id$name
     * 不能分开List.id,List.name取;会报错,因为集合时存的list，普通对象时存的map。
     *
     * @param bean       被解析的对象（可以是对象或者集合）
     * @param properties 可选的属性，支持级联 xx.xx.xx
     * @return map
     */
    public static Object extract(Object bean, String... properties) {
        if (bean == null) {
            return null;
        }
        HashMap<String, Object> map = new HashMap<>();
        //解析list
        if (bean instanceof Collection) {
            return getMessageList((Collection) bean, properties);
        }
        for (String property : properties) {
            HashMap<String, Object> tem = map;
            HashMap<String, Object> field;
            String[] strings = property.split("\\.");
            StringBuilder prefix = new StringBuilder();
            for (int i = 0; i < strings.length; i++) {
                if (i != strings.length - 1) {
                    field = (HashMap<String, Object>) tem.get(strings[i]);
                    if (field == null) {
                        field = new HashMap<>();
                        tem.put(strings[i], field);
                    }
                    prefix.append(strings[i]);
                    //获取此字段对应的对象
                    Object object = getPropertyObject(bean, prefix.toString());
//                Class type = getPropertyType(bean,prefix.toString());
                    prefix.append(".");
                    if (object == null) {
                        tem.put(strings[i], null);
                        break;
                    }
                    if (object instanceof Collection) {
                        //解析list，将list前的字段名置空
                        String suffix = property.replace(prefix.toString(), "");
                        tem.put(strings[i], getMessageList((Collection) object, suffix.split("\\$")));
                        break;
                    }
                    //进入下一层
                    tem = field;
                } else {
                    //最后一个字段
                    tem.put(strings[i], getPropertyObject(bean, property));
                }
            }
        }
        return map;
    }

    private static List getMessageList(Collection beans, String... properties) {
        ArrayList<Object> list = new ArrayList<>();
        if (beans != null && properties.length > 0) {
            for (Object bean : beans) {
                list.add(extract(bean, properties));
            }
        }
        return list;
    }

    /**
     * 获得对应字段的对象
     */
    private static Object getPropertyObject(Object o, String property) {
        String[] names = property.split("\\.");
        Method method;
        try {
            for (String name : names) {
                method = o.getClass().getMethod("get" + toUpperCaseFirstOne(name));
                o = method.invoke(o);
            }
        } catch (Exception e) {
            return null;
        }
        return o;
    }

    /**
     * 获得对应字段的class
     */
    private static Class getPropertyType(Object o, String property) {
        try {
            Method method = null;
            String[] names = property.split("\\.");
            for (String name : names) {
                method = o.getClass().getMethod("get" + toUpperCaseFirstOne(name));
                o = method.invoke(o);
            }
            return method == null ? null : method.getReturnType();
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 第一个字母变大写
     */
    private static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0))) {
            return s;
        } else {
            return Character.toUpperCase(s.charAt(0)) + s.substring(1);
        }
    }

}
