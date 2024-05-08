package com.defaulttenant.hotswap.service.dto.filters.logic.binary;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.defaulttenant.hotswap.service.dto.filters.AbstractQueryFilter;
import com.defaulttenant.hotswap.service.dto.filters.CustomerDeserializer;
import com.defaulttenant.hotswap.service.dto.filters.logic.binary.calculate.*;
import com.defaulttenant.hotswap.service.dto.filters.logic.binary.compare.*;
import com.defaulttenant.hotswap.service.dto.filters.logic.binary.logicCalculate.AndQueryFilter;
import com.defaulttenant.hotswap.service.dto.filters.logic.binary.logicCalculate.OrQueryFilter;
import com.defaulttenant.hotswap.service.dto.filters.logic.binary.matching.*;
import com.defaulttenant.hotswap.service.dto.filters.logic.binary.compare.NotEqualQueryFilter;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "operator",
        visible = true
)
@JsonSubTypes(
    value = {
        @JsonSubTypes.Type(value = AddQueryFilter.class, name = "+"),
        @JsonSubTypes.Type(value = DivQueryFilter.class, name = "/"),
        @JsonSubTypes.Type(value = ModQueryFilter.class, name = "%"),
        @JsonSubTypes.Type(value = MulQueryFilter.class, name = "*"),
        @JsonSubTypes.Type(value = SubQueryFilter.class, name = "-"),
        @JsonSubTypes.Type(value = EqualQueryFilter.class, name = "=="),
        @JsonSubTypes.Type(value = GtQueryFilter.class, name = ">"),
        @JsonSubTypes.Type(value = LtQueryFilter.class, name = "<"),
        @JsonSubTypes.Type(value = NgtQueryFilter.class, name = "<="),
        @JsonSubTypes.Type(value = NltQueryFilter.class, name = ">="),
        @JsonSubTypes.Type(value = NotEqualQueryFilter.class, name = "!="),
        @JsonSubTypes.Type(value = EndWithQueryFilter.class, name = "endwith"),
        @JsonSubTypes.Type(value = InQueryFilter.class, name = "in"),
        @JsonSubTypes.Type(value = LikeQueryFilter.class, name = "like"),
        @JsonSubTypes.Type(value = StartWithQueryFilter.class, name = "startwith"),
        @JsonSubTypes.Type(value = AndQueryFilter.class, name = "&&"),
        @JsonSubTypes.Type(value = OrQueryFilter.class, name = "||"),
    }
)
@JsonDeserialize(using= CustomerDeserializer.class)
public class BinaryExpressionFilter extends AbstractQueryFilter {

    protected String operator;

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public BinaryExpressionFilter() {
        this.concept = "BinaryExpression";
    }

    @Override
    public String sql(String dbType) {
        return "";
    }
}
