package com.defaulttenant.hotswap.domain.ui;


public class SelectData<T> {
    public T parent;
    public T item;
    public Long level;
    public Long index;

    public T getParent() {
        return parent;
    }

    public void setParent(T parent) {
        this.parent = parent;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }
}
