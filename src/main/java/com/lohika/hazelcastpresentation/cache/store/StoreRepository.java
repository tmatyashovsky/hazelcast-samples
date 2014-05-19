package com.lohika.hazelcastpresentation.cache.store;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author taras.matyashovsky
 */
@Repository
public class StoreRepository {

    private final String STORE_SQL = "INSERT INTO :tableName (cache_key, cache_value) " +
        "VALUES (:key, :value);";
    private final String DELETE_SQL = "DELETE FROM :tableName " +
        "WHERE cache_key = :key;";
    private final String LOAD_SQL = "SELECT cache_value FROM :tableName " +
        "WHERE cache_key = :key;";
    private final String LOAD_ALL_SQL = "SELECT cache_key, cache_value FROM :tableName " +
        "WHERE cache_key IN (:keys);";
    private final String LOAD_ALL_KEYS_SQL = "SELECT cache_key FROM :tableName";

    @Autowired
    private NamedParameterJdbcTemplate template;

    public void store(final String tableName, final String key, final String value) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("key", key);
        parameters.put("value", value);

        this.template.update(STORE_SQL.replace(":tableName", tableName), parameters);
    }

    public void delete(final String tableName, final String key) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("key", key);

        this.template.update(DELETE_SQL.replace(":tableName", tableName), parameters);
    }

    public String load(final String tableName, final String key) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("key", key);

        List<String> values = this.template.queryForList(LOAD_SQL.replace(":tableName", tableName),
            parameters, String.class);

        return values.size() > 0 ? values.get(0) : null;
    }

    public Map<String, String> loadAll(final String tableName, final Collection<String> keys) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("keys", keys);

        List<Map.Entry<String, String>> results = this.template.query(LOAD_ALL_SQL.replace(":tableName", tableName),
            parameters,
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

    public Set<String> loadAllKeys(final String tableName) {
        return new HashSet<String>(this.template.queryForList(LOAD_ALL_KEYS_SQL.replace(":tableName", tableName),
            new HashMap<String, Object>(), String.class));
    }

}
