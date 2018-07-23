package com.cheney.utils.mybatis.chain;

public class ColumnValuesSwitch implements Switch {

    private Switch next;

    private final static String target = "@\\{columnValues}";

    public ColumnValuesSwitch(Switch next) {
        this.next = next;
    }

    @Override
    public String replaceAll(String t, String fullPath) {
        t = t.replaceAll(target, "columnValues");
        return next == null ? t : next.replaceAll(t, fullPath);
    }

    @Override
    public Switch next() {
        return next;
    }
}
