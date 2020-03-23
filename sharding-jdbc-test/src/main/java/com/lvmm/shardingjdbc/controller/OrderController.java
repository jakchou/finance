package com.lvmm.shardingjdbc.controller;

import com.lvmm.shardingjdbc.common.ApiUriTemplates;
import com.lvmm.shardingjdbc.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiUriTemplates.V1 + "/order")
@Api(value = ApiUriTemplates.APP_ROOT_PATH)
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/findByOrderId")
    @ApiOperation("根据订单号查询订单")
    public String findByOrderId(Long orderId) {
        orderService.findByOrderId(orderId);
        return "success";
    }


}
