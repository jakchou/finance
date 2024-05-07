package com.macro.mall.tiny.modules.buss.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hazelcast.core.HazelcastInstance;
import com.macro.mall.tiny.common.constant.Constant;
import com.macro.mall.tiny.modules.buss.dto.CaptchaImageVO;
import com.macro.mall.tiny.modules.buss.mapper.BaseDictMapper;
import com.macro.mall.tiny.modules.buss.model.BaseDict;
import com.macro.mall.tiny.modules.buss.service.BaseDictService;
import com.macro.mall.tiny.modules.buss.service.SystemService;
import com.wf.captcha.ArithmeticCaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.hazelcast.map.impl.recordstore.expiry.ExpiryReason.TTL;

/**
 * <p>
 * 字典类型表 服务实现类
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
@Service
public class SystemServiceImpl implements SystemService {

    /**
     * 验证码有效期，单位秒
     */
    private static final int TTL = 60;
    @Autowired
    private HazelcastInstance hazelcastInstance;
    @Override
    public CaptchaImageVO getCaptchaImage(String key) {
        String image = generateCaptcha(key);
        return CaptchaImageVO.builder()
                .key(key)
                .image(image)
                .exp(TTL)
                .build();
    }

    public String generateCaptcha(String key) {
        ArithmeticCaptcha specCaptcha = new ArithmeticCaptcha(120, 40);
        specCaptcha.setLen(2);
        // 验证码值
        String verCode = specCaptcha.text();
        System.out.println(verCode);
        // 存入redis并设置过期时间为1分钟
        hazelcastInstance.getMap(Constant.CACHE_IMAP_NAME+Constant.CACHE_VERIFY).put(key, verCode, TTL, TimeUnit.SECONDS);
        return specCaptcha.toBase64();
    }
}
