package com.defaulttenant.hotswap.repository.handler;

import com.defaulttenant.hotswap.domain.enumeration.BaseEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * @author sys
 * @Desc: implement custom enumeration converter
 */

public class AutoEnumTypeHandler<E extends BaseEnum> extends BaseTypeHandler<E> {

    private Class<E> type;

    private E[] enums;

    public AutoEnumTypeHandler() {
    }

    public AutoEnumTypeHandler(Class<E> type){
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        } else {
            this.type = type;
            this.enums = this.type.getEnumConstants();
            if (this.enums == null) {
                throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
            }
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, E e, JdbcType jdbcType) throws SQLException {
        if (jdbcType == null) {
            preparedStatement.setObject(i, e.getCode());
        } else {
            preparedStatement.setObject(i, e.getCode(), jdbcType.TYPE_CODE);
        }
    }

    @Override
    public E getNullableResult(ResultSet resultSet, String s) throws SQLException {
        Object object = resultSet.getObject(s);

        if (resultSet.wasNull()) {
            return null;
        }
        return getEnmByCode(object);
    }

    @Override
    public E getNullableResult(ResultSet resultSet, int i) throws SQLException {
        Object code = resultSet.getObject(i);
        if (resultSet.wasNull()) {
            return null;
        }
        return getEnmByCode(code);
    }

    @Override
    public E getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        Object code = callableStatement.getObject(i);
        if (callableStatement.wasNull()) {
            return null;
        }

        return getEnmByCode(code);
    }

    public E getEnmByCode(Object code) {

        if (code == null) {
            return null;
        }

        if (code instanceof Integer || code instanceof Long) {
            for (E e : enums) {
                if (String.valueOf(e.getCode()).equals(String.valueOf(code))) {
                    return e;
                }
            }
            return null;
        }

        if (code instanceof String) {
            for (E e : enums) {
                if (code.equals(e.getCode())) {
                    return e;
                }
            }
            return null;
        }

        return null;
    }

}