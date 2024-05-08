package com.defaulttenant.hotswap.domain.structure.anonymous;

/**
 * structure for enumeration, only 2 properties: value & text
 * @author sys
 */
public class AnonymousEnumStructure {
    public AnonymousEnumStructure() {
        // default AnonymousEnumStructure
    }

    public String value;
    public String text;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
