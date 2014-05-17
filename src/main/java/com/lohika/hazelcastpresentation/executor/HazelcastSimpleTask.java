package com.lohika.hazelcastpresentation.executor;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple task to be executed on Hazelcast nodes.
 *
 * @author taras.matyashosky
 */
public class HazelcastSimpleTask implements Runnable, Serializable {

    private final Logger logger = LoggerFactory.getLogger(HazelcastSimpleTask.class);

    private final String message;

    public HazelcastSimpleTask(String message) {
        this.message = message;

    }
    @Override
    public void run() {
        try {
            Thread.sleep(5000);
        }
        catch (InterruptedException e) {
            logger.error("Interrupting exception {}", e.getMessage());
        }

        logger.info("Executing task {}", message);
    }

}