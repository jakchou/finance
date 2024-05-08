package com.defaulttenant.hotswap.service.dto.filters.logic.unary;


import com.defaulttenant.hotswap.service.dto.filters.logic.binary.BinaryExpressionFilter;

/**
 * @Author: sys
 */
public class NotQueryFilter extends UnaryExpressionFilter {

    public NotQueryFilter() {
        this.operator = "!";
    }

    @Override
    public String sql(String dbType) {

        if (left instanceof BinaryExpressionFilter) {
            return String.format(" not %s ", left.sql(dbType));
        }

        return String.format(" not (%s) ", left.sql(dbType));
    }
}
