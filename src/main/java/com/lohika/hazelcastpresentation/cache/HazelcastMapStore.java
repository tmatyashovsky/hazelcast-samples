package com.lohika.hazelcastpresentation.cache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MapLoaderLifecycleSupport;
import com.hazelcast.core.MapStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * A write-through Hazelcast cache store using H2 as a storage backend.
 *
 * @author taras.matyashovsky
 */
public class HazelcastMapStore implements MapStore<String, String>, MapLoaderLifecycleSupport {

    private final Logger logger = LoggerFactory.getLogger(HazelcastMapStore.class);

    private NamedParameterJdbcTemplate template;

    private final String STORE_SQL = "INSERT INTO presentation (cache_key, cache_value) " +
        "VALUES (:key, :value);";
    private final String DELETE_SQL = "DELETE FROM presentation " +
        "WHERE cache_key = :key;";
    private final String LOAD_SQL = "SELECT cache_value FROM presentation " +
        "WHERE cache_key = :key;";
    private final String LOAD_ALL_SQL = "SELECT cache_key, cache_value FROM presentation " +
        "WHERE cache_key IN (:keys);";
    private final String LOAD_ALL_KEYS_SQL = "SELECT cache_key FROM presentation";

    @Override
    public void store(String key, String value) {
        logger.info("Storing {}/{} to cache", key, value);

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("key", key);
        parameters.put("value", value);

        this.template.update(STORE_SQL, parameters);
    }

    @Override
    public void storeAll(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            this.store(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void delete(String key) {
        logger.info("Deleting {} from cache", key);

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("key", key);

        this.template.update(DELETE_SQL, parameters);
    }

    @Override
    public void deleteAll(Collection<String> keys) {
        for (String key : keys) {
            this.delete(key);
        }
    }

    @Override
    public String load(String key) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("key", key);

        List<String> values = this.template.queryForList(LOAD_SQL, parameters, String.class);

        return values.size() > 0 ? values.get(0) : null;
    }

    @Override
    public Map<String, String> loadAll(Collection<String> keys) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("keys", keys);

        List<Map.Entry<String, String>> results = this.template.query(LOAD_ALL_SQL, parameters,
            new RowMapper<Map.Entry<String, String>>() {

            public Map.Entry<String, String> mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                return new AbstractMap.SimpleEntry<String, String>(resultSet.getString("cache_key"),
                    resultSet.getString("cache_value"));
            }

        });

        Map<String, String> objects = new HashMap<String, String>();

        for (Map.Entry<String, String> result : results) {
            objects.put(result.getKey(), result.getValue());
        }

        return objects;
    }

    @Override
    public Set<String> loadAllKeys() {
        logger.info("Loading all keys from cache");

        return new HashSet<String>(this.template.queryForList(LOAD_ALL_KEYS_SQL, new HashMap<String, Object>(),
            String.class));
    }

    @Override
    public void init(HazelcastInstance hazelcastInstance, Properties properties, String mapName) {
        logger.info("Initializing {} cache", mapName);

        this.template = (NamedParameterJdbcTemplate) properties.get("template");
    }

    @Override
    public void destroy() {
        // Nothing to do.
    }

}
