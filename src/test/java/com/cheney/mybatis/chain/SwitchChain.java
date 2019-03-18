package com.cheney.mybatis.chain;

public class SwitchChain {

    private static Switch start;

    static {
        start = new ColumnValuesSwitch(
                new IdColumnSwitch(
                        new IdTypeSwitch(
                                new PackageSwitch(
                                        new SetValueSwitch(
                                                new TableNameSwitch(
                                                        new ColumnsSwitch(
                                                                new NamespaceSwitch(
                                                                        new ResultMapSwitch(
                                                                                   new LowSimpleClassNameSwitch(
                                                                                           new SimpleClassNameSwitch(
                                                                                                   new DaoPackageSwitch(
                                                                                                           new IdSimpleClassNameSwitch(
                                                                                                                   new ServicePackageSwitch(
                                                                                                                           new ServiceImportImplSwitch(
                                                                                                                                   new IdFieldSwitch(
                                                                                                                                           null
                                                                                                                                   ))))))))))))))));
    }

    public static String replaceAll(String toReplace, Class clazz) {
        return start.replaceAll(toReplace, clazz);
    }

}
