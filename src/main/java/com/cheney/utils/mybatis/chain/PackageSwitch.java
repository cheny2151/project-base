package com.cheney.utils.mybatis.chain;

public class PackageSwitch implements Switch {

    private Switch next;

    private final static String target = "@\\{package}";

    public PackageSwitch(Switch next) {
        this.next = next;
    }

    @Override
    public String replaceAll(String t, String fullPath) {
        t = t.replaceAll(target, "package");
        return next == null ? t : next.replaceAll(t, fullPath);
    }

    @Override
    public Switch next() {
        return next;
    }

}
