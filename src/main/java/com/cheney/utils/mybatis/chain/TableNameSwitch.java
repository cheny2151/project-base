package com.cheney.utils.mybatis.chain;

import com.cheney.utils.mybatis.XMLGenerator;

public class TableNameSwitch extends AbstractSwitch {

    private final static String target = "@\\{tableName}";

    public TableNameSwitch(Switch next) {
        super(next, target);
    }

    @Override
    public String getReplacement(Class clazz) {
        return XMLGenerator.TABLE_NAME;
    }

}
