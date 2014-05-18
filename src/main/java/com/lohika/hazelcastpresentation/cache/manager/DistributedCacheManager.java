package com.lohika.hazelcastpresentation.cache.manager;

import java.util.concurrent.ConcurrentMap;

/**
 * Distributed cache manager that hides implementation details, e.g. Infinispan, Hazelcast, etc.
 *
 * @author taras.matyashovsky
 */
public interface DistributedCacheManager {

    /**
     * Get cache with specific cache name.
     * If cache does not exist - creates new one based on config.
     *
     * @param cacheName cache name.
     *
     * @return distributed cache.
     */
   <K, V> ConcurrentMap<K, V> getCache(final String cacheName);

}
