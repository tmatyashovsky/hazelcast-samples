package com.lohika.hazelcastpresentation.cache;

import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

/**
 * @author taras.matyashovsky
 */
@Controller
@Lazy
@SuppressWarnings("unchecked")
public class HazelcastController implements BeanFactoryAware {

    private ConcurrentMap<String, String> cache;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
//        this.cache = (ConcurrentMap<String, String>) beanFactory.getBean("presentationHazelcastDistributedCache");
        this.cache = (ConcurrentMap<String, String>) beanFactory.getBean("writeThroughPresentationHazelcastDistributedCache");
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
