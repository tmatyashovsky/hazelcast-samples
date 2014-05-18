create table if not exists presentation(
    cache_key varchar(255),
    cache_value varchar(255),
    CONSTRAINT cache_key_should_be_unique UNIQUE(cache_key));