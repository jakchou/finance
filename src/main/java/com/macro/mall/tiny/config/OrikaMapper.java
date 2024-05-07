package com.macro.mall.tiny.config;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 对象copy工具类
 * @author: Mr.Eric
 * @create: 2021-06-17 10:38
 **/
public class OrikaMapper {

    private static MapperFacade mapper;

    static {
        MapperFactory factory = new DefaultMapperFactory.Builder().build();
        mapper = factory.getMapperFacade();
    }

    private OrikaMapper() {
    }

    /**
     * 默认字段实例集合
     */
    private static Map<String, MapperFacade> CACHE_MAPPER_FACADE_MAP = new ConcurrentHashMap<>();

    /**
     * 简单的复制出新类型对象.
     * <p>
     * 内部实现是通过source.getClass() 获得源Class
     */
    public static <S, D> D map(S source, Class<D> destinationClass) {
        return mapper.map(source, destinationClass);
    }

    /**
     * 极致性能的复制出新类型对象.
     * <p>
     */
    public static <S, D> D map(S source, Type<S> sourceType, Type<D> destinationType) {
        return mapper.map(source, sourceType, destinationType);
    }


    /**
     * 简单的复制出新对象列表到ArrayList
     * <p>
     * 不建议使用{@link MapperFacade#mapAsList(Object[], Class)}} 接口, sourceClass需要在遍历每一个元素的时候反射，实在有点慢
     */
    public static <S, D> List<D> mapList(Iterable<S> sourceList, Class<S> sourceClass, Class<D> destinationClass) {
        return mapper.mapAsList(sourceList, TypeFactory.valueOf(sourceClass), TypeFactory.valueOf(destinationClass));
    }

    /**
     * 极致性能的复制出新类型对象到ArrayList.
     * <p>
     */
    public static <S, D> List<D> mapList(Iterable<S> sourceList, Type<S> sourceType, Type<D> destinationType) {
        return mapper.mapAsList(sourceList, sourceType, destinationType);
    }

    /**
     * 简单复制出新对象列表到数组.
     * <p>
     * 内部实现是通过source.getComponentType() 获得源Class
     *
     * @param destination      要复制到的目标数组
     * @param source           待复制的源数据数组
     * @param destinationClass 要复制到的目标数组数据元素Class
     */
    public static <S, D> D[] mapArray(final D[] destination, final S[] source, final Class<D> destinationClass) {
        return mapper.mapAsArray(destination, source, destinationClass);
    }

    /**
     * 极致性能的复制出新类型对象到数组.
     * <p>
     *
     * @param destination     要复制到的目标数组
     * @param source          待复制的源数据数组
     * @param sourceType      待复制的源数据数组实例类型
     * @param destinationType 要复制到的目标数组类型
     */
    public static <S, D> D[] mapArray(D[] destination, S[] source, Type<S> sourceType, Type<D> destinationType) {
        return mapper.mapAsArray(destination, source, sourceType, destinationType);
    }

    /**
     * 预先获取orika转换所需要的{@link Type}，避免每次复制都做转换.
     */
    public static <E> Type<E> getType(final Class<E> rawType) {
        return TypeFactory.valueOf(rawType);
    }


    /**
     * 获取自定义映射
     *
     * @param toClass   映射类
     * @param dataClass 数据映射类
     * @param configMap 自定义配置
     * @return 映射类对象
     */
    private static <E, T> MapperFacade getMapperFacade(Class<E> toClass, Class<T> dataClass,
                                                       Map<String, String> configMap) {
        String mapKey = dataClass.getCanonicalName() + "_" + toClass.getCanonicalName();
        MapperFacade mapperFacade = CACHE_MAPPER_FACADE_MAP.get(mapKey);
        if (Objects.isNull(mapperFacade)) {
            MapperFactory factory = new DefaultMapperFactory.Builder().build();
            ClassMapBuilder classMapBuilder = factory.classMap(dataClass, toClass);
            configMap.forEach(classMapBuilder::field);
            classMapBuilder.byDefault().register();
            mapperFacade = factory.getMapperFacade();
            CACHE_MAPPER_FACADE_MAP.put(mapKey, mapperFacade);
        }
        return mapperFacade;
    }

    /**
     * 映射实体（自定义配置）
     *
     * @param toClass   映射类对象
     * @param data      数据（对象）
     * @param configMap 自定义配置
     * @return 映射类对象
     */
    public static <E, T> E map(Class<E> toClass, T data, Map<String, String> configMap) {
        MapperFacade mapperFacade = getMapperFacade(toClass, data.getClass(), configMap);
        return mapperFacade.map(data, toClass);
    }

    /**
     * 映射集合（自定义配置）
     *
     * @param toClass   映射类
     * @param data      数据（集合）
     * @param configMap 自定义配置
     * @return 映射类对象
     */
    public static <E, T> List<E> mapAsList(Class<E> toClass, Collection<T> data, Map<String, String> configMap) {
        T t = data.stream().findFirst().orElseThrow(() -> new RuntimeException("映射集合，数据集合为空"));
        MapperFacade mapperFacade = getMapperFacade(toClass, t.getClass(), configMap);
        return mapperFacade.mapAsList(data, toClass);
    }


}
