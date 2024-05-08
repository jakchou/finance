package com.defaulttenant.hotswap.service.dto.filters;

/**
 * @author sys
 */
public class FilterWrapper {

    AbstractQueryFilter returnExpression;

    public AbstractQueryFilter getReturnExpression() {
        return returnExpression;
    }

    public void setReturnExpression(AbstractQueryFilter returnExpression) {
        this.returnExpression = returnExpression;
    }
}
