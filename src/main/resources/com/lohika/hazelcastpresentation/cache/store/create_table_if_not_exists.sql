create table if not exists :tableName (
    cache_key varchar(255),
    cache_value varchar(255),
    CONSTRAINT :tableName_cache_key_should_be_unique UNIQUE(cache_key));