package com.defaulttenant.hotswap.service.entities.inner;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;
import javax.annotation.Resource;
import java.beans.PropertyDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import com.defaulttenant.hotswap.exception.HttpCodeException;
import com.defaulttenant.hotswap.domain.*;
import com.defaulttenant.hotswap.domain.enumeration.*;
import com.defaulttenant.hotswap.repository.*;
import com.defaulttenant.hotswap.util.SpringUtils;

/**
* auto generate RelationInnerService
*
* @author sys
*/
@Service
public class RelationInnerService {
    private Map<String, List<Object[]>> relationMap = new HashMap<>();
    private static Logger LOGGER = LoggerFactory.getLogger(RelationInnerService.class);
    private static final int INDEX_BEREF_PROPERTY = 0;
    private static final int INDEX_REF_ENTITY_MAPPER_CLASS = 1;
    private static final int INDEX_REF_PROPERTY = 2;
    private static final int INDEX_DEL_RULE = 3;

    public RelationInnerService() {
    }

    @Transactional(rollbackFor = Exception.class)
    public void onDelete(Object entity) {
        try {
            if (relationMap.containsKey(entity.getClass().getSimpleName())) {
                for (Object[] relationPayload : relationMap.get(entity.getClass().getSimpleName())) {
                    String beRefProperty = (String)relationPayload[INDEX_BEREF_PROPERTY];
                    PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(entity.getClass(), beRefProperty);
                    Object propertyVal = propertyDescriptor.getReadMethod().invoke(entity);

                    Class<ReferenceHandleMapper> refEntityMapperClass = (Class<ReferenceHandleMapper>) relationPayload[INDEX_REF_ENTITY_MAPPER_CLASS];
                    String refProperty = (String)relationPayload[INDEX_REF_PROPERTY];
                    ReferenceHandleMapper refEntityMapper = SpringUtils.getBean(refEntityMapperClass);

                    String delRule = (String) relationPayload[INDEX_DEL_RULE];
                    if ("cascade".equals(delRule)) {
                        LOGGER.info("cascade delete entity: {}, property {} = {}",
                            entity.getClass().getSimpleName(), refProperty, propertyVal);
                        refEntityMapper.deleteReference(refProperty, propertyVal);
                    } else if ("protect".equals(delRule)) {
                        Long affect = refEntityMapper.existReference(refProperty, propertyVal);
                        if (affect != null && affect > 0) {
                            throw new HttpCodeException(400, ErrorCodeEnum.RELATION_PROTECT.code);
                        }
                    }
                }
            }
        } catch (HttpCodeException ex) {
            throw ex;
        } catch(Exception e) {
            throw new HttpCodeException(400, "error: " + e.getMessage());
        }
    }
}