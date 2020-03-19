package com.lvmm.shardingjdbc.controller;

import com.lvmm.shardingjdbc.common.ApiUriTemplates;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiUriTemplates.V1 + "/user")
@Api(value = ApiUriTemplates.APP_ROOT_PATH)
public class UserController {
    @GetMapping("/testReq")
    @ApiOperation("测试请求")
    String home() {
        return "Hello World!";
    }
}
