package com.lohika.hazelcastpresentation.cache.manager.hazelcast;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import org.springframework.beans.factory.InitializingBean;

import com.lohika.hazelcastpresentation.cache.manager.DistributedCacheManager;
import com.lohika.hazelcastpresentation.cache.manager.DistributedCacheStorePreInitializer;

/**
 * Hazelcast specific implementation of distributed cache manager.
 *
 * @author taras.matyashovsky
 */
public class HazelcastDistributedCacheManager implements DistributedCacheManager, InitializingBean {

    private volatile ConcurrentMap<String, IMap> caches = new ConcurrentHashMap<String, IMap>();
    private HazelcastInstance hazelcastInstance;
    private DistributedCacheStorePreInitializer distributedCacheStorePreInitializer;

    /**
     * Instantiates cache manager with specific Hazelcast instance.
     *
     * @param hazelcastInstance Hazelcast instance.
     */
    public HazelcastDistributedCacheManager(final HazelcastInstance hazelcastInstance,
        final DistributedCacheStorePreInitializer distributedCacheStorePreInitializer) {
        this.hazelcastInstance = hazelcastInstance;
        this.distributedCacheStorePreInitializer = distributedCacheStorePreInitializer;
    }

    public void afterPropertiesSet() throws Exception {
        final Collection<DistributedObject> distributedObjects = this.hazelcastInstance.getDistributedObjects();
        for (DistributedObject distributedObject : distributedObjects) {
            if (distributedObject instanceof IMap) {
                final IMap map = (IMap) distributedObject;
                caches.put(map.getName(), map);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> IMap<K, V> getCache(String cacheName) {
        if (caches.get(cacheName) == null) {
            synchronized (this) {
                if (caches.get(cacheName) == null) {
                    distributedCacheStorePreInitializer.preInitialize(cacheName);
                    final IMap<K, V> map = this.hazelcastInstance.getMap(cacheName);
                    caches.putIfAbsent(cacheName, map);

                    return map;
                }
            }
        }

        return caches.get(cacheName);
    }
    
}
