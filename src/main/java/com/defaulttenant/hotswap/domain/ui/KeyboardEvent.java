package com.defaulttenant.hotswap.domain.ui;

public class KeyboardEvent {

    public Boolean altKey;

    public String code;

    public Boolean ctrlKey;

    public Boolean isComposing;

    public String key;

    public Boolean metaKey;

    public Boolean repeat;

    public Boolean shiftKey;

    public Boolean getAltKey() {
        return altKey;
    }

    public void setAltKey(Boolean altKey) {
        this.altKey = altKey;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getCtrlKey() {
        return ctrlKey;
    }

    public void setCtrlKey(Boolean ctrlKey) {
        this.ctrlKey = ctrlKey;
    }

    public Boolean getComposing() {
        return isComposing;
    }

    public void setComposing(Boolean composing) {
        isComposing = composing;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getMetaKey() {
        return metaKey;
    }

    public void setMetaKey(Boolean metaKey) {
        this.metaKey = metaKey;
    }

    public Boolean getRepeat() {
        return repeat;
    }

    public void setRepeat(Boolean repeat) {
        this.repeat = repeat;
    }

    public Boolean getShiftKey() {
        return shiftKey;
    }

    public void setShiftKey(Boolean shiftKey) {
        this.shiftKey = shiftKey;
    }
}



