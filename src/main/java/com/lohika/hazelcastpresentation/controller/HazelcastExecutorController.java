package com.lohika.hazelcastpresentation.controller;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiExecutionCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lohika.hazelcastpresentation.cache.processor.UpdateEntryProcessor;
import com.lohika.hazelcastpresentation.task.HazelcastAverageTask;
import com.lohika.hazelcastpresentation.task.HazelcastUpdateTask;
import com.lohika.hazelcastpresentation.task.HazelcastSimpleTask;
import com.lohika.hazelcastpresentation.task.HazelcastVerifyTask;

/**
 * @author taras.matyashovsky
 */
@Controller
public class HazelcastExecutorController {

    private final Logger logger = LoggerFactory.getLogger(HazelcastExecutorController.class);
    private Random random = new Random();

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Autowired
    private IExecutorService executorService;

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
        ConcurrentMap<String, String> cache = this.hazelcastInstance.getMap("inMemoryPresentationCache");
        cache.clear();
        cache = this.hazelcastInstance.getMap("inMemoryPresentationCache");

        for (int i = 0; i < 10; i++)
            cache.put(UUID.randomUUID().toString(), "");
            for (String key : cache.keySet()) {
                this.executorService.executeOnKeyOwner(new HazelcastVerifyTask(key), key);
            }

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @RequestMapping(value = "/average/{count}", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<String> average(@PathVariable int count) throws InterruptedException, ExecutionException {
        IMap<String, Double> cache = this.hazelcastInstance.getMap("averagePresentationHazelcastDistributedCache");
        cache.destroy();
        cache = this.hazelcastInstance.getMap("averagePresentationHazelcastDistributedCache");

        for (int i = 0; i < count; i++) {
            cache.put(UUID.randomUUID().toString(), random.nextDouble() * random.nextInt(10));
        }

        final long startTime = System.currentTimeMillis();

        MultiExecutionCallback callback =
            new MultiExecutionCallback() {

                @Override
                public void onResponse(com.hazelcast.core.Member member, Object value) {
                    logger.info("Member {} has responded with the result {}", member, value);
                }

                @Override
                public void onComplete(Map<com.hazelcast.core.Member, Object> values) {
                    logger.info("All members have responded with the results, calculating ...");

                    double sum = 0;

                    for (Map.Entry<com.hazelcast.core.Member, Object> entry : values.entrySet()) {
                        logger.info("Member {} provided intermediate result {}", entry.getKey(), entry.getValue());

                        sum += (Double) entry.getValue();
                    }

                    logger.info("Final result is {}", sum / values.size());

                    long stopTime = System.currentTimeMillis();
                    long elapsedTime = stopTime - startTime;

                    logger.info("Elapsed time with {} member(s) is {} ms", values.size(), elapsedTime);
                }
            };

        this.executorService.submitToAllMembers(new HazelcastAverageTask(), callback);

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @RequestMapping(value = "/update/{count}", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<String> updateViaExecutorService(@PathVariable int count) throws InterruptedException, ExecutionException {
        IMap<String, Double> cache = this.hazelcastInstance.getMap("updatePresentationHazelcastDistributedCache");
        cache.destroy();
        cache = this.hazelcastInstance.getMap("updatePresentationHazelcastDistributedCache");

        for (int i = 0; i < count; i++) {
            cache.put(UUID.randomUUID().toString(), random.nextDouble() * random.nextInt(100));
        }

        for (String key: cache.keySet()) {
            this.executorService.executeOnKeyOwner(new HazelcastUpdateTask(key), key);
        }

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @RequestMapping(value = "/process/{count}", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<String> updateViaEntryProcessor(@PathVariable int count) throws InterruptedException, ExecutionException {
        IMap<String, Double> cache = this.hazelcastInstance.getMap("updatePresentationHazelcastDistributedCache");
        cache.destroy();
        cache = this.hazelcastInstance.getMap("updatePresentationHazelcastDistributedCache");

        for (int i = 0; i < count; i++) {
            cache.put(UUID.randomUUID().toString(), random.nextDouble() * random.nextInt(100));
        }

        for (String key: cache.keySet()) {
            cache.executeOnKey(key, new UpdateEntryProcessor());
        }

        return new ResponseEntity<String>(HttpStatus.OK);
    }

}
