package com.macro.mall.tiny.modules.buss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.tiny.modules.buss.dto.CaptchaImageVO;
import com.macro.mall.tiny.modules.buss.model.BaseDict;

/**

 * @author macro
 * @since 2024-03-28
 */
public interface SystemService {

    CaptchaImageVO getCaptchaImage(String key);
}
