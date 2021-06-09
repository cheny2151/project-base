package com.cheney.mybatis.chain;

import java.io.File;

public abstract class AbstractSwitch implements Switch {

    public final static String PRE = "#{";

    public final static String END = "}";

    public final static String SEPARATOR = ",";

    public final static String EQUAL_TO = " = ";

    private final static char UNDERLINE = '_';

    public final static String LINE_BREAK = System.getProperty("line.separator");

    private Switch next;

    private String target;

    public AbstractSwitch() {
    }

    public AbstractSwitch(Switch next, String target) {
        this.next = next;
        this.target = target;
    }

    @Override
    public String replaceAll(String t, Class clazz) {
        t = t.replaceAll(target, getReplacement(clazz));
        return next == null ? t : next.replaceAll(t, clazz);
    }

    public abstract String getReplacement(Class clazz);

    public Switch next() {
        return next;
    }

    public void setNext(Switch next) {
        this.next = next;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    String placeholder(String name) {
        return PRE + name + END;
    }

    String toJavaPackage(String pack) {
        String javaPage = pack.replaceAll(File.separator, ".");
        if (javaPage.contains("src.test.java") || javaPage.contains("src.main.java")) {
            return javaPage.substring(14);
        }
        return null;
    }

    private char[] resize(char[] chars) {
        char[] newChars = new char[chars.length + 3];
        System.arraycopy(chars, 0, newChars, 0, chars.length);
        return newChars;
    }

}
