package com.defaulttenant.hotswap.service.dto.filters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import com.defaulttenant.hotswap.domain.enumeration.ErrorCodeEnum;
import com.defaulttenant.hotswap.exception.HttpCodeException;
import com.defaulttenant.hotswap.service.dto.filters.logic.binary.calculate.*;
import com.defaulttenant.hotswap.service.dto.filters.logic.binary.compare.*;
import com.defaulttenant.hotswap.service.dto.filters.logic.binary.logicCalculate.AndQueryFilter;
import com.defaulttenant.hotswap.service.dto.filters.logic.binary.logicCalculate.OrQueryFilter;
import com.defaulttenant.hotswap.service.dto.filters.logic.binary.matching.EndWithQueryFilter;
import com.defaulttenant.hotswap.service.dto.filters.logic.binary.matching.InQueryFilter;
import com.defaulttenant.hotswap.service.dto.filters.logic.binary.matching.LikeQueryFilter;
import com.defaulttenant.hotswap.service.dto.filters.logic.binary.matching.StartWithQueryFilter;
import com.defaulttenant.hotswap.service.dto.filters.logic.unary.IsNullQueryFilter;
import com.defaulttenant.hotswap.service.dto.filters.logic.unary.NotQueryFilter;
import com.defaulttenant.hotswap.util.JacksonUtils;

import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * @author sys
 */

public class CustomerDeserializer extends JsonDeserializer<AbstractQueryFilter> {

    @Override
    public AbstractQueryFilter deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        //解析Json
        TreeNode treeNode = jsonParser.getCodec().readTree(jsonParser);

        // 一元或二元表达式的操作符
        TextNode operatorTreeNode = (TextNode) treeNode.get("operator");
        String operator = operatorTreeNode.asText();

        // 左子树、右子树节点
        TreeNode leftTreeNode = treeNode.get("left");
        TreeNode rightTreeNode = treeNode.get("right");
        AbstractQueryFilter left = null;
        AbstractQueryFilter right = null;
        if (null != leftTreeNode) {
            left = JacksonUtils.fromJson(leftTreeNode.toString(), AbstractQueryFilter.class);
        }
        if (null != rightTreeNode) {
            right = JacksonUtils.fromJson(rightTreeNode.toString(), AbstractQueryFilter.class);
        }
        AbstractQueryFilter filter;

        switch (operator) {
            // 二元运算--算数运算
            case "+":
                filter = new AddQueryFilter().left(left).right(right);
                break;
            case "/":
                filter = new DivQueryFilter().left(left).right(right);
                break;
            case "%":
                filter = new ModQueryFilter().left(left).right(right);
                break;
            case "*":
                filter = new MulQueryFilter().left(left).right(right);
                break;
            case "-":
                filter = new SubQueryFilter().left(left).right(right);
                break;

            // 二元运算--比较运算
            case "==":
                filter = new EqualQueryFilter().left(left).right(right);
                break;
            case ">":
                filter = new GtQueryFilter().left(left).right(right);
                break;
            case "<":
                filter = new LtQueryFilter().left(left).right(right);
                break;
            case "<=":
                filter = new NgtQueryFilter().left(left).right(right);
                break;
            case ">=":
                filter = new NltQueryFilter().left(left).right(right);
                break;
            case "!=":
                filter = new NotEqualQueryFilter().left(left).right(right);
                break;

            // 二元运算--逻辑运算
            case "&&":
                filter = new AndQueryFilter().left(left).right(right);
                break;
            case "||":
                filter = new OrQueryFilter().left(left).right(right);
                break;

            // 二元运算--匹配运算
            case "endwith":
                filter = new EndWithQueryFilter().left(left).right(right);
                break;
            case "in":
                filter = new InQueryFilter().left(left).right(right);
                break;
            case "like":
                filter = new LikeQueryFilter().left(left).right(right);
                break;
            case "startwith":
                filter = new StartWithQueryFilter().left(left).right(right);
                break;

            // 一元运算
            case "isNull":
                filter = new IsNullQueryFilter().left(left);
                break;
            case "!":
                filter = new NotQueryFilter().left(left);
                break;
            default:
                throw new HttpCodeException(HttpStatus.METHOD_NOT_ALLOWED.value(), ErrorCodeEnum.OPERATOR_NOT_SUPPORT.code);
        }
        return filter;
    }
}
