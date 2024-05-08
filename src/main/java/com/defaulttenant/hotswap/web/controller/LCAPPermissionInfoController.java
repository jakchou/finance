package com.defaulttenant.hotswap.web.controller;

import com.defaulttenant.hotswap.context.UserContext;
import com.defaulttenant.hotswap.config.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import org.springframework.util.CollectionUtils;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* auto generate LCAPPermissionController
* 权限下沉时，页面模版里默认会调应用的一些接口
* 比如检查用户可访问权限资源的getUserResources接口
*
* @author sys
*/
@RestController
@RequestMapping("api/user/system")
public class LCAPPermissionInfoController {


    private <T> T readFileToCollect(String filePath, TypeReference<T> typeReference) {
        ClassPathResource classPathResource = new ClassPathResource(filePath);
        InputStream inputStream = null;
        try {
            inputStream = classPathResource.getInputStream();
        } catch (IOException e) {
            return null;
        }
        T readValue = null;
        try {
            readValue = new ObjectMapper().readValue(inputStream, typeReference);
        } catch (IOException e) {
            return null;
        }
        return readValue;
    }

    @GetMapping("/getUserResources")
    public List<Map<String,String>> getUserResources() {
        UserContext.UserInfo currentUserInfo = UserContext.getCurrentUserInfo();
        if(Objects.isNull(currentUserInfo)){
            return null;
        }
        List<Map<String,String>> uiResourceList = new ArrayList<>();
        //TODO: 兼容3.0版本以下，模板中LCAPGetUserResources的自定义逻辑实现
        List<Map> resourceMetaData = readFileToCollect("permission/resourceMetaData.json", new TypeReference<List<Map>>() {});
        if(!CollectionUtils.isEmpty(resourceMetaData)){
                resourceMetaData.stream().forEach(result->{
                Map<String,String> item = new HashMap<>(2);
                Object resourceTypeObject = result.get("name");
                if(Objects.nonNull(resourceTypeObject)){
                    //与前端页约定类型，统一为ui
                    item.put("resourceType","ui");
                    item.put("resourceValue",resourceTypeObject.toString());
                    uiResourceList.add(item);
                }
            });
        }
       return uiResourceList;
   }



}