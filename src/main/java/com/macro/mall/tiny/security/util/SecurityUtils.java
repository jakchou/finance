package com.macro.mall.tiny.security.util;

import com.macro.mall.tiny.domain.AdminUserDetails;
import com.macro.mall.tiny.modules.buss.dto.ColumnAthority;
import com.macro.mall.tiny.modules.ums.model.UmsResource;
import com.macro.mall.tiny.modules.ums.service.UmsResourceService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Spring工具类
 *
 * @author macro
 * @date 2020/3/3
 */
@Component
public class SecurityUtils {

    public static Boolean isAuthority(ColumnAthority columnAuthority){
        AdminUserDetails userDetails =getUserInfo();
        if(Objects.isNull(userDetails)){
            return false;
        }

        if(userDetails.getUmsAdmin().getRoleType() ==1){
            return true;
        }

        if(userDetails.getUmsAdmin().getRoleType() ==2){
            return true;
        }

        List<SimpleGrantedAuthority> authorities = (List<SimpleGrantedAuthority>) userDetails.getAuthorities();
        if(!CollectionUtils.isEmpty(authorities)){
           List <String> sourceList = authorities.stream().map(SimpleGrantedAuthority::getAuthority).collect(Collectors.toList());
           UmsResourceService resourceService = (UmsResourceService) SpringUtil.getBean("UmsResourceService");
           List<UmsResource> umsResources= resourceService.getSourceWithCache();
           Map<Long,UmsResource> map = umsResources.stream().collect(Collectors.toMap(e->e.getId(), e->e));
           UmsResource resource = umsResources.stream().filter(e->e.getRelationId().equals(columnAuthority.getColumnId())).findFirst().orElse(null);
           if(Objects.nonNull(resource)){
                if(resource.getHide() ==1 ){
                    resource = map.get(resource.getParentId());
                }
                if(sourceList.contains(resource.getId().toString())){
                    return true;
                }
           }
        }
        return false;
    };

    public static AdminUserDetails getUserInfo(){
        AdminUserDetails userDetails =null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            userDetails = (AdminUserDetails) principal;
        }
        return userDetails;
    };



}
