package com.defaulttenant.hotswap.domain.ui;


public class ChangeItemEvent {
    public String selected;
    public String value;
    public String oldValue;
    public String item;
    public String oldItem;
    public String label;

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
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

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getOldItem() {
        return oldItem;
    }

    public void setOldItem(String oldItem) {
        this.oldItem = oldItem;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
