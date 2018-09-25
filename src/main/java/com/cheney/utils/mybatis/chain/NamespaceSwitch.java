package com.cheney.utils.mybatis.chain;

import com.cheney.utils.mybatis.XMLGenerator;

public class NamespaceSwitch extends AbstractSwitch {

    private static final String target = "@\\{namespace}";

    public NamespaceSwitch() {
    }

    public NamespaceSwitch(Switch next) {
        super(next, target);
    }

    @Override
    public String getReplacement(Class clazz) {
        return toJavaPackage(XMLGenerator.getGeneratorFilePackage().get("Dao")) + "." + clazz.getSimpleName() + XMLGenerator.END;
    }

}
