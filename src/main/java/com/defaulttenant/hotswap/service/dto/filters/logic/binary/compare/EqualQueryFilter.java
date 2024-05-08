package com.defaulttenant.hotswap.service.dto.filters.logic.binary.compare;

import com.defaulttenant.hotswap.service.dto.filters.atomic.NullLiteralQueryFilter;
import com.defaulttenant.hotswap.service.dto.filters.logic.binary.BinaryExpressionFilter;
/**
 * @Author: sys
 */
public class EqualQueryFilter extends BinaryExpressionFilter {

    public EqualQueryFilter() {
        this.operator = "==";
    }

    @Override
    public String sql(String dbType) {

        // 数据库语法 字段 = null 不生效，需要写成: 字段 is null
        if (right instanceof NullLiteralQueryFilter) {
            return String.format(" (%s is %s) ", left.sql(dbType), right.sql(dbType));
        }

        return String.format(" (%s = %s) ", left.sql(dbType), right.sql(dbType));
    }
}
