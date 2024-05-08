package com.defaulttenant.hotswap.service.dto.filters.atomic;


import com.defaulttenant.hotswap.config.Constants;
import com.defaulttenant.hotswap.domain.enumeration.ErrorCodeEnum;
import com.defaulttenant.hotswap.exception.HttpCodeException;
import com.defaulttenant.hotswap.service.dto.filters.AbstractQueryFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

/**
 * @Author: sys
 */
public class ColumnQueryFilter extends AbstractQueryFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ColumnQueryFilter.class);
    private String entityName;
    private String tableName;
    private String propertyName;
    private String columnName;

    private Property property;

    private MemberExpressionObject object;

    public static class Property {
        private String concept;

        private String name;

        public Property() {
        }

        public Property(String concept, String name) {
            this.concept = concept;
            this.name = name;
        }

        public String getConcept() {
            return concept;
        }

        public void setConcept(String concept) {
            this.concept = concept;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class MemberExpressionObject{

        private String concept;

        private String name;

        private String namespace;

        public MemberExpressionObject() {
        }

        public MemberExpressionObject(String concept, String name, String namespace) {
            this.concept = concept;
            this.name = name;
            this.namespace = namespace;
        }

        public String getConcept() {
            return concept;
        }

        public void setConcept(String concept) {
            this.concept = concept;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }
    }

    public ColumnQueryFilter() {
        this.concept = "MemberExpression";
    }

    public ColumnQueryFilter(String entityName, String tableName, String propertyName, String columnName) {
        this();
        this.entityName = entityName;
        this.tableName = tableName;
        this.propertyName = propertyName;
        this.columnName = columnName;
        this.property = new Property("EntityProperty", propertyName);
    }

    public ColumnQueryFilter(String entityName, String tableName, String propertyName, String columnName,
                             Property property) {
        this();
        this.entityName = entityName;
        this.tableName = tableName;
        this.propertyName = propertyName;
        this.columnName = columnName;
        this.property = property;
    }

    public ColumnQueryFilter(String entityName, String tableName, String propertyName, String columnName,
                             Property property, MemberExpressionObject object) {
        this(entityName, tableName, propertyName, columnName, property);
        this.object = object;
    }

    public MemberExpressionObject getObject() {
        return object;
    }

    public void setObject(MemberExpressionObject object) {
        this.object = object;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public String sql(String dbType) {

        if (this.object != null && "app.enums".equals(this.object.getNamespace())) {
            // 处理枚举值
            return fillSymbolOnTwoSide(SINGLE_QUOT, this.property.getName());
        }

        dbType = StringUtils.defaultString(dbType, "");
        columnName = StringUtils.defaultString(columnName, this.property.getName());
        if (StringUtils.isBlank(columnName) || columnName.contains(SPLIT_SPACE) || columnName.contains(SPLIT_COMMA) || columnName.contains(SINGLE_QUOT)) {
            LOGGER.info("illegal column name: {}", columnName);
            throw new HttpCodeException(HttpStatus.METHOD_NOT_ALLOWED.value(), ErrorCodeEnum.PARAM_INVALID.code);
        }
        switch (dbType) {
            case "mysql":
                return String.format("`%s`", columnName);
            default:
                throw new HttpCodeException(HttpStatus.METHOD_NOT_ALLOWED.value(), ErrorCodeEnum.DB_TYPE_NOT_SUPPORT.code);
        }
    }
}
