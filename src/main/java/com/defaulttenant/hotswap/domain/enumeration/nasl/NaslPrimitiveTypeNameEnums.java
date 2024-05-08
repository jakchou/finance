package com.defaulttenant.hotswap.domain.enumeration.nasl;

/**
 * 基础类型枚举
 *
 * @author sys
 */
public enum NaslPrimitiveTypeNameEnums {
    BOOLEAN("Boolean", java.lang.Boolean.class.getCanonicalName(), "布尔类型"),
    INTEGER("Integer", java.lang.Integer.class.getCanonicalName(),  "整型"),
    LONG("Long", java.lang.Long.class.getCanonicalName(), "长整型"),
    DOUBLE("Double", java.lang.Double.class.getCanonicalName(), "小数"),
    STRING("String", java.lang.String.class.getCanonicalName(), "字符串"),
    TEXT("Text", java.lang.String.class.getCanonicalName(), "大文本"),
    BINARY("Binary", java.lang.Byte[].class.getCanonicalName(), "二进制"),
    DATE("Date", java.time.LocalDate.class.getCanonicalName(), "日期"),
    TIME("Time", java.time.LocalTime.class.getCanonicalName(), "时间"),
    DATETIME("DateTime", java.time.ZonedDateTime.class.getCanonicalName(), "时间日期"),
    EMAIL("Email", java.lang.String.class.getCanonicalName(), "邮箱类型"),
    BIGDECIMAL("Decimal", java.math.BigDecimal.class.getCanonicalName(), "精确小数"),
    ;

    public final String code;
    public final String desc;
    public final String javaType;

    NaslPrimitiveTypeNameEnums(String code, String javaType, String desc) {
        this.code = code;
        this.javaType = javaType;
        this.desc = desc;
    }

    public static final NaslPrimitiveTypeNameEnums of(String code) {
        for (NaslPrimitiveTypeNameEnums naslTypeNamespaceEnums : NaslPrimitiveTypeNameEnums.values()) {
            if (naslTypeNamespaceEnums.code.equals(code)) {
                return naslTypeNamespaceEnums;
            }
        }

        return null;
    }
}
