package com.cheney.utils.mybatis.chain;

public abstract class AbstractSwitch implements Switch {

    public final static String PRE = "#{";

    public final static String END = "}";

    public final static String SEPARATOR = ",";

    public final static String EQUAL_TO = " = ";

    public final static String LINE_BREAK = System.getProperty("line.separator");

    private final static char UNDERLINE = "_".toCharArray()[0];

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

    String underline(String name) {
        char[] chars = name.toCharArray();
        int len = chars.length + 3;
        char[] newChars = new char[len];
        int j = 0;
        for (int i = 0; i < chars.length; i++, j++) {
            char c = chars[i];
            if (c >= 65 && c <= 90) {
                if (j == len) {
                    newChars = resize(newChars);
                    len = newChars.length;
                }
                newChars[j++] = UNDERLINE;
                c = (char) (c + 32);
            }
            newChars[j] = c;
        }
        return new String(newChars, 0, j);
    }

    String toJavaPackage(String pack) {
        return pack.replaceAll("\\\\", ".").replaceAll("src.main.java.", "");
    }

    private char[] resize(char[] chars) {
        char[] newChars = new char[chars.length + 3];
        System.arraycopy(chars, 0, newChars, 0, chars.length);
        return newChars;
    }

}
