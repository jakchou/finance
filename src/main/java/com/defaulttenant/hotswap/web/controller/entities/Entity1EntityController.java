package com.defaulttenant.hotswap.web.controller.entities;

import java.io.Serializable;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.math.BigDecimal;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;

import com.defaulttenant.hotswap.exception.HttpCodeException;
import com.defaulttenant.hotswap.domain.entities.Entity1Entity;
import com.defaulttenant.hotswap.domain.enumeration.*;
import com.defaulttenant.hotswap.service.entities.Entity1EntityService;
import com.defaulttenant.hotswap.web.ApiReturn;
import com.defaulttenant.hotswap.service.dto.filters.EntityFilter;
import com.defaulttenant.hotswap.service.dto.filters.AbstractQueryFilter;
import com.defaulttenant.hotswap.service.dto.filters.FilterWrapper;
import com.defaulttenant.hotswap.domain.PageOf;
import com.defaulttenant.hotswap.util.JacksonUtils;

/**
* auto generate Entity1Entity controller
*
* @author sys
*/
@RestController
public class Entity1EntityController {
    @Resource
    private Entity1EntityService service;

    /**
    * auto gen import method
    **/
    @PostMapping("/api/entity1/import")
    public ApiReturn<String> importEntities(@RequestParam("file") MultipartFile file) {
        return ApiReturn.of(service.importFile(file));
    }
}