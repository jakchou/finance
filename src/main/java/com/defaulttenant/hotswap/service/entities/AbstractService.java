package com.defaulttenant.hotswap.service.entities;

import org.hibernate.validator.HibernateValidator;
import org.springframework.util.CollectionUtils;

import com.defaulttenant.hotswap.domain.enumeration.ErrorCodeEnum;
import com.defaulttenant.hotswap.exception.HttpCodeException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;

/**
 * Abstract Service
 *
 * @author sys
 * @date 2021-08-31 12:01
 */
public abstract class AbstractService {
    private static Validator validator = Validation.byProvider(HibernateValidator.class).configure()
            .failFast(false).buildValidatorFactory().getValidator();
    /**
     * get value return value if val is not null otherwise return defaultVal
     * @param val
     * @param defaultVal
     * @param <T>
     * @return
     */
    protected <T> T getOrDefault(T val, T defaultVal) {
        if (null == val) {
            return defaultVal;
        }
        return val;
    }

    /**
     * param validate
     * @param val
     * @param <T>
     */
    protected <T> void paramValidate(T val) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(val);
        if (!CollectionUtils.isEmpty(constraintViolations)) {
            // required check
            StringBuilder missedParams = new StringBuilder();
            String delimiter = "";
            for (ConstraintViolation<T> constraintViolation : constraintViolations) {
                if (constraintViolation.getConstraintDescriptor().getAnnotation() instanceof NotNull) {
                    missedParams.append(delimiter).append(constraintViolation.getPropertyPath().toString());
                    delimiter = ",";
                }
            }
            if (missedParams.length() > 0) {
                throw new HttpCodeException(400, ErrorCodeEnum.PARAM_REQUIRED.code, missedParams.toString());
            }

            // other check
        }
    }
}
