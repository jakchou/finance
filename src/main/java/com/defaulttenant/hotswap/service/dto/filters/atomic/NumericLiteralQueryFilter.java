package com.defaulttenant.hotswap.service.dto.filters.atomic;

import com.defaulttenant.hotswap.service.dto.filters.AbstractQueryFilter;

import java.math.BigDecimal;

/**
 * @Author: sys
 */
public class NumericLiteralQueryFilter extends AbstractQueryFilter {

    private String value;

    private Number preparedValue;

    public NumericLiteralQueryFilter() {
        this.concept = "NumericLiteral";
    }

    public NumericLiteralQueryFilter(String value) {
        this();
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Number getPreparedValue() {
        return preparedValue;
    }

    public void setPreparedValue(Number preparedValue) {
        this.preparedValue = preparedValue;
    }

    @Override
    public String sql(String dbType) {
        if (null != value && (value.indexOf(".") >= 0 || value.toLowerCase().indexOf("e") >= 0)) {
            // 小数或科学计数
            preparedValue = new BigDecimal(value);
        } else if (null != value) {
            // 整数
            preparedValue = Long.valueOf(value);
        }
        return QUESTION_MARK;
    }
}
