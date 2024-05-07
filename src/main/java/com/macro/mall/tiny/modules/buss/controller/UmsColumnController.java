package com.macro.mall.tiny.modules.buss.controller;
import com.macro.mall.tiny.common.annotation.OperationLogAnnotation;
import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.common.constant.BusinessType;
import com.macro.mall.tiny.modules.buss.dto.*;
import com.macro.mall.tiny.modules.buss.service.UmsColumnService;
import com.macro.mall.tiny.security.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 节点资源编辑
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
@RestController
@RequestMapping("/column")
public class UmsColumnController {

    @Autowired
    private UmsColumnService columnService;

    @Operation(summary = "模块资源查询")
    @RequestMapping(value = "/{menuId}/{columnId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult querySource(@PathVariable Long menuId, @PathVariable Long columnId) {
        UmsColumnDto source = columnService.querySource(menuId, columnId);
        return CommonResult.success(source);
    }

    @Operation(summary = "模块列表")
    @RequestMapping(value = "/{menuId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult queryColumn(@PathVariable Long menuId) {
        List<UmsColumnDto> sourceList = columnService.queryColumn(menuId);
        return CommonResult.success(sourceList);
    }


    @Operation(summary = "模块变更")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    @OperationLogAnnotation(title="节点资源变更-提交",businessType = BusinessType.INSERT)
    public CommonResult updateSource(@RequestBody UmsColumnChangeDto colum) {
        System.out.println(colum.toString());
        if(!isAuthority(colum.getId())){
            return CommonResult.failed("无权限");
        }
        Long submitId =  columnService.updateSource(colum);
        return CommonResult.success(submitId);
    }

    @Operation(summary = "模块审核")
    @RequestMapping(value = "/review", method = RequestMethod.POST)
    @ResponseBody
    @OperationLogAnnotation(title="节点资源变更-提交",businessType = BusinessType.INSERT)
    public CommonResult reviewSource(@RequestBody UmsNodeChangeReviewDto reviewDto) {
        if(!isAuthority(null)){
            return CommonResult.failed("无权限");
        }
        columnService.reviewSource(reviewDto);
        return CommonResult.success(null);
    }

    @Operation(summary = "模块审核列表")
    @RequestMapping(value = "/review-list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult reviewSourceList() {
        List<UmsColumnChangeDto> source = columnService.reviewSourceList();
        return CommonResult.success(source);
    }

    @Operation(summary = "模块变更明细")
    @RequestMapping(value = "/review/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult reviewSourceDetail(@PathVariable Long id) {
        UmsColumnChangeDto source = columnService.reviewSourceDetail(id);
        return CommonResult.success(source);
    }

   /* @Operation(summary = "节点资源删除")
    @RequestMapping(value = "/{menuId}/{columnId}/{nodeId}", method = RequestMethod.DELETE)
    @ResponseBody
    @OperationLogAnnotation(title="节点资源变更-删除",businessType = BusinessType.INSERT)
    public CommonResult deleteSource(@PathVariable Long menuId, @PathVariable Long columnId, @PathVariable Long nodeId) {
        if(!isAuthority(columnId)){
            return CommonResult.failed("无权限");
        }
        UmsNodeDto nodeDto = new UmsNodeDto();
        nodeDto.setColumnId(columnId);
        nodeDto.setId(nodeId);
        columnService.deleteSource(nodeDto);
        return CommonResult.success(null);
    }*/

    @Operation(summary = "新闻资源变更")
    @RequestMapping(value = "/news", method = RequestMethod.POST)
    @ResponseBody
    @OperationLogAnnotation(title="新闻资源变更",businessType = BusinessType.INSERT)
    public CommonResult saveNewSource(@RequestBody UmsNodeChangeDetailDto detailDto) {
        if(!isAuthority(detailDto.getColumnId())){
            return CommonResult.failed("无权限");
        }
        Long submitId = columnService.saveNewSource(detailDto);
        return CommonResult.success(submitId);
    }

    /*@Operation(summary = "新闻资源修改")
    @RequestMapping(value = "/news", method = RequestMethod.PUT)
    @ResponseBody
    @OperationLogAnnotation(title="新闻资源修改",businessType = BusinessType.UPDATE)
    public CommonResult updateNewSource(@RequestBody UmsNodeChangeDetailDto detailDto) {
        if(!isAuthority(detailDto.getColumnId())){
            return CommonResult.failed("无权限");
        }
        Long submitId =columnService.updateNewSource(detailDto);
        return CommonResult.success(submitId);
    }*/


    @Operation(summary = "新闻资源明细")
    @RequestMapping(value = "/news/detail/{columnId}/{newId}", method = RequestMethod.GET)
    @ResponseBody
    @OperationLogAnnotation(title="新闻资源修改",businessType = BusinessType.UPDATE)
    public CommonResult newSourceDetail(@PathVariable Long columnId,@PathVariable Long newId) {
        UmsNodeDto nodeDto = columnService.newSourceDetail(columnId,newId);
        return CommonResult.success(nodeDto);
    }

    @Operation(summary = "新闻资源列表")
    @RequestMapping(value = "/news/{menuId}/{columnId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult newSourceList(@PathVariable Long menuId,@PathVariable Long columnId) {
        List<UmsNodeDto> nodeDtoList = columnService.newSourceList(menuId,columnId);
        return CommonResult.success(nodeDtoList);
    }

    private boolean isAuthority(Long columnId){
        ColumnAthority columnAthority = new ColumnAthority();
        columnAthority.setColumnId(columnId);
        return SecurityUtils.isAuthority(columnAthority);
    }

    @Operation(summary = "新闻资源删除")
    @RequestMapping(value = "/news/{columnId}/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @OperationLogAnnotation(title="新闻资源删除",businessType = BusinessType.DELETE)
    public CommonResult deleteNewsSource(@PathVariable Long columnId,@PathVariable Long id) {
        if(!isAuthority(columnId)){
            return CommonResult.failed("无权限");
        }
        UmsNodeDto news = new UmsNodeDto();
        news.setId(id);
        news.setColumnId(columnId);
        columnService.deleteNewsSource(news);
        return CommonResult.success(null);
    }

    @Operation(summary = "岗位资源新增")
    @RequestMapping(value = "/jobs", method = RequestMethod.POST)
    @ResponseBody
    @OperationLogAnnotation(title="岗位资源新增",businessType = BusinessType.INSERT)
    public CommonResult saveJobSource(@RequestBody UmsWorkChangeDto workDto) {
        if(!isAuthority(Long.valueOf(workDto.getColumnId()+"01"))){
            return CommonResult.failed("无权限");
        }
        columnService.saveJobSource(workDto);
        return CommonResult.success(null);
    }

    @Operation(summary = "岗位资源列表")
    @RequestMapping(value = "/jobs", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult jobList() {
        List<UmsWorkDto> workDtoList = columnService.jobList();
        return CommonResult.success(workDtoList);
    }

    @Operation(summary = "岗位资源明细")
    @RequestMapping(value = "/jobs/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult jobDetail(@PathVariable Long id) {
        UmsWorkDto workDto = columnService.jobDetail(id);
        return CommonResult.success(workDto);
    }

    @Operation(summary = "岗位资源修改")
    @RequestMapping(value = "/jobs", method = RequestMethod.PUT)
    @ResponseBody
    @OperationLogAnnotation(title="岗位资源修改",businessType = BusinessType.UPDATE)
    public CommonResult updateJobSource(@RequestBody UmsWorkChangeDto workDto) {
        if(!isAuthority(Long.valueOf(workDto.getColumnId()+"02"))){
            return CommonResult.failed("无权限");
        }
        columnService.updateJobSource(workDto);
        return CommonResult.success(null);
    }

    @Operation(summary = "岗位资源删除")
    @RequestMapping(value = "/jobs/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @OperationLogAnnotation(title="岗位资源删除",businessType = BusinessType.DELETE)
    public CommonResult deleteJobSource(@PathVariable Long id) {
        UmsWorkDto workDto = new UmsWorkDto();
        workDto.setId(id);
        columnService.deleteJobSource(workDto);
        return CommonResult.success(null);
    }











}
