package com.defaulttenant.hotswap.service.logics; 

import org.springframework.stereotype.Service; 
import com.defaulttenant.hotswap.util.CommonFunctionUtil; 
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory; 
import com.defaulttenant.hotswap.config.Constants; 

@Service
public class Logic61CustomizeService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger(Constants.LCAP_CUSTOMIZE_LOGGER);
    public void logic61() {
        LCAP_LOGGER.info(CommonFunctionUtil.toString("哈哈哈哈"));
        return ;
    } 


}
