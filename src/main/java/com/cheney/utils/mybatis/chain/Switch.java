package com.cheney.utils.mybatis.chain;

public interface Switch {

    String replaceAll(String t,String fullPath);

    Switch next();

}
