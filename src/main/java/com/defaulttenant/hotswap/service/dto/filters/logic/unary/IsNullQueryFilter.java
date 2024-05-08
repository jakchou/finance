package com.defaulttenant.hotswap.service.dto.filters.logic.unary;


/**
 * @Author: sys
 */
public class IsNullQueryFilter extends UnaryExpressionFilter {


    public IsNullQueryFilter() {
        this.operator = "isNull";
    }

    @Override
    public String sql(String dbType) {
        return String.format(" (%s is null) ", left.sql(dbType));
    }
}
