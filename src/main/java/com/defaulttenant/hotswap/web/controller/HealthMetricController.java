package com.defaulttenant.hotswap.web.controller;

import org.springframework.boot.actuate.health.Health;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class HealthMetricController {

    @RequestMapping(value = "/management/health")
    @ResponseBody
    public Health health(HttpServletResponse response) {
        return Health.up().build();
    }
}
