package com.macro.mall.tiny.modules.buss.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaptchaImageVO {

    /**
     * 验证码key
     */
    private String key;

    /**
     * 验证码base64
     */
    private String image;

    /**
     * 过期时间，单位秒
     */
    private Integer exp;

}
