package com.defaulttenant.hotswap.domain.ui;

import java.util.List;

public class ChangeEvent {
    public String value;
    public String oldValue;
    public String formattedValue;
    public List<String> values;
    public List<String> oldValues;
    public String label;
    public Boolean valid;

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

    public String getFormattedValue() {
        return formattedValue;
    }

    public void setFormattedValue(String formattedValue) {
        this.formattedValue = formattedValue;
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }
}
