package com.defaulttenant.hotswap.domain.ui;


import java.util.List;

public class ChangeItemsEvent {
    public Boolean selected;
    public String item;
    public List<String> value;
    public List<String> oldValue;
    public List<String> items;
    public List<String> oldItems;

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }

    public List<String> getOldValue() {
        return oldValue;
    }

    public void setOldValue(List<String> oldValue) {
        this.oldValue = oldValue;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public List<String> getOldItems() {
        return oldItems;
    }

    public void setOldItems(List<String> oldItems) {
        this.oldItems = oldItems;
    }
}
