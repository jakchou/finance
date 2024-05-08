package com.defaulttenant.hotswap.util;

import com.defaulttenant.hotswap.exception.HttpCodeException;

public class SqlInjectCheckUtil {
    private static final String DESC = "DESC";
    private static final String ASC = "ASC";

    private SqlInjectCheckUtil() {
    }

    public static String handleOrderByParam(String param) {
        return param.replace("`", "").replace("\"", "");
    }

    public static String checkAscOrDesc(String param) {
        if (!DESC.equalsIgnoreCase(param) && !ASC.equalsIgnoreCase(param)) {
            throw new HttpCodeException(400, String.format("desc param is invalid: %s", param));
        }
        return param;
    }
}
