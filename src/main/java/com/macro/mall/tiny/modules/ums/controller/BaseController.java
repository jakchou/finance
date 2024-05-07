package com.macro.mall.tiny.modules.ums.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhouzz
 */
public class BaseController {
    @Autowired
    protected HttpServletRequest request;
}
