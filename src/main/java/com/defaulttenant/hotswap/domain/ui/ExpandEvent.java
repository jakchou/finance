package com.defaulttenant.hotswap.domain.ui;


public class ExpandEvent {
    public String item;
    public Boolean expanded;

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Boolean getExpanded() {
        return expanded;
    }

    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }
}
