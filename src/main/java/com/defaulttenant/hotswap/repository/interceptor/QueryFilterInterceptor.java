package com.defaulttenant.hotswap.repository.interceptor;


import com.defaulttenant.hotswap.service.dto.filters.AbstractQueryFilter;
import com.defaulttenant.hotswap.service.dto.filters.atomic.*;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * sql injection interceptor for mybatis
 */
@Intercepts(
        {
                @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        }
)
public class QueryFilterInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryFilterInterceptor.class);
    private Properties properties;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] queryArgs  = invocation.getArgs();
        MappedStatement ms  = (MappedStatement) queryArgs[0];
        MapperMethod.ParamMap paramMap = null;
        for (Object o : queryArgs) {
            if (o instanceof MapperMethod.ParamMap) {
                paramMap = (MapperMethod.ParamMap) o;
            }
        }
        if (null == paramMap) {
            return invocation.proceed();
        }

        AbstractQueryFilter filter = null;
        for (Object obj : paramMap.values()) {
            if (obj instanceof AbstractQueryFilter) {
                LOGGER.info("filter: " + obj.getClass().getName());
                filter = (AbstractQueryFilter) obj;
            }
        }

        if (null == filter || (null == filter.getLeft() && null == filter.getRight())) {
            return invocation.proceed();
        }

        BoundSql boundSql = ms.getBoundSql(paramMap);
        String sql = boundSql.getSql();
        // 重新new一个查询语句对象
        BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql,
                new ArrayList<>(boundSql.getParameterMappings()), boundSql.getParameterObject());
        MappedStatement newMs = newMappedStatement(ms, new BoundSqlSqlSource(newBoundSql));
        for (ParameterMapping mapping : boundSql.getParameterMappings()) {
            String prop = mapping.getProperty();
            if (boundSql.hasAdditionalParameter(prop)) {
                newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
            }
        }

        addParameterMapping(filter, newBoundSql, ms, "filter");
        queryArgs[0] = newMs;
        LOGGER.info("改写后的sql为：{}", newBoundSql.getSql());
        Object result = invocation.proceed();
        return result;
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    private boolean addPrimitiveParameterMapping(AbstractQueryFilter filter, BoundSql newBoundSql, MappedStatement ms, String prefix) {
        if (filter instanceof BooleanLiteralQueryFilter) {
            ParameterMapping mapping = new ParameterMapping.Builder(ms.getConfiguration(), prefix + ".value", Boolean.class).build();
            newBoundSql.getParameterMappings().add(mapping);
            return true;
        }

        if (filter instanceof UnparsedQueryFilter) {
            ParameterMapping mapping = new ParameterMapping.Builder(ms.getConfiguration(), prefix + ".value", String.class).build();
            newBoundSql.getParameterMappings().add(mapping);
            return true;
        }

        if (filter instanceof StringLiteralQueryFilter) {
            StringLiteralQueryFilter stringLiteralQueryFilter = (StringLiteralQueryFilter) filter;
            Object preparedValue = stringLiteralQueryFilter.getPreparedValue();
            ParameterMapping mapping = new ParameterMapping.Builder(ms.getConfiguration(), prefix + ".preparedValue",
                    null == preparedValue ? String.class : preparedValue.getClass()).build();
            newBoundSql.getParameterMappings().add(mapping);
            return true;
        }

        if (filter instanceof NumericLiteralQueryFilter) {
            NumericLiteralQueryFilter numberLiteralQueryFilter = (NumericLiteralQueryFilter) filter;
            Number preparedValue = numberLiteralQueryFilter.getPreparedValue();
            ParameterMapping mapping = new ParameterMapping.Builder(ms.getConfiguration(), prefix + ".preparedValue", preparedValue.getClass()).build();
            newBoundSql.getParameterMappings().add(mapping);
            return true;
        }

        if (filter instanceof NullLiteralQueryFilter || filter instanceof ColumnQueryFilter) {
            return true;
        }

        return false;
    }

    private void addParameterMapping(AbstractQueryFilter filter, BoundSql newBoundSql, MappedStatement ms, String prefix) {
        if (newBoundSql == null || filter == null) {
            return;
        }

        if (addPrimitiveParameterMapping(filter, newBoundSql, ms, prefix)) {
            return;
        }

        if (filter instanceof ListLiteralQueryFilter) {
            List<Object> listValues = ((ListLiteralQueryFilter) filter).getListValues();
            for (int i = 0; i < listValues.size(); i++) {
                Object obj = listValues.get(i);
                ParameterMapping mapping = new ParameterMapping.Builder(ms.getConfiguration(), prefix + ".listValues[" + i + "]" , null == obj ? String.class : obj.getClass()).build();
                newBoundSql.getParameterMappings().add(mapping);
            }
            return;
        }

        if (filter instanceof IdentifierQueryFilter) {
            // 变量可能是个list，条件需要拆分成 (?,?,?)形式
            IdentifierQueryFilter identifierQueryFilter = (IdentifierQueryFilter) filter;
            List<Object> identifyValues = identifierQueryFilter.getIdentifyValues();
            for (int i = 0; i < identifyValues.size(); i++) {
                // 指定变量 filter.left.right[0] filter.left.right[1]
                Object obj = identifyValues.get(i);
                ParameterMapping mapping = new ParameterMapping.Builder(ms.getConfiguration(), prefix + ".identifyValues[" + i + "]" , null == obj ? String.class : obj.getClass()).build();
                newBoundSql.getParameterMappings().add(mapping);
            }
            return;
        }

        addParameterMapping(filter.getLeft(), newBoundSql, ms, prefix + ".left");
        addParameterMapping(filter.getRight(), newBoundSql, ms, prefix + ".right");
    }

    static class BoundSqlSqlSource implements SqlSource {
        private BoundSql boundSql;
        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }

    }

    private MappedStatement newMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new
                MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length > 0) {
            builder.keyProperty(ms.getKeyProperties()[0]);
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }
}
