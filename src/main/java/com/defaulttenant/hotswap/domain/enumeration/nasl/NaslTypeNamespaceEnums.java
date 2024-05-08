package com.defaulttenant.hotswap.domain.enumeration.nasl;

import java.util.regex.Pattern;

/**
 * @author sys
 */
public enum NaslTypeNamespaceEnums {
    /** 官方namespace **/
    CORE("nasl.core", "内置基础类型"),
    COLLECTION("nasl.collection", "集合泛型"),
    INTERFACE("nasl.interface", "官方接口相关类型"),
    HTTP("nasl.http", "官方HTTP相关类型"),
    AUTH("nasl.auth", "官方认证领域相关类型"),
    UTIL("nasl.util", "官方内置函数和内置逻辑"),
    ANNOTATION("nasl.annotation", "官方注解"),
    UI("nasl.ui", "官方内置UI对象"),
    LOG("nasl.logging", "日志服务"),

    /** 用户namespace **/
    ENUMS("app.enums", "枚举类型"),
    STRUCTURES("app.structures", "数据结构类型"),
    PROCESS("app.processes", "流程"),
    DATASOURCE("app.dataSources", "数据源类型"),
    ENTITIES("app.(dataSources.\\S+.)?entities", "2.12版本后新增数据源概念，实体放再数据源下", true),

    /** 依赖库相关namespace **/
    DEPENDENCY("^extensions.", "依赖库", true),
    DEPENDENCY_ENUMS("^extensions.\\S+.enums$", "依赖库枚举", true),
    DEPENDENCY_STRUCTURE("^extensions.\\S+.structures$", "依赖库数据结构", true),

    /** 导入接口namespace **/
    IMPORT_INTERFACE("^apis.", "导入接口", true),
    IMPORT_STRUCTURES("^apis.\\S+.structures$", "导入接口", true),
    ;

    public final String code;
    public final String desc;
    private final Pattern pattern;

    NaslTypeNamespaceEnums(String code, String desc) {
        this(code, desc, false);
    }

    NaslTypeNamespaceEnums(String code, String desc, boolean isCodePattern) {
        this.code = code;
        this.desc = desc;
        if (isCodePattern) {
            this.pattern = Pattern.compile(code);
        } else {
            this.pattern = null;
        }
    }

    /**
     * 判断传入的某个namespace是否包含在自己的域下
     * @param namespace
     * @return
     */
    public boolean isBelong(String namespace) {
        if (this.pattern == null) {
            return code.startsWith(namespace);
        } else {
            return this.pattern.matcher(namespace).find();
        }
    }

    public static final NaslTypeNamespaceEnums of(String code) {
        for (NaslTypeNamespaceEnums naslTypeNamespaceEnums : NaslTypeNamespaceEnums.values()) {
            if (naslTypeNamespaceEnums.code.equals(code) || Pattern.matches(naslTypeNamespaceEnums.code, code)) {
                return naslTypeNamespaceEnums;
            }
        }

        return null;
    }

    private static final String SEPARATOR_DOT = "\\.";


    public static String getEntityNameFromNamespace(String namespace) {
        if (!ENTITIES.isBelong(namespace)) {
            return null;
        }

        int startIndex = namespace.indexOf("entities.") + "entities.".length();
        int endIndex = namespace.indexOf(".", startIndex);
        if (startIndex >= namespace.length() || endIndex < 0) {
            return null;
        }

        return namespace.substring(startIndex, endIndex);
    }

    public static String getDataSourceNameFromNamespace(String namespace) {
        if (!ENTITIES.isBelong(namespace)) {
            return null;
        }

        // 2.11版本的实体在默认数据源下
        if (namespace.startsWith("app.entities")) {
            return "defaultDs";
        }

        int startIndex = namespace.indexOf("dataSources.") + "dataSources.".length();
        int endIndex = namespace.indexOf(".", startIndex);
        if (startIndex >= namespace.length() || endIndex < 0) {
            return null;
        }

        return namespace.substring(startIndex, endIndex);
    }

    public static boolean isDefaultDataSource(String dataSource) {
        return "defaultDS".equals(dataSource);
    }

    public static String getModuleNameFromNamespace(String namespace) {
        if (DEPENDENCY.isBelong(namespace)) {
            int prefixLength = "extensions.".length();
            int index = namespace.indexOf(".",prefixLength);
            return index > 0 ? namespace.substring(prefixLength, index) : namespace.substring(prefixLength);
        } else if (IMPORT_INTERFACE.isBelong(namespace)) {
            int prefixLength = "apis.".length();
            int index = namespace.indexOf(".", prefixLength);
            return index > 0 ? namespace.substring(prefixLength, index) : namespace.substring(prefixLength);
        }

        return null;
    }
}
