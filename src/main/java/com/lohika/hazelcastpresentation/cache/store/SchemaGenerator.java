package com.lohika.hazelcastpresentation.cache.store;

import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.util.FileCopyUtils;

/**
 * @author taras.matyashovsky
 */
public class SchemaGenerator {

    private final String createTableSql;

    public SchemaGenerator() {
        try {
            createTableSql = loadClassPathResource("create_table_if_not_exists.sql");
        } catch (IOException ioException) {
            throw new RuntimeException("Failed to load sql templates", ioException);
        }
    }

    /**
     * Creates table using predefined SQL template loaded from file.
     *
     * @param tableName table name.
     * @param template JDBC template.
     */
    public void createTable(final String tableName, final NamedParameterJdbcOperations template) {
        template.getJdbcOperations().update(createTableSql.replace(":tableName", tableName));
    }

    private static String loadClassPathResource(final String fileName) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(fileName, SchemaGenerator.class);
        return FileCopyUtils.copyToString(new InputStreamReader(classPathResource.getInputStream()));
    }

}
