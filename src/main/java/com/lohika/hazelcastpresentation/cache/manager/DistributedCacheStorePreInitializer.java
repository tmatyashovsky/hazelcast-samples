package com.lohika.hazelcastpresentation.cache.manager;

/**
 * Defines dynamic actions to be taken before distributed cache initialization, e.g.
 * update global configuration, create schema/table, etc.
 *
 * @author taras.matyashovsky
 */
public interface DistributedCacheStorePreInitializer {

    /**
     * Can perform any steps required upon cache initialization, e.g.
     * update default cache store configuration, create schema/table, etc. depending on cache implementation.
     *
     * In our simple case creates new table in public schema.
     *
     * @param tableName table name.
     */
    void preInitialize(final String tableName);

}
