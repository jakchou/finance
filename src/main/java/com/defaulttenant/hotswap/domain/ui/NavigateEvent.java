package com.defaulttenant.hotswap.domain.ui;

public class NavigateEvent {
    public String to;
    public Boolean replace;
    public Boolean append;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Boolean getReplace() {
        return replace;
    }

    public void setReplace(Boolean replace) {
        this.replace = replace;
    }

    public Boolean getAppend() {
        return append;
    }

    public void setAppend(Boolean append) {
        this.append = append;
    }
}
