package com.macro.mall.tiny.modules.buss.controller;

import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.common.constant.Constant;
import com.macro.mall.tiny.common.constant.DictType;
import com.macro.mall.tiny.modules.buss.dto.BaseDictDto;
import com.macro.mall.tiny.modules.buss.dto.CaptchaImageVO;
import com.macro.mall.tiny.modules.buss.dto.UmsContractDto;
import com.macro.mall.tiny.modules.buss.dto.UmsDepartmentDto;
import com.macro.mall.tiny.modules.buss.service.BaseDictService;
import com.macro.mall.tiny.modules.buss.service.SystemService;
import com.macro.mall.tiny.modules.buss.service.UmsContractService;
import com.macro.mall.tiny.modules.buss.service.UmsDepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhouzz
 */
@Controller
@Tag(name = "CommonController",description = "公共请求")
public class CommonController {

    @Autowired
    private SystemService systemService;

    @Autowired
    private UmsContractService contractService;
    @Autowired
    private UmsDepartmentService departmentService;
    @Autowired
    private BaseDictService dictService;

    @RequestMapping(value = "/common/captcha/{key}", method = RequestMethod.GET)
    @ResponseBody
    @Operation(summary = "验证码")
    public CommonResult<CaptchaImageVO> captcha(@PathVariable(required = true) String key) throws Exception {
        CaptchaImageVO vo= systemService.getCaptchaImage(key);
        return CommonResult.success(vo);
    }


    @RequestMapping(value = "/common/departments", method = RequestMethod.GET)
    @ResponseBody
    @Operation(summary = "部门")
    public CommonResult departmentList(){
        List<UmsDepartmentDto> list = departmentService.selectList();
        return CommonResult.success(list);
    }
    @RequestMapping(value = "/common/recruitment", method = RequestMethod.GET)
    @ResponseBody
    @Operation(summary = "招聘类型")
    public CommonResult jobs(){
        List<Map> list =new ArrayList<>();
        List<BaseDictDto> dictList = dictService.listByDictType(DictType.Recruitment);
        if(CollectionUtils.isEmpty(dictList)){
            return CommonResult.success(list);
        }

        for(BaseDictDto dict:dictList){
            Map<String,Object> map =new HashMap<>();
            map.put("id",dict.getDictCode());
            map.put("name",dict.getDictValue());
            list.add(map);
        }
        return CommonResult.success(list);
    }

    @RequestMapping(value = "/common/educational", method = RequestMethod.GET)
    @ResponseBody
    @Operation(summary = "学历")
    public CommonResult educational(){
        List<Map> list =new ArrayList<>();
        List<BaseDictDto> dictList = dictService.listByDictType(DictType.Educational);
        if(CollectionUtils.isEmpty(dictList)){
            return CommonResult.success(list);
        }

        for(BaseDictDto dict:dictList){
            Map<String,Object> map =new HashMap<>();
            map.put("id",dict.getDictCode());
            map.put("name",dict.getDictValue());
            list.add(map);
        }
        return CommonResult.success(list);
    }

    @RequestMapping(value = "/common/contacts", method = RequestMethod.GET)
    @ResponseBody
    @Operation(summary = "联系人")
    public CommonResult contacts(){
        List<UmsContractDto> list = contractService.selectList();
        return CommonResult.success(list);
    }

    @RequestMapping(value ="/images/{filename:.+}", method = RequestMethod.GET)
    @ResponseBody
    @Operation(summary = "图片下载")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws MalformedURLException {
        Path filePath = Paths.get(Constant.IMAGE_PATH).resolve(filename).normalize();
        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("Fail to load file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Fail to load file: " + filename, e);
        }
        String contentType = determineContentType(filename);
        MediaType mediaType = MediaType.parseMediaType(contentType);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    private String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".")+1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            default:
                return "application/octet-stream"; // 通用的MIME类型
        }
    }

    @RequestMapping(value = "/common/upload", method = RequestMethod.POST)
    @ResponseBody
    @Operation(summary = "文件上传")
    public CommonResult handleFileUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        // 例如，将文件保存到服务器的某个位置
        try {
            // 获取请求协议，例如 http 或 https
            String scheme = request.getScheme();
            // 获取服务器名称，例如 localhost
            String serverName = request.getServerName();
            // 获取服务器端口号
            int serverPort = request.getServerPort();
            // 获取请求URI
            String requestURI = request.getRequestURI();
            // 获取查询字符串，如果请求中没有查询字符串，则为null
            String queryString = request.getQueryString();

            // 构建完整的请求路径
            String url = scheme + "://" + serverName + ":" + serverPort +"/images/";
            System.out.println(url);
            // 确保目录存在
            //获取文件名
            String fileName = file.getOriginalFilename();
            //获取文件后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            //重新生成文件名
            fileName = UUID.randomUUID()+suffixName;
            //指定本地文件夹存储图片
            try {
                File dir = new File( Constant.IMAGE_PATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File upload_file = new File( Constant.IMAGE_PATH + fileName);
                file.transferTo(upload_file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return CommonResult.success(url+fileName);
        } catch (Exception e) {
        }
        return CommonResult.failed();
    }

}
