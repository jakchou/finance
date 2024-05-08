package com.defaulttenant.hotswap.service.dto.filters.atomic;

import com.defaulttenant.hotswap.service.dto.filters.AbstractQueryFilter;


/**
 * @Author: sys
 */
public class UnparsedQueryFilter extends AbstractQueryFilter {

    private String value;

    public UnparsedQueryFilter() {
        this.concept = "Unparsed";
    }

    public UnparsedQueryFilter(String value) {
        this();
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String sql(String dbType) {
        return QUESTION_MARK;
    }
}
