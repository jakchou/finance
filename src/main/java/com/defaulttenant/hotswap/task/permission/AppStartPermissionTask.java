package com.defaulttenant.hotswap.task.permission;

import com.defaulttenant.hotswap.iam.permission.AppStartPermissionDataService;
import com.defaulttenant.hotswap.service.entities.*;
import com.defaulttenant.hotswap.task.permission.model.*;
import com.defaulttenant.hotswap.domain.entities.*;
import com.defaulttenant.hotswap.service.logics.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
* 应用启动时权限数据上报任务类
*
* @author sys
* @since 2.22
*/
@Component
public class AppStartPermissionTask implements ApplicationRunner {

    Logger log = LoggerFactory.getLogger(AppStartPermissionTask.class);

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private AppStartPermissionDataService permissionDataService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void run(ApplicationArguments args) throws Exception {
        long begin = System.currentTimeMillis();
        log.info("应用启动时处理权限数据开始----->>>");
        // 1. 构建权限数据包
        DeployPermissionPack permissionPack = null;
        try {
            permissionPack = buildPermissionPack();
        } catch (Exception e) {
            log.error("应用启动时构建权限数据包失败: {}", e);
            return;
        }

        // 2. 执行默认权限数据上报逻辑
        // 如果没有扩展实现 就走默认上报权限的逻辑
        permissionDataService.permissionDataProcess(permissionPack);
        log.info("应用启动时处理权限数据结束。 耗时: {} ms", System.currentTimeMillis() - begin);
    }

    private DeployPermissionPack buildPermissionPack() {
        DeployPermissionPack packBuilder = new DeployPermissionPack();
        // 读取逻辑鉴权元数据
        Map<String, List<List<DeployLogicAuthMetaData>>> logicAuthMetaData = readFileToCollect("permission/logicAuthMetaData.json", new TypeReference<Map<String, List<List<DeployLogicAuthMetaData>>>>() {
        });
        packBuilder.setLogicAuthMetaDataCollect(logicAuthMetaData);
        // 1.2 资源数据
        List<DeployResourceMetaData> resourceMetaData = readFileToCollect("permission/resourceMetaData.json", new TypeReference<List<DeployResourceMetaData>>() {
        });
        packBuilder.setResourceMetaDataCollect(resourceMetaData);
        // 1.3 权限数据
        List<DeployPermissionMetaData> permissionMetaData = readFileToCollect("permission/permissionMetaData.json", new TypeReference<List<DeployPermissionMetaData>>() {
        });
        packBuilder.setPermissionMetaDataCollect(permissionMetaData);
        // 1.4 角色数据
        List<DeployRoleMetaData> roleMetaData = readFileToCollect("permission/roleMetaData.json", new TypeReference<List<DeployRoleMetaData>>() {
        });
        packBuilder.setRoleMetaDataCollect(roleMetaData);
        // 1.5 用户数据
        List<DeployUserMetaData> userMetaData = readFileToCollect("permission/userMetaData.json", new TypeReference<List<DeployUserMetaData>>() {
        });
        packBuilder.setUserMetaDataCollect(userMetaData);
        List<DeployRoleResourceMetaData> roleResourceMetaData = readFileToCollect("permission/roleResourceMetaData.json", new TypeReference<List<DeployRoleResourceMetaData>>() {
        });
        packBuilder.setRoleResourceMetaDataCollect(roleResourceMetaData);
        return packBuilder;
    }

    private <T> T readFileToCollect(String filePath, TypeReference<T> typeReference) {
        ClassPathResource classPathResource = new ClassPathResource(filePath);
        InputStream inputStream = null;
        try {
            inputStream = classPathResource.getInputStream();
        } catch (IOException e) {
            log.error("应用启动时权限数据 {} 读取失败 {}", filePath, e);
            return null;
        }
        T readValue = null;
        try {
            readValue = objectMapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            log.error("应用启动时权限数据 {} 转换失败 {}", filePath, e);
            return null;
        }
        return readValue;
    }
}
