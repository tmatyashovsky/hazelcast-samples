package com.lohika.hazelcastpresentation.controller;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lohika.hazelcastpresentation.task.HazelcastSimpleTask;
import com.lohika.hazelcastpresentation.task.HazelcastVerifyTask;

/**
 * @author taras.matyashovsky
 */
@Controller
@Lazy
@SuppressWarnings("unchecked")
public class HazelcastExecutorController implements BeanFactoryAware {

    private final Logger logger = LoggerFactory.getLogger(HazelcastExecutorController.class);

    private HazelcastInstance hazelcastInstance;
    private IExecutorService executorService;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.hazelcastInstance = (HazelcastInstance) beanFactory.getBean("presentationHazelcastInstance");
        this.executorService = (IExecutorService) beanFactory.getBean("presentationHazelcastExecutorService");
    }

    @RequestMapping(value = "/execute", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<String> execute() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            logger.info("Producing task {}", i);
            this.executorService.execute(new HazelcastSimpleTask(String.valueOf(i)));
        }

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<String> verify() throws InterruptedException {
        ConcurrentMap<String, String> cache = this.hazelcastInstance.getMap("presentationHazelcastDistributedCache");

        for (int i = 0; i < 10; i++)
            cache.put(UUID.randomUUID().toString(), "");
            for (String key : cache.keySet()) {
                this.executorService.executeOnKeyOwner(new HazelcastVerifyTask(key), key);
            }

        return new ResponseEntity<String>(HttpStatus.OK);
    }

}
