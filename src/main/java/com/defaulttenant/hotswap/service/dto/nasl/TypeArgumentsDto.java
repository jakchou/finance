package com.defaulttenant.hotswap.service.dto.nasl;

import com.defaulttenant.hotswap.Application;
import com.defaulttenant.hotswap.domain.ApiReturnOf;
import com.defaulttenant.hotswap.domain.enumeration.nasl.NaslPrimitiveTypeNameEnums;
import com.defaulttenant.hotswap.domain.enumeration.nasl.NaslTypeKindEnums;
import com.defaulttenant.hotswap.domain.enumeration.nasl.NaslTypeNamespaceEnums;
import com.defaulttenant.hotswap.exception.HttpCodeException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

import static org.apache.commons.lang3.StringUtils.capitalize;

public class TypeArgumentsDto {
    private String typeKind;
    private String typeNamespace;
    private String typeName;
    private TypeArgumentsDto[] typeArguments;

    public String getTypeKind() {
        return typeKind;
    }

    public void setTypeKind(String typeKind) {
        this.typeKind = typeKind;
    }

    public String getTypeNamespace() {
        return typeNamespace;
    }

    public void setTypeNamespace(String typeNamespace) {
        this.typeNamespace = typeNamespace;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public TypeArgumentsDto[] getTypeArguments() {
        return typeArguments;
    }

    public void setTypeArguments(TypeArgumentsDto[] typeArguments) {
        this.typeArguments = typeArguments;
    }

    public Class toJavaClass() {
        if (StringUtils.isEmpty(typeKind)) {
            return null;
        }

        String basePackage = Application.class.getPackage().getName();
        String domainPackage = ApiReturnOf.class.getPackage().getName();

        try {
            switch (NaslTypeKindEnums.of(typeKind)) {
                case UNION:
                    return Object.class;
                case ANONYMOUS_STRUCTURE:
                    // 匿名数据结构
                    return Class.forName(domainPackage + ".structure.anonymous." + typeName);
                case PRIMITIVE:
                    // 基础类型
                    return Class.forName(NaslPrimitiveTypeNameEnums.of(typeName).javaType);
                case REFERENCE:
                    return parseReference(basePackage, domainPackage);
                case GENERIC:
                    // todo 泛型不能再指定尖括号里的类型，否则语法错误
                    if (NaslTypeNamespaceEnums.COLLECTION.isBelong(typeNamespace) && typeName.equals("List")) {
                        return ArrayList.class;
                    } else if (NaslTypeNamespaceEnums.COLLECTION.isBelong(typeNamespace) && typeName.equals("Map")) {
                        return HashMap.class;
                    } else if (NaslTypeNamespaceEnums.INTERFACE.isBelong(typeNamespace) && typeName.equals("ApiReturnOf")) {
                        return ApiReturnOf.class;
                    }
                    return null;
                default:
                    return null;
            }
        } catch (ClassNotFoundException e) {
            throw new HttpCodeException(500, e);
        }
    }

    private Class parseReference(String basePackage, String domainPackage) throws ClassNotFoundException {
        if (NaslTypeNamespaceEnums.ENUMS.isBelong(typeNamespace)) {
            // 应用枚举类型
            return Class.forName(domainPackage + ".enumeration." + capitalize(typeName)+ "Enum");
        } else if (NaslTypeNamespaceEnums.ENTITIES.isBelong(typeNamespace)) {
            String dataSourceName = NaslTypeNamespaceEnums.getDataSourceNameFromNamespace(typeNamespace);
            if (NaslTypeNamespaceEnums.isDefaultDataSource(dataSourceName)) {
                return Class.forName(domainPackage + ".entities." + capitalize(typeName) + "Entity");
            } else {
                return Class.forName(domainPackage + ".entities." + dataSourceName.toLowerCase() + "." + capitalize(typeName) + "Entity");
            }
        } else if (NaslTypeNamespaceEnums.STRUCTURES.isBelong(typeNamespace)) {
            // 应用数据结构类型
            return Class.forName(domainPackage + ".structure." + capitalize(typeName) + "Structure");
        } else if (typeNamespace.startsWith(NaslTypeNamespaceEnums.UI.code)) {
            // 系统内置ui类型
            return Class.forName(domainPackage + ".ui." + capitalize(typeName));
        } else if (NaslTypeNamespaceEnums.DEPENDENCY_ENUMS.isBelong(typeNamespace)) {
            // 依赖库枚举
            // todo 如果是外部依赖库(jar包依赖)的类可能会有问题，那些类有自己的package规则
            String moduleName = NaslTypeNamespaceEnums.getModuleNameFromNamespace(typeNamespace);
            return Class.forName(domainPackage + ".enumeration.extensions." + moduleName + "." +  capitalize((typeName) + "Enum"));
        }else if (NaslTypeNamespaceEnums.DEPENDENCY_STRUCTURE.isBelong(typeNamespace)) {
            // 依赖库数据结构
            // todo 如果是外部依赖库(jar包依赖)的类可能会有问题，那些类有自己的package规则
            String moduleName = NaslTypeNamespaceEnums.getModuleNameFromNamespace(typeNamespace);
            return Class.forName(domainPackage + ".structure.extensions." + moduleName + "." +  capitalize((typeName) + "Structure"));
        } else if (NaslTypeNamespaceEnums.IMPORT_STRUCTURES.isBelong(typeNamespace)) {
            // 外部接口导入, 结构体相关
            String moduleName = NaslTypeNamespaceEnums.getModuleNameFromNamespace(typeNamespace);
            return Class.forName(domainPackage + ".structure." + moduleName + "." + capitalize(typeName) + "Structure");
        } else if (NaslTypeNamespaceEnums.PROCESS.isBelong(typeNamespace)) {
            //暂时流程系统只有数据结构，暂时简单处理，后面若引入枚举、系统实体需变更相关。
            return Class.forName(basePackage + ".process.system.domain.structure." + typeName);
        }  else if (typeNamespace.startsWith("nasl.auth")) {
            return Class.forName(basePackage + ".context.UserContext.UserInfo");
        }
        //这里需要判断是否外部导入接口所依赖的
        return null;
    }
}
