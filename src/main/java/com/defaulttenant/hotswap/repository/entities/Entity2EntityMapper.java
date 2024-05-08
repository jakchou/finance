package com.defaulttenant.hotswap.repository.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import java.math.BigDecimal;
import com.defaulttenant.hotswap.domain.entities.Entity2Entity;
import com.defaulttenant.hotswap.repository.ReferenceHandleMapper;
import com.defaulttenant.hotswap.service.dto.filters.AbstractQueryFilter;
import org.apache.ibatis.annotations.Param;
/**
* auto generate Entity2Entity Mapper
*
* @author sys
*/
public interface Entity2EntityMapper extends ReferenceHandleMapper {

    int insert(Entity2Entity bean);
    int batchInsert(List<Entity2Entity> beans);
    List<Entity2Entity> selectList(@Param("filter") AbstractQueryFilter filter);
    int count(@Param("filter") AbstractQueryFilter filter);

    int update(Entity2Entity bean, List<String> updateFields);
    int batchUpdate(List<Entity2Entity> beans, List<String> updateFields);
    int delete(Long id);
    int batchDelete(List<Long> ids);
    Entity2Entity selectOne(Long id);

    int createOrUpdate(Entity2Entity bean);
    int updateBy(Entity2Entity bean, List<String> updateFields, AbstractQueryFilter filter);
    int deleteBy(@Param("filter") AbstractQueryFilter filter);

}