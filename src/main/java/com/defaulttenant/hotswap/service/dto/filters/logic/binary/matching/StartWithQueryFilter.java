package com.defaulttenant.hotswap.service.dto.filters.logic.binary.matching;

import com.defaulttenant.hotswap.service.dto.filters.logic.binary.BinaryExpressionFilter;

/**
 * @Author: sys
 */
public class StartWithQueryFilter extends BinaryExpressionFilter {

    private static final String CONCAT_START = "concat(%s, '%%')";

    public StartWithQueryFilter() {
        this.operator = "startwith";
    }

    @Override
    public String sql(String dbType) {
        String likeString = String.format(CONCAT_START, right.sql(dbType));
        return String.format(" (%s LIKE %s) ", left.sql(dbType), likeString);
    }
}
