package com.defaulttenant.hotswap.domain.ui;

import java.util.List;

public class CascadeCapsulesEvent {
    public Long level;
    public String value;
    public String oldValue;
    public List<String> values;
    public List<String> oldValues;
    public String item;

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public List<String> getOldValues() {
        return oldValues;
    }

    public void setOldValues(List<String> oldValues) {
        this.oldValues = oldValues;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
