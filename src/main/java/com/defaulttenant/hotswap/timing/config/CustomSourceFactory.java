package com.defaulttenant.hotswap.timing.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;

public class CustomSourceFactory extends DefaultPropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        if (resource == null) {
            return super.createPropertySource(name, resource);
        }
        Resource fileResource = resource.getResource();
        ResourcePropertySource defaultResourceProperty =  new ResourcePropertySource(name, resource);
        if (fileResource.exists() && StringUtils.isNotBlank(fileResource.getFilename())){
            List<PropertySource<?>> sources = null;
            String originFileName = fileResource.getFilename();
            // 这里优先使用Yaml配置加载类来读取yml文件信息，如果存在相同的属性，后面的配置文件内容会覆盖前面配置文件内容
            if (originFileName.endsWith(".yaml") || originFileName.endsWith(".yml")) {
                sources = new YamlPropertySourceLoader().load(resource.getResource().getFilename(), resource.getResource());
            }
            if (originFileName.endsWith(".properties")){
                sources = new PropertiesPropertySourceLoader().load(originFileName,resource.getResource());
            }
            if (!CollectionUtils.isEmpty(sources)) {
                return sources.get(0);
            }
        }
        return defaultResourceProperty;
    }
}
