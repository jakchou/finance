package com.defaulttenant.hotswap.domain.ui;

public class TreeChangeEvent {
    public String param1;
    public Long param2;
    public String value;
    public String oldValue;
    public String node;
    public String oldNode;

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public Long getParam2() {
        return param2;
    }

    public void setParam2(Long param2) {
        this.param2 = param2;
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

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getOldNode() {
        return oldNode;
    }

    public void setOldNode(String oldNode) {
        this.oldNode = oldNode;
    }
}
