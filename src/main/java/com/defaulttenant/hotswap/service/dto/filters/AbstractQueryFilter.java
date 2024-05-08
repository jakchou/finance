package com.defaulttenant.hotswap.service.dto.filters;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.defaulttenant.hotswap.service.dto.filters.atomic.*;
import com.defaulttenant.hotswap.service.dto.filters.logic.binary.BinaryExpressionFilter;
import com.defaulttenant.hotswap.service.dto.filters.logic.unary.UnaryExpressionFilter;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "concept",
    visible = true,
    defaultImpl = UnaryExpressionFilter.class
)
@JsonSubTypes(
    value = {
            @JsonSubTypes.Type(value = BooleanLiteralQueryFilter.class, name = "BooleanLiteral"),
            @JsonSubTypes.Type(value = ColumnQueryFilter.class, name = "MemberExpression"),
            @JsonSubTypes.Type(value = IdentifierQueryFilter.class, name = "Identifier"),
            @JsonSubTypes.Type(value = NullLiteralQueryFilter.class, name = "NullLiteral"),
            @JsonSubTypes.Type(value = NumericLiteralQueryFilter.class, name = "NumericLiteral"),
            @JsonSubTypes.Type(value = StringLiteralQueryFilter.class, name = "StringLiteral"),
            @JsonSubTypes.Type(value = UnparsedQueryFilter.class, name = "Unparsed"),
            @JsonSubTypes.Type(value = ListLiteralQueryFilter.class, name = "ListLiteral"),
            @JsonSubTypes.Type(value = UnaryExpressionFilter.class, name = "UnaryExpression"),
            @JsonSubTypes.Type(value = BinaryExpressionFilter.class, name = "BinaryExpression"),
    }
)
public abstract class AbstractQueryFilter {

    protected String DOT = ".";
    protected String QUOT = "`";
    protected String SINGLE_QUOT = "'";
    protected String DOUBLE_QUOT = "\"";
    protected String SPLIT_SPACE = " ";
    protected String SPLIT_COMMA = ",";
    protected String LEFT_PARENTHESES = "(";
    protected String RIGHT_PARENTHESES = ")";
    protected String NULL_STRING = "null";
    protected String DATETIME_T = "T";

    protected String QUESTION_MARK = "?";
    protected AbstractQueryFilter left;
    protected AbstractQueryFilter right;
    protected String concept;

    public AbstractQueryFilter() {
    }

    public AbstractQueryFilter getLeft() {
        return left;
    }

    public void setLeft(AbstractQueryFilter left) {
        this.left = left;
    }

    public AbstractQueryFilter getRight() {
        return right;
    }

    public void setRight(AbstractQueryFilter right) {
        this.right = right;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public AbstractQueryFilter left(AbstractQueryFilter leftExpr) {
        this.left = leftExpr;
        return this;
    }

    public AbstractQueryFilter right(AbstractQueryFilter rightExpr) {
        this.right = rightExpr;
        return this;
    }

    public abstract String sql(String dbType);

    public String fillSymbolOnTwoSide(String symbol, String value) {
        return symbol + value + symbol;
    }

}
