package com.defaulttenant.hotswap.filestorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.defaulttenant.hotswap.config.Constants;
import com.defaulttenant.hotswap.config.ExtensionProperties;
import com.defaulttenant.hotswap.config.LcpProperties;
import com.defaulttenant.hotswap.filestorage.spi.ExtensionFileStorageClient;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * spi拓展点管理器
 *
 * @author sys
 * @date 2022-05-30 14:56
 */
@Component
public class FileStorageClientManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileStorageClientManager.class);
    private static final String FILESYSTEM_SPI_CLASS = "com.netease.lowcode.extension.LcapFileSystemSpi";

    @Resource
    private LcpProperties lcpProperties;
    @Resource
    private ExtensionProperties extensionProperties;

    @Resource
    private List<FileStorageClient> fileSystemClients;

    Map<String, FileStorageClient> fileSystemSpiCache = new HashMap<>();

    @PostConstruct
    public void init() {
        loadFileSystemSpi();
    }

    public synchronized void loadFileSystemSpi() {
        // 加载默认支持的文件系统spi
        loadBuiltInFileSystemSpi();

        // 加载拓展的文件系统客户端实现
        loadExtendsionFileSystemSpi();
    }

    /**
     * 系统默认支持的文件系统spi
     */
    private void loadBuiltInFileSystemSpi() {
        Map<String, String> builtInConfigs = new HashMap<>();
        builtInConfigs.put(Constants.UPLOAD_CONFIG_KEY_FILE_STORAGE_TYPE, lcpProperties.getUpload().getSinkType());
        builtInConfigs.put(Constants.UPLOAD_CONFIG_LOCAL_BASE_PATH, lcpProperties.getUpload().getSinkPath());
        builtInConfigs.put(Constants.UPLOAD_CONFIG_KEY_S3_ADDRESS, lcpProperties.getUpload().getS3Address());
        builtInConfigs.put(Constants.UPLOAD_CONFIG_KEY_S3_ACCESS_KEY, lcpProperties.getUpload().getS3AccessKey());
        builtInConfigs.put(Constants.UPLOAD_CONFIG_KEY_S3_SECRET_KEY, lcpProperties.getUpload().getS3SecretKey());
        builtInConfigs.put(Constants.UPLOAD_CONFIG_KEY_S3_BUCKET, lcpProperties.getUpload().getS3Bucket());
        builtInConfigs.put(Constants.UPLOAD_CONFIG_KEY_CDN_ADDRESS,lcpProperties.getUpload().getCdnAddress());

        for (FileStorageClient fileSystemSpi : fileSystemClients) {
            fileSystemSpi.init(builtInConfigs);
            for (String type : fileSystemSpi.types()) {
                fileSystemSpiCache.put(type, fileSystemSpi);
            }
        }
    }

    /**
     * 拓展的文件系统spi
     */
    private void loadExtendsionFileSystemSpi() {
        try {
            Class fsClass = Class.forName(FILESYSTEM_SPI_CLASS);
            ServiceLoader.load(fsClass).forEach(service->{
                ExtensionFileStorageClient extensionFileSystem = new ExtensionFileStorageClient(service);
                extensionFileSystem.init(extensionProperties);
                fileSystemSpiCache.put(extensionFileSystem.type(), extensionFileSystem);
                LOGGER.info("自定义文件系统spi " + extensionFileSystem.type() + "加载成功");
            });
        } catch (ClassNotFoundException e) {
            LOGGER.info("没有自定义拓展的文件系统spi实现");
        }
    }

    public FileStorageClient getFileSystemSpi(String type) {
        return fileSystemSpiCache.get(type);
    }

}
