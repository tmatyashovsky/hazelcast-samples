package com.lohika.hazelcastpresentation.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import com.lohika.hazelcastpresentation.Application;
import com.lohika.hazelcastpresentation.cache.store.StoreRepository;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebAppConfiguration
@IntegrationTest("server.port:8888")
public class HazelcastControllerIntegrationTest {

    private final String HAZELCAST_CONTROLLER_ENDPOINT_PATH = "http://localhost:8888/{key}";
    private final String TABLE_NAME = "presentation";
    private final String CACHE_KEY = "key";
    private final String CACHE_VALUE = "value";

    private RestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private StoreRepository storeRepository;

    @Before
    public void setUp() {
        this.storeRepository.delete(TABLE_NAME, CACHE_KEY);
    }

    @Test
    public void shouldPutAndGetValueToAndFromWriteThroughCache() {
        // Checking that value does not exists.
        HttpEntity<String> requestEntity = new HttpEntity<String>(new HttpHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
            HAZELCAST_CONTROLLER_ENDPOINT_PATH.replace("{key}", CACHE_KEY),
            HttpMethod.GET, requestEntity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Put value into cache.
        requestEntity = new HttpEntity<String>(CACHE_VALUE);

        response = restTemplate.exchange(
            HAZELCAST_CONTROLLER_ENDPOINT_PATH.replace("{key}", CACHE_KEY),
            HttpMethod.PUT, requestEntity, String.class);

        // Assertions.
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        String cacheValue = this.storeRepository.load(TABLE_NAME, CACHE_KEY);
        assertEquals(CACHE_VALUE, cacheValue);

        // Checking that value exists.
        requestEntity = new HttpEntity<String>(new HttpHeaders());

        response = restTemplate.exchange(
            HAZELCAST_CONTROLLER_ENDPOINT_PATH.replace("{key}", CACHE_KEY),
            HttpMethod.GET, requestEntity, String.class);

        // Assertions.
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(CACHE_VALUE, response.getBody());
    }

}
