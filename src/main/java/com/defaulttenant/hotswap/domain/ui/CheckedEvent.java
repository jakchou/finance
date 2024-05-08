package com.defaulttenant.hotswap.domain.ui;


import java.util.List;

public class CheckedEvent {
    public Boolean checked;
    public Boolean oldChecked;
    public List<String> values;
    public List<String> oldValues;
    public String node;
    public String item;

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Boolean getOldChecked() {
        return oldChecked;
    }

    public void setOldChecked(Boolean oldChecked) {
        this.oldChecked = oldChecked;
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

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
