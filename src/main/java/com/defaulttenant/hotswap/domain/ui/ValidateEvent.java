package com.defaulttenant.hotswap.domain.ui;

public class ValidateEvent {
    public String rawValue;
    public String value;
    public String trigger;
    public String muted;
    public Boolean valid;
    public Boolean touched;
    public Boolean dirty;
    public String firstError;

    public String getRawValue() {
        return rawValue;
    }

    public void setRawValue(String rawValue) {
        this.rawValue = rawValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String getMuted() {
        return muted;
    }

    public void setMuted(String muted) {
        this.muted = muted;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Boolean getTouched() {
        return touched;
    }

    public void setTouched(Boolean touched) {
        this.touched = touched;
    }

    public Boolean getDirty() {
        return dirty;
    }

    public void setDirty(Boolean dirty) {
        this.dirty = dirty;
    }

    public String getFirstError() {
        return firstError;
    }

    public void setFirstError(String firstError) {
        this.firstError = firstError;
    }
}
