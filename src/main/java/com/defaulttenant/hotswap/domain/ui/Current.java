package com.defaulttenant.hotswap.domain.ui;

/**
 * System built in generic class
 * ScopeOf
 *
 * @author sys
 */
public class Current<T> {
    public T item;

    public Long index;

    public Long rowIndex;

    public Long columnIndex;

    public String value;

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public Long getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(Long rowIndex) {
        this.rowIndex = rowIndex;
    }

    public Long getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(Long columnIndex) {
        this.columnIndex = columnIndex;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return (
            "ScopeOf{" +
            "item=" +
            item +
            ", index=" +
            index +
            ", rowIndex=" +
            rowIndex +
            ", columnIndex=" +
            columnIndex +
            ", value='" +
            value +
            '\'' +
            '}'
        );
    }
}
