package com.defaulttenant.hotswap.domain.ui;


public class CollapseEvent {
    public Boolean expanded;
    public Boolean open;
    public Boolean value;
    public Boolean oldValue;
    public String node;

    public Boolean getExpanded() {
        return expanded;
    }

    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    public Boolean getOldValue() {
        return oldValue;
    }

    public void setOldValue(Boolean oldValue) {
        this.oldValue = oldValue;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }
}
