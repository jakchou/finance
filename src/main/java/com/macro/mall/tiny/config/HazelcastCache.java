/*
package com.macro.mall.tiny.config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.apache.ibatis.cache.Cache;


*/
/**
 * @author zhouzz
 *//*

public class HazelcastCache implements Cache {

    private final String id;
    private final IMap<Object, Object> cacheMap;
    @Override
    public String getId() {
        return id;
    }
    public HazelcastCache(String id, HazelcastInstance hazelcastInstance) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.id = id;
        this.cacheMap = hazelcastInstance.getMap(id);
    }

    @Override
    public void putObject(Object key, Object value) {
        cacheMap.put(key, value);
    }

    @Override
    public Object getObject(Object key) {
        return cacheMap.get(key);
    }

    @Override
    public Object removeObject(Object key) {
        return cacheMap.remove(key);
    }

    @Override
    public void clear() {
        cacheMap.clear();
    }

    @Override
    public int getSize() {
        return cacheMap.size();
    }
}
*/
