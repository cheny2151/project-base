package com.cheney.utils.mybatis.chain;

public class TableNameSwitch implements Switch {

    private Switch next;

    private final static String target = "@\\{tableName}";

    public TableNameSwitch(Switch next) {
        this.next = next;
    }

    @Override
    public String replaceAll(String t, String fullPath) {
        t = t.replaceAll(target, "tableName");
        return next == null ? t : next.replaceAll(t, fullPath);
    }

    @Override
    public Switch next() {
        return next;
    }
}
