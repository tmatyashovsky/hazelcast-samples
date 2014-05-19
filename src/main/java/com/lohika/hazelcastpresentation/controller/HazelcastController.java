package com.lohika.hazelcastpresentation.controller;

import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import com.lohika.hazelcastpresentation.cache.manager.DistributedCacheManager;

/**
 * @author taras.matyashovsky
 */
@Controller
@Lazy
@SuppressWarnings("unchecked")
public class HazelcastController implements BeanFactoryAware {

    private ConcurrentMap<String, String> cache;

    @Value("${hazelcastpresentation.cacheBeanToUse}")
    private String cacheBeanToUse;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        try {
            this.cache = (ConcurrentMap<String, String>) beanFactory.getBean(this.cacheBeanToUse);
        } catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
            /* Creating cache on the fly.
              Pay attention that 'default' map config key is going to be used in this case. */
            DistributedCacheManager cacheManager = (DistributedCacheManager) beanFactory.getBean(
                "presentationHazelcastDistributedCacheManager");

            this.cache = cacheManager.getCache(this.cacheBeanToUse);
        }
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<String> get(@PathVariable String key) {
        String value = this.cache.get(key);

        if (value != null) {
            return new ResponseEntity<String>(value, HttpStatus.OK);
        }

        return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.PUT)
    @ResponseBody
    ResponseEntity<String> put(@PathVariable String key, @RequestBody String value) {
        this.cache.put(key, value);

        return new ResponseEntity<String>(HttpStatus.CREATED);
    }

}
