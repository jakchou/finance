package com.defaulttenant.hotswap.service.dto.filters;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author sys
 */
public class EntityFilter {

    private Map entity;

    private List<Map> entities;

    private FilterWrapper filter;

    private List<String> properties;

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }

    public Map getEntity() {
        return entity;
    }

    public void setEntity(Map entity) {
        this.entity = entity;
    }

    public List<Map> getEntities() {
        return entities;
    }

    public void setEntities(List<Map> entities) {
        this.entities = entities;
    }

    public FilterWrapper getFilter() {
        return filter;
    }

    public void setFilter(FilterWrapper filter) {
        this.filter = filter;
    }
}
