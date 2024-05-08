package com.defaulttenant.hotswap.repository.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import java.math.BigDecimal;
import com.defaulttenant.hotswap.domain.entities.Entity1Entity;
import com.defaulttenant.hotswap.repository.ReferenceHandleMapper;
import com.defaulttenant.hotswap.service.dto.filters.AbstractQueryFilter;
import org.apache.ibatis.annotations.Param;
/**
* auto generate Entity1Entity Mapper
*
* @author sys
*/
public interface Entity1EntityMapper extends ReferenceHandleMapper {

    int insert(Entity1Entity bean);
    int batchInsert(List<Entity1Entity> beans);
    List<Entity1Entity> selectList(@Param("filter") AbstractQueryFilter filter);
    int count(@Param("filter") AbstractQueryFilter filter);

    int update(Entity1Entity bean, List<String> updateFields);
    int batchUpdate(List<Entity1Entity> beans, List<String> updateFields);
    int delete(Long id);
    int batchDelete(List<Long> ids);
    Entity1Entity selectOne(Long id);

    int createOrUpdate(Entity1Entity bean);
    int updateBy(Entity1Entity bean, List<String> updateFields, AbstractQueryFilter filter);
    int deleteBy(@Param("filter") AbstractQueryFilter filter);

}