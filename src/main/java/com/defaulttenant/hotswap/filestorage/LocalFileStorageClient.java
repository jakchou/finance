package com.defaulttenant.hotswap.filestorage;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.defaulttenant.hotswap.config.Constants;
import com.defaulttenant.hotswap.domain.enumeration.ErrorCodeEnum;
import com.defaulttenant.hotswap.exception.HttpCodeException;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;

/**
 * 本地文件系统实现
 *
 * @author sys
 * @date 2022-06-06 13:36
 */
@Component
public class LocalFileStorageClient implements FileStorageClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileStorageClient.class);
    private static final String DEFAULT_BASE_PATH = System.getProperty("user.home");

    @Value("${file.types}")
    private String fileTypes;

    @Override
    public String type() {
        return Constants.UPLOAD_TYPE_LOCAL;
    }

    @Override
    public void init(Map<String, String> config) {
        LOGGER.info("本地文件系统客户端初始化完成");
    }

    @Override
    public String upload(InputStream inputStream, String targetPath, Map<String, String> payloads) throws IOException {
        File targetFile = new File(targetPath);
        //当前文件上传时，不会进行覆盖写，因为本地生成的文件名 都是根据时间戳来进行生成的
        if(targetFile.exists()){
            return targetPath;
        }
        targetFile.getParentFile().mkdirs();

        try (OutputStream outputStream = new FileOutputStream(targetFile)) {
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        }

        return targetPath;
    }

    @Override
    public void download(OutputStream outputStream, String targetPath, Map<String, String> payloads) throws IOException {
        if(!targetPath.startsWith(File.separator)){
            targetPath = File.separator + targetPath;
        }
        File targetFile = new File(targetPath);
        if (targetFile.exists()) {
            if (payloads.containsKey(FileStorageClient.SINGLE_DOWNLOAD) && Boolean.valueOf(payloads.get(FileStorageClient.SINGLE_DOWNLOAD))) {
                this.addContentLength(targetFile.length());
            }
            //黑名单中的文件，禁止浏览器直接预览
            filterTargetFile(targetFile);
            try (InputStream inputStream = new FileInputStream(targetFile)) {
                IOUtils.copyLarge(inputStream, outputStream);
            }
        } else {
            throw new HttpCodeException(404, ErrorCodeEnum.FILE_NOT_EXIST.desc);
        }
    }

    @Override
    public void delete(String targetPath, Map<String, String> payloads) throws IOException {
        if(!targetPath.startsWith(File.separator)){
            targetPath = File.separator + targetPath;
        }
        File targetFile = new File(targetPath);
        if (targetFile.exists()) {
            targetFile.delete();
        }
    }

    private void addContentLength(final long contentLength) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        //非浏览器请求不设置
        if (null == response) {
            return;
        }
        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
    }

    @Override
    public void filterTargetFile(Object fileObject) {
        File targetFile = (File) fileObject;
        //过滤掉targetFile中的特殊字符
        //修改content-type,通过文件下载的方式，浏览器会根据content-type来决定是否直接打开文件 希望通过配置的方式
        String fileType = null;
        try {
            fileType = Files.probeContentType(targetFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (fileType != null && (Arrays.asList(fileTypes.split(";"))).contains(fileType)) {
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment;filename=" + targetFile.getName());
        }
    }

}
