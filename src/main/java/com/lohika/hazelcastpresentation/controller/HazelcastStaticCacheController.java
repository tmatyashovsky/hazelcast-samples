package com.lohika.hazelcastpresentation.controller;

import java.util.concurrent.ConcurrentMap;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

/**
 * Controller that handles all requests related to statically defined Hazelcast cache.
 *
 * @author taras.matyashovsky
 */
@Controller
public class HazelcastStaticCacheController {

    @Resource(name = "writeThroughPresentationHazelcastDistributedCache")
    private ConcurrentMap<String, String> cache;

    @RequestMapping(value = "/static/{key}", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<String> get(@PathVariable String key) {
        String value = this.cache.get(key);

        if (value != null) {
            return new ResponseEntity<String>(value, HttpStatus.OK);
        }

        return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/static/{key}", method = RequestMethod.PUT)
    @ResponseBody
    ResponseEntity<String> put(@PathVariable String key, @RequestBody String value) {
        this.cache.put(key, value);

        return new ResponseEntity<String>(HttpStatus.CREATED);
    }

}
