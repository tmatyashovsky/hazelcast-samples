package com.lohika.hazelcastpresentation.cache.store;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MapLoaderLifecycleSupport;
import com.hazelcast.core.MapStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A write-through Hazelcast cache store using Postgres as a storage backend.
 *
 * @author taras.matyashovsky
 */
public class HazelcastMapStore implements MapStore<String, String>, MapLoaderLifecycleSupport {

    private final Logger logger = LoggerFactory.getLogger(HazelcastMapStore.class);

    private StoreRepository storeRepository;
    private String tableName;

    @Override
    public void init(HazelcastInstance hazelcastInstance, Properties properties, String mapName) {
        logger.info("Initializing {} cache", mapName);

        this.storeRepository = (StoreRepository) properties.get("repository");
        this.tableName = mapName;
    }

    @Override
    public void destroy() {
        // Nothing to do.
    }

    @Override
    public String load(final String key) {
        return this.storeRepository.load(this.tableName, key);
    }

    @Override
    public Map<String, String> loadAll(final Collection<String> keys) {
        return this.storeRepository.loadAll(this.tableName, keys);
    }

    @Override
    public Set<String> loadAllKeys() {
        logger.info("Loading all keys from cache");

        return this.storeRepository.loadAllKeys(this.tableName);
    }

    @Override
    public void store(final String key, final String value) {
        logger.info("Storing {}/{} to cache", key, value);

        this.storeRepository.store(this.tableName, key, value);
    }

    @Override
    public void storeAll(final Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            this.store(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void delete(final String key) {
        logger.info("Deleting {} from cache", key);

        this.storeRepository.delete(this.tableName, key);
    }

    @Override
    public void deleteAll(final Collection<String> keys) {
        for (String key : keys) {
            this.delete(key);
        }
    }

}
