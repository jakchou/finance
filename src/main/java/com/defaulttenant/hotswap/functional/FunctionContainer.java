package com.defaulttenant.hotswap.functional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * 函数容器，实现了函数接口的逻辑并注入到spring容器中均会放入此容器。
 * 通过逻辑名即可找到对应函数
 */
@Component
public class FunctionContainer {
    /**
     * 注册为函数bean的统一后缀
     */
    private static final String FUNCTION_BEAN_SUFFIX = "CallbackFunction";
    /**
     * 获取逻辑名称映射，key为对应的匹配条件，value的返回值为处理后得到的逻辑名称
     */
    private static final Map<Predicate<String>, Function<String, String>> LOGIC_NAME_MAPPER;

    /**
     * 函数容器，key为对应service名称，value为对应函数
     */
    private static Map<String, CallbackFunction> functions = new LinkedHashMap<>();

    static {
        Map<Predicate<String>, Function<String, String>> mapper = new LinkedHashMap<>();
        mapper.put(fullPathLogicName -> {
                    Pattern appLogic = Pattern.compile("^app.logics.\\S+$");
                    return appLogic.matcher(fullPathLogicName).find();
                },
                fullPathLogicName -> StringUtils.replaceOnce(fullPathLogicName, "app.logics.", StringUtils.EMPTY) + FUNCTION_BEAN_SUFFIX
        );

        mapper.put(fullPathLogicName -> {
                    Pattern libraryLogic = Pattern.compile("^extensions.\\S+.logics.\\S+$");
                    return libraryLogic.matcher(fullPathLogicName).find();
                },
                fullPathLogicName -> StringUtils.replaceEach(fullPathLogicName, new String[]{"extensions.", ".logics.", "-"},
                        new String[]{StringUtils.EMPTY, StringUtils.EMPTY, "_"}) + FUNCTION_BEAN_SUFFIX
        );
        LOGIC_NAME_MAPPER = Collections.unmodifiableMap(mapper);
    }

    /**
     * 通过逻辑名称获取对应函数，如果函数不存在的话则会抛出异常
     *
     * @param fullPathLogicName 逻辑名称
     * @return 对应函数实现
     */
    public static <T> T getBean(String fullPathLogicName, Class<T> clazz) {
        CallbackFunction callbackFunction =  functions.entrySet().stream()
                .filter(funEntry -> StringUtils.equalsIgnoreCase(funEntry.getKey(), LOGIC_NAME_MAPPER.entrySet().stream()
                        .filter(nameEntry -> nameEntry.getKey().test(fullPathLogicName))
                        .findFirst()
                        .map(entry -> entry.getValue().apply(fullPathLogicName))
                        .orElse(null)))
                .findFirst().map(Map.Entry::getValue)
                .orElseThrow(() -> new IllegalArgumentException("未找到对应回调函数"));
        return clazz.cast(callbackFunction);
    }

    @Autowired(required = false)
    public void setFunctions(Map<String, CallbackFunction> functions) {
        this.functions.putAll(functions);
    }
}
