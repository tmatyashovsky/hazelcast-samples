package com.lohika.hazelcastpresentation.cache.manager.hazelcast;

import com.lohika.hazelcastpresentation.cache.store.SchemaGenerator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import com.lohika.hazelcastpresentation.cache.manager.DistributedCacheStorePreInitializer;

/**
 * Hazelcast specific actions to be made before distributed cache initialization.
 *
 * @author taras.matyashovsky
 */
public class HazelcastDistributedCacheStorePreInitializer implements DistributedCacheStorePreInitializer {

    private NamedParameterJdbcOperations template;
    private SchemaGenerator schemaGenerator = new SchemaGenerator();

    public void setTemplate(final NamedParameterJdbcOperations template) {
        this.template = template;
    }

    /**
     * Creates the schema and the table names in order to store the
     * objects which will be cached.
     *
     * TL; DR;
     *
     * For Hazelcast it does not make sense to update common map store configuration on the fly and
     * create schema/table in cache store as for Infinispan. Reason for that is the only possibility to pass
     * data to cache store is via Hazelcast instance, but each component might have its own Hazelcast instance, so
     * schema name, table name, etc. will be lost between those.
     *
     * So only way to create schema/table is when the cached in touched for the 1st time.
     */
    @Override
    public void preInitialize(final String tableName) {
        // Nothing complex here, but we could generate schema/table for particular class on the fly.
        // For now just creating table with cache name using hardcoded scripts inside public schema.
        this.schemaGenerator.createTable(tableName, this.template);
    }

}
