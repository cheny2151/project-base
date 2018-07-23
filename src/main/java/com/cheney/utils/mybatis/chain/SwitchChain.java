package com.cheney.utils.mybatis.chain;

public class SwitchChain {

    private static Switch start;

    static {
        start = new ColumnValuesSwitch(
                new IdColumnSwitch(
                        new IdTypeSwitch(
                                new PackageSwitch(
                                        new SetValueSwitch(
                                                new TableNameSwitch(null)
                                        )))));
    }

    public static String replaceAll(String toReplace, String fullPath) {
        return start.replaceAll(toReplace, fullPath);
    }

}
