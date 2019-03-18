package com.cheney.mybatis.chain;

import com.cheney.mybatis.XMLGenerator;

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
