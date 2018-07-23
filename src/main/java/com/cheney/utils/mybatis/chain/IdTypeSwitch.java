package com.cheney.utils.mybatis.chain;

public class IdTypeSwitch implements Switch {

    private Switch next;

    private final static String target = "@\\{idType}";

    public IdTypeSwitch(Switch next) {
        this.next = next;
    }

    @Override
    public String replaceAll(String t, String fullPath) {
        t = t.replaceAll(target, "idType");
        return next == null ? t : next.replaceAll(t, fullPath);
    }

    @Override
    public Switch next() {
        return next;
    }
}
