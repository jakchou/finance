package com.defaulttenant.hotswap.domain.structure.anonymous;

/**
 * structure for enumeration, only 2 properties: value & text
 * @author sys
 */
public class AnonymousLongEnumStructure {
    public AnonymousLongEnumStructure() {
        // default AnonymousLongEnumStructure
    }

    public Long value;
    public String text;

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
