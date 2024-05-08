package com.defaulttenant.hotswap.util;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 *
 */
public class DependenceHashMapWrapper<K,V> extends HashMap<K,V> {
    protected transient Map target;
    protected boolean targetDeprecatedType = true;

    public DependenceHashMapWrapper(Map map) {
        this.target = map;
    }

    public DependenceHashMapWrapper(Map map, boolean targetDeprecatedType) {
        this.target = map;
        this.targetDeprecatedType = targetDeprecatedType;
    }

    @Override
    public int size() {
        return target.size();
    }

    @Override
    public boolean isEmpty() {
        return target.isEmpty();
    }

    @Override
    public V get(Object key) {
        return (V) valueTypeConvert(target.get(valueTypeConvert(key, true)), false);
    }

    @Override
    public boolean containsKey(Object key) {
        return target.containsKey(valueTypeConvert(key, true));
    }

    @Override
    public V put(K key, V value) {
        return (V) valueTypeConvert(target.put(valueTypeConvert(key, true), valueTypeConvert(value, true)), false);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        target.putAll(mapCopy(m, true));
    }

    @Override
    public V remove(Object key) {
        return (V) valueTypeConvert(target.remove(valueTypeConvert(key, true)), false);
    }

    @Override
    public void clear() {
        target.clear();
    }

    @Override
    public boolean containsValue(Object value) {
        return target.containsValue(valueTypeConvert(value, true));
    }

    @Override
    public Set<K> keySet() {
        return mapCopy(target, false).keySet();
    }

    @Override
    public Collection<V> values() {
        return mapCopy(target, false).values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return mapCopy(target, false).entrySet();
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return (V) target.getOrDefault(valueTypeConvert(key, true), valueTypeConvert(defaultValue, true));
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return (V) target.putIfAbsent(valueTypeConvert(key, true), valueTypeConvert(value, true));
    }

    @Override
    public boolean remove(Object key, Object value) {
        return target.remove(valueTypeConvert(key, true), valueTypeConvert(value, true));
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return target.replace(valueTypeConvert(key, true),
                valueTypeConvert(oldValue, true), valueTypeConvert(newValue, true));
    }

    @Override
    public V replace(K key, V value) {
        return (V) valueTypeConvert(target.replace(valueTypeConvert(key, true), valueTypeConvert(value, true)), false);
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return (V) target.computeIfAbsent(valueTypeConvert(key, true),
                k->valueTypeConvert(mappingFunction.apply((K) valueTypeConvert(k, false)), true));
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return (V) target.computeIfPresent(valueTypeConvert(key, true), (k, v)->valueTypeConvert(remappingFunction.apply(
                        (K) valueTypeConvert(k, false),
                        (V) valueTypeConvert(v, false)),
                        true));
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return (V) target.compute(valueTypeConvert(key, true), (k, v)->valueTypeConvert(remappingFunction.apply(
                        (K) valueTypeConvert(k, false),
                        (V)valueTypeConvert(v, false)),
                true));
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return (V) target.merge(valueTypeConvert(key, true), valueTypeConvert(value, true),
                (oldValue, newValue)->valueTypeConvert(remappingFunction.apply(
                        (V) valueTypeConvert(oldValue, false),
                        (V)valueTypeConvert(newValue, false)),
                true));
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        target.forEach((k, v)->action.accept((K) valueTypeConvert(k, false), (V) valueTypeConvert(v, false)));
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        target.replaceAll((k, v)->function.apply((K) valueTypeConvert(k, false), (V) valueTypeConvert(v, false)));
    }

    public boolean isTargetDeprecatedType() {
        return this.targetDeprecatedType;
    }

    private Map mapCopy(Map<?, ?> source, boolean toTargetType) {
        Map result = new HashMap<>(source.size());
        if (null != source) {
            for (Entry entry : source.entrySet()) {
                Object key = entry.getKey();
                Object value = entry.getValue();

                result.put(valueTypeConvert(key, toTargetType), valueTypeConvert(value, toTargetType));
            }
        }

        return result;
    }

    private Object valueTypeConvert(Object obj, boolean toTargetType) {
        if ((toTargetType && targetDeprecatedType) || (!toTargetType && !targetDeprecatedType)) {
            return LogicCallUtils.toDependenceType(obj);
        } else {
            return LogicCallUtils.fromDependenceType(obj);
        }
    }
}
