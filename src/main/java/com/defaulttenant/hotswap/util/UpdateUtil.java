package com.defaulttenant.hotswap.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;

import com.defaulttenant.hotswap.config.Constants;
import com.defaulttenant.hotswap.exception.HttpCodeException;

public class UpdateUtil {

    private UpdateUtil() {}

    public static void copyProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNoNullProperties(target));
    }

    private static String[] getNoNullProperties(Object target) {
        BeanWrapper srcBean = new BeanWrapperImpl(target);
        PropertyDescriptor[] pds = srcBean.getPropertyDescriptors();
        Set<String> noEmptyName = new HashSet<>();
        for (PropertyDescriptor p : pds) {
            Object value = srcBean.getPropertyValue(p.getName());
            if (value != null) {
                if (value.getClass().getName().startsWith(Constants.BASE_PACKAGE) && isEmptyObject(value)) {
                    try {
                        Field f = target.getClass().getDeclaredField(p.getName());
                        f.setAccessible(true);
                        f.set(target, null);
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        throw new HttpCodeException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
                    }
                }
                noEmptyName.add(p.getName());
            }
        }
        String[] result = new String[noEmptyName.size()];
        return noEmptyName.toArray(result);
    }

    public static void setPropertyNullWhenEmpty(Object object) {
        BeanWrapper srcBean = new BeanWrapperImpl(object);
        PropertyDescriptor[] pds = srcBean.getPropertyDescriptors();
        for (PropertyDescriptor p : pds) {
            if (p.getName().equals("class")) {
                continue;
            }
            Object value = srcBean.getPropertyValue(p.getName());
            if (value != null && value.getClass().getName().startsWith(Constants.BASE_PACKAGE) && isEmptyObject(value)) {
                try {
                    Field f = object.getClass().getDeclaredField(p.getName());
                    f.setAccessible(true);
                    f.set(object, null);
                } catch (Exception e) {
                    throw new HttpCodeException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
                }
            } else {
                setPropertyNullWhenEmpty(value);
            }
        }
    }

    private static boolean isEmptyObject(Object object) {
        BeanWrapper srcBean = new BeanWrapperImpl(object);
        PropertyDescriptor[] pds = srcBean.getPropertyDescriptors();
        for (PropertyDescriptor p : pds) {
            if (p.getName().equals("class")) {
                continue;
            }
            Object value = srcBean.getPropertyValue(p.getName());
            if (value != null) {
                return false;
            }
        }
        return true;
    }
}
