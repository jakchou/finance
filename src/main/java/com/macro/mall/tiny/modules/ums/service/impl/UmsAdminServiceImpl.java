package com.macro.mall.tiny.modules.ums.service.impl;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.sql.Order;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hazelcast.core.HazelcastInstance;
import com.macro.mall.tiny.common.constant.Constant;
import com.macro.mall.tiny.common.exception.Asserts;
import com.macro.mall.tiny.config.OrikaMapper;
import com.macro.mall.tiny.domain.AdminUserDetails;
import com.macro.mall.tiny.modules.buss.model.UmsAdminSourceRelation;
import com.macro.mall.tiny.modules.buss.service.UmsAdminSourceRelationService;
import com.macro.mall.tiny.modules.ums.dto.UmsAdminParam;
import com.macro.mall.tiny.modules.ums.dto.UmsResourceDto;
import com.macro.mall.tiny.modules.ums.dto.UpdateAdminPasswordParam;
import com.macro.mall.tiny.modules.ums.mapper.UmsAdminLoginLogMapper;
import com.macro.mall.tiny.modules.ums.mapper.UmsAdminMapper;
import com.macro.mall.tiny.modules.ums.mapper.UmsResourceMapper;
import com.macro.mall.tiny.modules.ums.mapper.UmsRoleMapper;
import com.macro.mall.tiny.modules.ums.model.*;
import com.macro.mall.tiny.modules.ums.service.UmsAdminRoleRelationService;
import com.macro.mall.tiny.modules.ums.service.UmsAdminService;
import com.macro.mall.tiny.modules.ums.service.UmsResourceService;
import com.macro.mall.tiny.security.util.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 后台管理员管理Service实现类
 * Created by macro on 2018/4/26.
 */
@Service
public class UmsAdminServiceImpl extends ServiceImpl<UmsAdminMapper, UmsAdmin> implements UmsAdminService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UmsAdminServiceImpl.class);
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UmsAdminLoginLogMapper loginLogMapper;
    @Autowired
    private UmsAdminRoleRelationService adminRoleRelationService;
    @Autowired
    private UmsRoleMapper roleMapper;
    @Autowired
    private UmsResourceMapper resourceMapper;

    @Autowired
    private UmsResourceService resourceService;
    @Autowired
    private UmsAdminSourceRelationService adminSourceRelationService;
    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Override
    public UmsAdmin getAdminByUsername(String username) {
        UmsAdmin admin = null;
        QueryWrapper<UmsAdmin> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsAdmin::getUsername,username);
        List<UmsAdmin> adminList = list(wrapper);
        if (adminList != null && adminList.size() > 0) {
            admin = adminList.get(0);
            hazelcastInstance.getMap(Constant.CACHE_IMAP_NAME+"admin").put(admin.getId(),admin);
            return admin;
        }
        return null;
    }

    @Override
    public UmsAdmin register(UmsAdminParam umsAdminParam) {
        UmsAdmin umsAdmin = new UmsAdmin();
        BeanUtils.copyProperties(umsAdminParam, umsAdmin);
        umsAdmin.setCreateTime(new Date());
        umsAdmin.setStatus(1);
        //查询是否有相同用户名的用户
        QueryWrapper<UmsAdmin> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsAdmin::getUsername,umsAdmin.getUsername());
        List<UmsAdmin> umsAdminList = list(wrapper);
        if (umsAdminList.size() > 0) {
            return null;
        }
        //将密码进行加密操作
        String encodePassword = passwordEncoder.encode(umsAdmin.getPassword());
        umsAdmin.setPassword(encodePassword);
        baseMapper.insert(umsAdmin);
        return umsAdmin;
    }

    @Override
    public String login(String username, String password) {
        String token = null;
        //密码需要客户端加密后传递
        try {
            UserDetails userDetails = loadUserByUsername(username);
            if(!passwordEncoder.matches(password,userDetails.getPassword())){
                Asserts.fail("密码不正确");
            }
            if(!userDetails.isEnabled()){
                Asserts.fail("帐号已被禁用");
            }


            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            AdminUserDetails adminUserDetails = (AdminUserDetails) userDetails;
            token = jwtTokenUtil.generateToken(adminUserDetails);
//            updateLoginTimeByUsername(username);
            insertLoginLog(username);
        } catch (AuthenticationException e) {
            LOGGER.warn("登录异常:{}", e.getMessage());
        }
        return token;
    }

    /**
     * 添加登录记录
     * @param username 用户名
     */
    private void insertLoginLog(String username) {
        UmsAdmin admin = getAdminByUsername(username);
        if(admin==null) return;
        UmsAdminLoginLog loginLog = new UmsAdminLoginLog();
        loginLog.setAdminId(admin.getId());
        loginLog.setCreateTime(new Date());
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        loginLog.setIp(request.getRemoteAddr());
        loginLogMapper.insert(loginLog);
    }

    /**
     * 根据用户名修改登录时间
     */
    private void updateLoginTimeByUsername(String username) {
        UmsAdmin record = new UmsAdmin();
        record.setLoginTime(new Date());
        QueryWrapper<UmsAdmin> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsAdmin::getUsername,username);
        update(record,wrapper);
    }

    @Override
    public String refreshToken(String oldToken) {
        return jwtTokenUtil.refreshHeadToken(oldToken);
    }

    @Override
    public String generateRefreshToken(String username) {
        UserDetails userDetails = loadUserByUsername(username);
        AdminUserDetails adminUserDetails = (AdminUserDetails) userDetails;
        return   null;//jwtTokenUtil.generateRefreshToken(adminUserDetails);
    }

    @Override
    public Page<UmsAdmin> list(String keyword, Integer pageSize, Integer pageNum) {
        Page<UmsAdmin> page = new Page<>(pageNum,pageSize);
        QueryWrapper<UmsAdmin> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<UmsAdmin> lambda = wrapper.lambda();
        if(StrUtil.isNotEmpty(keyword)){
            lambda.like(UmsAdmin::getUsername,keyword);
            lambda.or().like(UmsAdmin::getNickName,keyword);
        }
        return page(page,wrapper);
    }

    @Override
    public boolean update(Long id, UmsAdmin admin) {
        admin.setId(id);
        UmsAdmin rawAdmin = getById(id);
        if(rawAdmin.getPassword().equals(admin.getPassword())){
            //与原加密密码相同的不需要修改
            admin.setPassword(null);
        }else{
            //与原加密密码不同的需要加密修改
            if(StrUtil.isEmpty(admin.getPassword())){
                admin.setPassword(null);
            }else{
                admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            }
        }
        boolean success = updateById(admin);
       // getCacheService().delAdmin(id);
        return success;
    }

    @Override
    public boolean delete(Long id) {
        //getCacheService().delAdmin(id);
        boolean success = removeById(id);
       // getCacheService().delResourceList(id);
        return success;
    }

    @Override
    public int updateRole(Long adminId, List<Long> roleIds) {
        int count = roleIds == null ? 0 : roleIds.size();
        //先删除原来的关系
        QueryWrapper<UmsAdminRoleRelation> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsAdminRoleRelation::getAdminId,adminId);
        adminRoleRelationService.remove(wrapper);
        //建立新关系
        if (!CollectionUtils.isEmpty(roleIds)) {
            List<UmsAdminRoleRelation> list = new ArrayList<>();
            for (Long roleId : roleIds) {
                UmsAdminRoleRelation roleRelation = new UmsAdminRoleRelation();
                roleRelation.setAdminId(adminId);
                roleRelation.setRoleId(roleId);
                list.add(roleRelation);
            }
            adminRoleRelationService.saveBatch(list);
        }
       // getCacheService().delResourceList(adminId);
        return count;
    }

    @Override
    public List<UmsRole> getRoleList(Long adminId) {
        return roleMapper.getRoleList(adminId);
    }

    @Override
    public List<UmsResource> getResourceList(Long adminId) {
      /* List<UmsResource> resourceList = getCacheService().getResourceList(adminId);
        if(CollUtil.isNotEmpty(resourceList)){
            return  resourceList;
        }
        resourceList = resourceMapper.getResourceList(adminId);
        if(CollUtil.isNotEmpty(resourceList)){
            getCacheService().setResourceList(adminId,resourceList);
        }
        return resourceList;*/
       return null;
    }

    @Override
    public int updatePassword(UpdateAdminPasswordParam param) {
        if(StrUtil.isEmpty(param.getUsername())
                ||StrUtil.isEmpty(param.getOldPassword())
                ||StrUtil.isEmpty(param.getNewPassword())){
            return -1;
        }
        QueryWrapper<UmsAdmin> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsAdmin::getUsername,param.getUsername());
        List<UmsAdmin> adminList = list(wrapper);
        if(CollUtil.isEmpty(adminList)){
            return -2;
        }
        UmsAdmin umsAdmin = adminList.get(0);
        if(!passwordEncoder.matches(param.getOldPassword(),umsAdmin.getPassword())){
            return -3;
        }
        umsAdmin.setPassword(passwordEncoder.encode(param.getNewPassword()));
        updateById(umsAdmin);
        //getCacheService().delAdmin(umsAdmin.getId());
        return 1;
    }

    @Override
    public UserDetails loadUserByUsername(String username){
        //获取用户信息
        UmsAdmin admin = getAdminByUsername(username);
        if (admin != null && admin.getRoleType() >= 1) {
            List<UmsResourceDto> resourceList = getUserResource(admin.getId());
            List<UmsResource> resources = OrikaMapper.mapList(resourceList,UmsResourceDto.class,UmsResource.class);
            return new AdminUserDetails(admin,resources);
        }
        throw new UsernameNotFoundException("用户名或密码错误");
    }

    @Override
    public List<UmsResourceDto> getUserResource(Long adminId) {
        List<UmsAdminSourceRelation> relations = adminSourceRelationService.selectListByAdmin(adminId);
        if(CollectionUtils.isEmpty(relations)){
            return  new ArrayList<>();
        }
        List<Long> sourceIds = relations.stream().map(UmsAdminSourceRelation::getResourceId).collect(Collectors.toList());
        List<UmsResource> cache = resourceService.getSourceWithCache();
        List<UmsResourceDto> sourceList = OrikaMapper.mapList(cache,UmsResource.class,UmsResourceDto.class);

        List<UmsResourceDto> tmpList =new ArrayList<>();
        for (UmsResourceDto resource: sourceList){
           if(sourceIds.contains(resource.getId())){
               tmpList.add(resource);
           }
        }
        Map<Long,UmsResourceDto> resourceDtoMap = sourceList.stream().collect(Collectors.toMap(UmsResourceDto::getId, e->e));
        Map<Long,List<UmsResourceDto>> chileList = sourceList.stream().collect(Collectors.groupingBy(UmsResourceDto::getParentId));
        List<UmsResourceDto> resultList =new ArrayList<>();
        for (UmsResourceDto resource: tmpList){
            resultList.add(resource);
            if(resource.getLevel() ==4){
                UmsResourceDto three = resourceDtoMap.get(resource.getParentId());
                UmsResourceDto two = resourceDtoMap.get(three.getParentId());
                UmsResourceDto one = resourceDtoMap.get(two.getParentId());
                resultList.add(three);
                resultList.add(two);
                resultList.add(one);
            }else if(resource.getLevel() ==3){
                UmsResourceDto two = resourceDtoMap.get(resource.getParentId());
                UmsResourceDto one = resourceDtoMap.get(two.getParentId());
                resultList.add(two);
                resultList.add(one);
                List<UmsResourceDto> fourList = chileList.get(resource.getId());
                if(!CollectionUtils.isEmpty(fourList)){
                    fourList = fourList.stream().filter(e->e.getHide() ==1 ).collect(Collectors.toList());
                    resultList.addAll(fourList);
                }
            }else if(resource.getLevel() ==2){
                UmsResourceDto one = resourceDtoMap.get(resource.getParentId());
                resultList.add(one);
                List<UmsResourceDto> threeList = chileList.get(resource.getId());
                if(!CollectionUtils.isEmpty(threeList)){
                    threeList =threeList.stream().filter(e->e.getHide() == 1).collect(Collectors.toList());
                    resultList.addAll(threeList);
                    for (UmsResourceDto three: threeList){
                        List<UmsResourceDto> fourList = chileList.get(three.getId());
                        if(!CollectionUtils.isEmpty(fourList)){
                            fourList =fourList.stream().filter(e->e.getHide() == 1).collect(Collectors.toList());
                            resultList.addAll(fourList);
                        }

                    }
                }
            }
        }
        resultList=resultList.stream().filter(distinctByKey(UmsResourceDto::getId)).collect(Collectors.toList());
        //Map<Long,List<UmsResourceDto>> childList = resultList.stream().collect(Collectors.groupingBy(UmsResourceDto::getParentId));
        return resultList;
    }

    private static <T> Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
        Set<Object> seen = new HashSet<>();
        return t -> seen.add(keyExtractor.apply(t));
    }

    @Override
    public int updateSource(Long adminId, List<Long> sourceIds) {
        List<UmsAdminSourceRelation> relationsList =new ArrayList<>();
        List<UmsResource> temp =resourceService.getSourceWithCache();
        Map<Long,UmsResource> map =temp.stream().collect(Collectors.toMap(UmsResource::getId, e->e));
        for (Long sourId :sourceIds){
            UmsResource resource = map.get(sourId);
             if(Objects.nonNull(resource)){
                 UmsAdminSourceRelation relation =new UmsAdminSourceRelation();
                 relation.setAdminId(adminId);
                 relation.setResourceId(resource.getId());
                 relationsList.add(relation);
             }
        }
        adminSourceRelationService.saveBatchs(relationsList);
        return 0;
    }

}
