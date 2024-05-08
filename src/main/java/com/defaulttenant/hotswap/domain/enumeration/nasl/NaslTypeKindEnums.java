package com.defaulttenant.hotswap.domain.enumeration.nasl;

/**
 * @author sys
 */
public enum NaslTypeKindEnums {
    PRIMITIVE("primitive", "原子类型,如Integer,Boolean等"),
    REFERENCE("reference", "引用类型"),
    GENERIC("generic", "泛型"),
    TYPE_PARAM("typeParam", "泛型"),
    ANONYMOUS_STRUCTURE("anonymousStructure", "匿名数据结构"),
    UNION("union", "Union类型"),
    ;

    public final String code;
    public final String desc;

    NaslTypeKindEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static final NaslTypeKindEnums of(String code) {
        for (NaslTypeKindEnums naslTypeKindEnum : NaslTypeKindEnums.values()) {
            if (naslTypeKindEnum.code.equalsIgnoreCase(code)) {
                return naslTypeKindEnum;
            }
        }

        return null;
    }
}
