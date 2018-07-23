package com.cheney.utils.mybatis.chain;

public class IdColumnSwitch implements Switch {

    private Switch next;

    private final static String target = "@\\{idColumn}";

    public IdColumnSwitch(Switch next) {
        this.next = next;
    }

    @Override
    public String replaceAll(String t, String fullPath) {
        t = t.replaceAll(target, "idColumn");
        return next == null ? t : next.replaceAll(t, fullPath);
    }

    @Override
    public Switch next() {
        return next;
    }
}
