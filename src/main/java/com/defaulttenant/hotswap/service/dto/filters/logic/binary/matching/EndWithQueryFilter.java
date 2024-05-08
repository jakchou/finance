package com.defaulttenant.hotswap.service.dto.filters.logic.binary.matching;

import com.defaulttenant.hotswap.service.dto.filters.logic.binary.BinaryExpressionFilter;

/**
 * @Author: sys
 */
public class EndWithQueryFilter extends BinaryExpressionFilter {
    private static String CONCAT_END = "concat('%%', %s)";

    public EndWithQueryFilter() {
        this.operator = "endwith";
    }

    @Override
    public String sql(String dbType) {
        String likeString = String.format(CONCAT_END, right.sql(dbType));
        return String.format(" (%s LIKE %s) ", left.sql(dbType), likeString);
    }
}
