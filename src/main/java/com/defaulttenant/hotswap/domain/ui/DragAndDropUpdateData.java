package com.defaulttenant.hotswap.domain.ui;

import java.util.List;

public class DragAndDropUpdateData<T> {
    public List<T> sourceList;
    public List<T> targetList;

    public List<T> getSourceList() {
        return sourceList;
    }

    public void setSourceList(List<T> sourceList) {
        this.sourceList = sourceList;
    }

    public List<T> getTargetList() {
        return targetList;
    }

    public void setTargetList(List<T> targetList) {
        this.targetList = targetList;
    }
}
