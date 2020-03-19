package com.lvmm.shardingjdbc.controller;

import com.lvmm.shardingjdbc.common.ApiUriTemplates;
import com.lvmm.shardingjdbc.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiUriTemplates.V1 + "/user")
@Api(value = ApiUriTemplates.APP_ROOT_PATH)
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/testReq")
    @ApiOperation("测试请求")
    String home() {
        return "Hello World!";
    }

    @PostMapping("/findAll")
    @ApiOperation("查询所有用户")
    public void findAll() {
        userService.findAll();
    }
}
