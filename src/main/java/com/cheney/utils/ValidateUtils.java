package com.cheney.utils;

import com.cheney.exception.ValidateException;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Set;

/**
 * 对象字段校验
 *
 * @author cheney
 * @date 2019/4/19
 */
public class ValidateUtils {

    private final static Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private ValidateUtils() {
    }

    public static <T> void validate(T obj) {
        if (obj instanceof Collection) {
            Collection<?> collection = (Collection<?>) obj;
            collection.forEach(ValidateUtils::validateEntity);
        } else if (obj.getClass().isArray()) {
            for (Object o : ((Object[]) obj)) {
                validateEntity(o);
            }
        } else {
            validateEntity(obj);
        }
    }

    private static <T> void validateEntity(T entity) {
        Set<ConstraintViolation<T>> result = VALIDATOR.validate(entity);
        if (!CollectionUtils.isEmpty(result)) {
            ConstraintViolation<T> violation = result.iterator().next();
            throw new ValidateException(violation.getMessage());
        }
    }

}
