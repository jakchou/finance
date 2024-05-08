package com.defaulttenant.hotswap.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * UI controller for web.
 * Used to return static resource data.
 *
 * @author sys
 */
@RestController
public class UiController extends BaseUiController {

    @GetMapping("/")
    public String uiResource() {
        return getIndexHtml("", concatPath(lcpProperties.getUiResourceAddress(), JS_FILE_NAME));
    }

    /**
     * default page for not found
     * @param response
     * @return
     */
    @GetMapping(value = "/error", produces = {"text/html"})
    public String error(HttpServletRequest request, HttpServletResponse response) {
        return handleErrorRequest(request, response);
    }
}
