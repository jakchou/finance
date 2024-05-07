package com.macro.mall.tiny.common.constant;

/**
 * @author zhouzz
 */
public enum DictType {
    Recruitment("Recruitment"),
    Educational("educational");

    private final String type;
    DictType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }
}
