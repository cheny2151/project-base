package com.cheney.utils.tree;

import lombok.Data;

import java.util.Collection;

/**
 * @author cheney
 * @date 2019/4/23
 */
@Data
public class TreeType<T> {

    public final static String CODE_SEPARATOR = ",";

    private String code;

    private String codeSequence;

    private T parent;

    private Collection<T> children;

}
