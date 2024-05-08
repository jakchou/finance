package com.defaulttenant.hotswap.iam.permission;

import com.defaulttenant.hotswap.config.Constants;
import com.defaulttenant.hotswap.service.entities.*;
import com.defaulttenant.hotswap.task.permission.model.*;
import com.defaulttenant.hotswap.domain.entities.*;
import com.defaulttenant.hotswap.service.logics.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* 应用启动时权限数据处理类
*
* @author sys
* @since 3.0
*/
@Service
@DependsOn("initDatabaseConfig")
public class AppStartPermissionDataService  implements InitializingBean {

    private final Logger log = LoggerFactory.getLogger(AppStartPermissionDataService.class);
    public static final String USER_SOURCE_ENUM_PREFIX_FIELD = "FIELD_";

    private String clientTypes = "pc";
    private String allClientTypes = "pc";
    private List<String> allClientTypeList =  new ArrayList<>();
    private List<String> targetClientTypeList = new ArrayList<>();

    public void permissionDataProcess(DeployPermissionPack permissionPack) throws Exception {
        // 2. 执行默认权限数据上报逻辑
        // 如果没有扩展实现 就走默认上报权限的逻辑
        defaultUploadPermission(
                permissionPack.getResourceMetaDataCollect(),
                permissionPack.getPermissionMetaDataCollect(),
                permissionPack.getRoleMetaDataCollect(),
                permissionPack.getUserMetaDataCollect(),
                permissionPack.getRoleResourceMetaDataCollect()
        );
    }

    private void defaultUploadPermission(List<DeployResourceMetaData> resourceMetaData,
                                         List<DeployPermissionMetaData> permissionMetaData,
                                         List<DeployRoleMetaData> roleMetaData,
                                         List<DeployUserMetaData> userMetaData,
                                         List<DeployRoleResourceMetaData> roleResourceMetaData) throws Exception {
        Instant now = Instant.now();
        ZonedDateTime nowZonedDateTime = now.atZone(ZoneId.systemDefault());
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        if(StringUtils.isNotEmpty(allClientTypes)){
            allClientTypeList = Arrays.asList(allClientTypes.split(","));
            if(StringUtils.isNotEmpty(clientTypes)){
                targetClientTypeList.addAll(Arrays.asList(clientTypes.split(",")));
            }
        }
    }
}
