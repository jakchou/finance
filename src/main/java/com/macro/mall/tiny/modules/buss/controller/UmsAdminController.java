package com.macro.mall.tiny.modules.buss.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hazelcast.core.HazelcastInstance;
import com.macro.mall.tiny.common.annotation.OperationLogAnnotation;
import com.macro.mall.tiny.common.api.CommonPage;
import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.common.constant.BusinessType;
import com.macro.mall.tiny.common.constant.Constant;
import com.macro.mall.tiny.modules.buss.dto.UmsContractDto;
import com.macro.mall.tiny.modules.buss.model.UmsAdminSourceRelation;
import com.macro.mall.tiny.modules.buss.service.UmsAdminSourceRelationService;
import com.macro.mall.tiny.modules.buss.service.UmsContractService;
import com.macro.mall.tiny.modules.ums.controller.BaseController;
import com.macro.mall.tiny.modules.ums.dto.UmsAdminLoginParam;
import com.macro.mall.tiny.modules.ums.dto.UmsAdminParam;
import com.macro.mall.tiny.modules.ums.dto.UmsResourceDto;
import com.macro.mall.tiny.modules.ums.dto.UpdateAdminPasswordParam;
import com.macro.mall.tiny.modules.ums.model.UmsAdmin;
import com.macro.mall.tiny.modules.ums.model.UmsRole;
import com.macro.mall.tiny.modules.ums.service.UmsAdminService;
import com.macro.mall.tiny.modules.ums.service.UmsRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 后台用户管理
 * Created by macro on 2018/4/26.
 */
@Controller
@Tag(name = "UmsAdminController",description = "后台用户管理")
@RequestMapping("/admin")
public class UmsAdminController extends BaseController {
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Autowired
    private UmsAdminService adminService;
    @Autowired
    private UmsAdminSourceRelationService adminSourceRelationService;
    @Autowired
    private UmsRoleService roleService;
    @Autowired
    private UmsContractService contractService;
    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Operation(summary = "用户注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    @OperationLogAnnotation(title="用户模块-注册",businessType = BusinessType.INSERT)
    public CommonResult<UmsAdmin> register(@Validated @RequestBody UmsAdminParam umsAdminParam) {
        UmsAdmin umsAdmin = adminService.register(umsAdminParam);
        if (umsAdmin == null) {
            return CommonResult.failed();
        }
        return CommonResult.success(umsAdmin);
    }

    @Operation(summary = "登录以后返回token")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    @OperationLogAnnotation(title="用户模块-登录")
    public CommonResult login(@Validated @RequestBody UmsAdminLoginParam umsAdminLoginParam) {
        System.out.println(umsAdminLoginParam.toString());
        /* String value = (String) hazelcastInstance.getMap(Constant.CACHE_IMAP_NAME+Constant.CACHE_VERIFY).get(umsAdminLoginParam.getVerifyCodeKey());
        if (Objects.isNull(value)) {
            return CommonResult.validateFailed("验证码已过期");
        }
        if (!value.equals(umsAdminLoginParam.getVerifyCode())) {
            return CommonResult.validateFailed("验证码错误");
        }
       String decodePassword = new String(Base64.getDecoder().decode(umsAdminLoginParam.getPassword()));
       umsAdminLoginParam.setPassword(decodePassword);*/

        String token = adminService.login(umsAdminLoginParam.getUsername(), umsAdminLoginParam.getPassword());
        if (token == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @Operation(summary = "刷新token")
    @RequestMapping(value = "/refreshToken", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult refreshToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        String refreshToken = adminService.refreshToken(token);
        if (refreshToken == null) {
            return CommonResult.failed("token已经过期！");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", refreshToken);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @Operation(summary = "获取当前登录用户信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getAdminInfo(Principal principal) {
        if(principal==null){
            return CommonResult.unauthorized(null);
        }
        String username = principal.getName();
        UmsAdmin umsAdmin = adminService.getAdminByUsername(username);
        Map<String, Object> data = new HashMap<>();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", umsAdmin.getId());
        userInfo.put("username", umsAdmin.getUsername());
        data.put("userInfo", userInfo);
       /* List<UmsResourceDto> userResource = adminService.getUserResource(umsAdmin.getId());
        if(CollUtil.isNotEmpty(userResource)){
            data.put("resourceList",userResource);
        }*/
        List<UmsAdminSourceRelation> relations = adminSourceRelationService.selectListByAdmin(umsAdmin.getId());
        if(CollUtil.isNotEmpty(relations)){
            List<Long> resourceList = relations.stream().map(UmsAdminSourceRelation::getResourceId).collect(Collectors.toList());
            data.put("resourceList",resourceList);
        }
        return CommonResult.success(data);
    }

    @Operation(summary = "登出功能")
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult logout() {
        rejectAuthorize();
        return CommonResult.success(null);
    }

    @Operation(summary ="根据用户名或姓名分页获取用户列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsAdmin>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                                   @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                   @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        Page<UmsAdmin> adminList = adminService.list(keyword, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(adminList));
    }

    @Operation(summary = "获取指定用户信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getItem(@PathVariable Long id) {
        UmsAdmin admin = adminService.getById(id);
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", admin.getId());
        userInfo.put("userName",admin.getUsername());
        userInfo.put("roleType",admin.getRoleType());
        data.put("userInfo", userInfo);
        List<UmsAdminSourceRelation> relations = adminSourceRelationService.selectListByAdmin(admin.getId());
        if(CollUtil.isNotEmpty(relations)){
            List<Long> resourceList = relations.stream().map(UmsAdminSourceRelation::getResourceId).collect(Collectors.toList());
            data.put("resourceList",resourceList);
        }
        return CommonResult.success(data);
    }

    @Operation(summary = "修改指定用户信息")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    @OperationLogAnnotation(title="用户模块-更新",businessType = BusinessType.UPDATE)
    public CommonResult update(@PathVariable Long id, @RequestBody UmsAdmin admin) {
        boolean success = adminService.update(id, admin);
        if (success) {
            rejectAuthorize();
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "修改指定用户密码")
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    @OperationLogAnnotation(title="用户模块-更新",businessType = BusinessType.INSERT)
    public CommonResult updatePassword(@Validated @RequestBody UpdateAdminPasswordParam updatePasswordParam) {
        int status = adminService.updatePassword(updatePasswordParam);
        if (status > 0) {
            rejectAuthorize();
            return CommonResult.success(status);
        } else if (status == -1) {
            return CommonResult.failed("提交参数不合法");
        } else if (status == -2) {
            return CommonResult.failed("找不到该用户");
        } else if (status == -3) {
            return CommonResult.failed("旧密码错误");
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "删除指定用户信息")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    @OperationLogAnnotation(title="用户模块-删除",businessType = BusinessType.DELETE)
    public CommonResult delete(@PathVariable Long id) {
        boolean success = adminService.delete(id);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "修改帐号状态")
    @RequestMapping(value = "/updateStatus/{id}", method = RequestMethod.POST)
    @ResponseBody
    @OperationLogAnnotation(title="用户模块-更新",businessType = BusinessType.UPDATE)
    public CommonResult updateStatus(@PathVariable Long id,@RequestParam(value = "status") Integer status) {
        UmsAdmin umsAdmin = new UmsAdmin();
        umsAdmin.setStatus(status);
        boolean success = adminService.update(id,umsAdmin);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    /*@Operation(summary = "给用户分配角色")
    @RequestMapping(value = "/role/update", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateRole(@RequestParam("adminId") Long adminId,
                                   @RequestParam("roleIds") List<Long> roleIds) {
        int count = adminService.updateRole(adminId, roleIds);
        if (count >= 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }*/

   /* @Operation(summary = "获取指定用户的角色")
    @RequestMapping(value = "/role/{adminId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<UmsRole>> getRoleList(@PathVariable Long adminId) {
        List<UmsRole> roleList = adminService.getRoleList(adminId);
        return CommonResult.success(roleList);
    }*/

    @Operation(summary = "给用户分资源")
    @RequestMapping(value = "/resource/update", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateSource(@RequestParam("adminId") Long adminId,
                                   @RequestParam("sourceIds") List<Long> sourceIds) {
        int count = adminService.updateSource(adminId, sourceIds);
        if (count >= 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "获取用户资源")
    @RequestMapping(value = "/resource/{adminId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getUserResource(@PathVariable Long adminId) {
        //List<UmsResourceDto> userResource = adminService.getUserResource(adminId);
        List userResource = new ArrayList();
        List<UmsAdminSourceRelation> relations = adminSourceRelationService.selectListByAdmin(adminId);
        if(!CollectionUtils.isEmpty(relations)){
            userResource = relations.stream().map(UmsAdminSourceRelation::getResourceId).collect(Collectors.toList());
        }
        return CommonResult.success(userResource);
    }


    @RequestMapping(value = "/contacts", method = RequestMethod.POST)
    @ResponseBody
    @Operation(summary = "联系人")
    public CommonResult contacts(UmsContractDto contractDto){
        contractService.insert(contractDto);
        return CommonResult.success(null);
    }

    //更新或者修改密码后，需要重新登录
    private void rejectAuthorize(){
        String authHeader = request.getHeader(this.tokenHeader);
        //黑名单用户用户变更后重新登陆
        if (authHeader != null && authHeader.startsWith(this.tokenHead)) {
            String authToken = authHeader.substring(this.tokenHead.length());
            hazelcastInstance.getMap(Constant.CACHE_IMAP_NAME+Constant.MODEL_TOKEN).put(authToken, 1, 1,TimeUnit.HOURS);
        }
    }
}
