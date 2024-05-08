package com.defaulttenant.hotswap.filestorage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * 文件服务器拓展点
 *
 * @author sys
 * @date 2022-05-30 14:56
 */
public interface FileStorageClient {
    /**
     * @since 3.3 多文件下载时, 不应该设置content-length
     */
    String SINGLE_DOWNLOAD = "singleDownload";
    /**
     * @since 3.3 如果是多个s3Client, 需要判断这个url是否从旧的s3Client下载
     */
    String DOWNLOAD_FORM_ORIGINAL_S3_CLIENT = "downloadFromOriginalS3Client";
    /**
     * 有的client可以支持多种类型的文件存储，比如s3的client既可以访问s3服务器也可以访问nos服务器
     * @return
     */
    default String[] types() {
        return new String[] {type()};
    }

    /**
     * 支持的文件存储类型，目前系统默认支持nos、s3、local等
     * @return
     */
    String type();
    /**
     * 初始化
     * @param config
     */
    void init(Map<String, String> config);
    /**
     * 文件上传
     * @param inputStream
     * @param targetPath
     * @param payloads
     * @return
     */
    String upload(InputStream inputStream, String targetPath, Map<String, String> payloads) throws IOException;

    /**
     * 文件下载
     * @param outputStream
     * @param targetPath
     * @return
     */
    void download(OutputStream outputStream, String targetPath, Map<String, String> payloads) throws IOException;

    /**
     * 删除文件或目录
     * @param targetPath
     * @param payloads
     * @throws IOException
     */
    void delete(String targetPath, Map<String, String> payloads) throws IOException;

    void filterTargetFile(Object fileObject);
}
