package com.defaulttenant.hotswap.service.dto.filters.logic.unary;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.defaulttenant.hotswap.service.dto.filters.AbstractQueryFilter;
import com.defaulttenant.hotswap.service.dto.filters.CustomerDeserializer;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "operator",
    visible = true
)
@JsonSubTypes(
    value = {
        @JsonSubTypes.Type(value = IsNullQueryFilter.class, name = "isNull"),
        @JsonSubTypes.Type(value = NotQueryFilter.class, name = "!"),
    }
)
@JsonDeserialize(using= CustomerDeserializer.class)
public class UnaryExpressionFilter extends AbstractQueryFilter {

    protected String operator;

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public UnaryExpressionFilter() {
        this.concept = "UnaryExpression";
    }

    @Override
    public String sql(String dbType) {
        return "";
    }
}
