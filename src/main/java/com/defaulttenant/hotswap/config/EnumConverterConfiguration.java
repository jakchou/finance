package com.defaulttenant.hotswap.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.defaulttenant.hotswap.domain.enumeration.BaseEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;


@Configuration
public class EnumConverterConfiguration implements WebMvcConfigurer {


    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void myObjectMapper() {
        // 解决enum不匹配问题
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new StringCodeToEnumConverterFactory());
    }

    public class StringToEnumConverter <T extends BaseEnum> implements Converter<String, T> {

        private Map<String, T> enumMap = new HashMap<>();

        public StringToEnumConverter(Class<T> enumType) {
            T[] enums = enumType.getEnumConstants();
            for (T e : enums) {
                enumMap.put(e.getCode().toString(), e);
            }
        }

        @Override
        public T convert(String source) {
            T t = enumMap.get(source);
            if (ObjectUtils.isEmpty(t)) {
                return null;
            }
            return t;
        }
    }

    public class StringCodeToEnumConverterFactory implements ConverterFactory<String, BaseEnum> {

        private Map<Class, Converter> CONVERTERS = new HashMap<>();

        @Override
        public <T extends BaseEnum> Converter<String, T> getConverter(Class<T> targetType) {
            return CONVERTERS.computeIfAbsent(targetType, k -> new StringToEnumConverter<>(k));
        }
    }

}
