package com.defaulttenant.hotswap.service.dto.filters.logic.binary.compare;

import com.defaulttenant.hotswap.service.dto.filters.logic.binary.BinaryExpressionFilter;

/**
 * @Author: sys
 */
public class LtQueryFilter extends BinaryExpressionFilter {

    public LtQueryFilter() {
        this.operator = "<";
    }

    @Override
    public String sql(String dbType) {
        return String.format(" (%s < %s) ", left.sql(dbType), right.sql(dbType));
    }
}
