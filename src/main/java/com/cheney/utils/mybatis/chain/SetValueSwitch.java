package com.cheney.utils.mybatis.chain;

public class SetValueSwitch implements Switch {

    private Switch next;

    private final static String target = "@\\{setValue}";

    public SetValueSwitch(Switch next) {
        this.next = next;
    }

    @Override
    public String replaceAll(String t, String fullPath) {
        t = t.replaceAll(target, "setValue");
        return next == null ? t : next.replaceAll(t, fullPath);
    }

    @Override
    public Switch next() {
        return next;
    }
}
