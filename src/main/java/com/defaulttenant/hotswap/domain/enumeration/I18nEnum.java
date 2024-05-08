package com.defaulttenant.hotswap.domain.enumeration;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
* auto generate I18nEnum enum
*
* @author sys
*/

@JsonDeserialize(using = I18nEnum.I18nEnumDeserializer.class)
public enum I18nEnum implements BaseEnum<I18nEnum, String>{
    en$US("en-US", "en-US"),
    fr$FR("fr-FR", "fr-FR"),
    zh$CN("zh-CN", "zh-CN"),
    zh$TW("zh-TW", "zh-TW"),
    ja$JP("ja-JP", "ja-JP"),
    ko$KR("ko-KR", "ko-KR")
    ;
    public final String code;
    public final String desc;

    I18nEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonValue
    public String getJsonValue(){
        return this.code;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public static class I18nEnumDeserializer extends JsonDeserializer<I18nEnum> {
        @Override
        public I18nEnum deserialize(JsonParser p, DeserializationContext ctxt) {
            String code;
            try {
                code = p.getValueAsString();
            } catch (Exception e) {
                return null;
            }
            for (I18nEnum anEnum : I18nEnum.values()) {
                if (anEnum.getCode().toString().equals(code)) {
                    return anEnum;
                }
            }
            return null;
        }
    }
}