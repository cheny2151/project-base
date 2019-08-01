package com.cheney.utils.entityCopy;

/**
 * @author cheney
 * @date 2019-08-01
 */
public class DefaultFieldCopyAdopt implements FieldCopyAdopt {

    @Override
    public Object format(Object sourceValue) {
        return sourceValue;
    }

}
