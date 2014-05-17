package com.lohika.hazelcastpresentation.executor;

import java.io.Serializable;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Verify task to be executed on Hazelcast nodes.
 *
 * @author taras.matyashosky
 */
public class HazelcastVerifyTask implements Runnable, Serializable, HazelcastInstanceAware {

    private final Logger logger = LoggerFactory.getLogger(HazelcastVerifyTask.class);

    private final String key;
    private transient HazelcastInstance hazelcastInstance;

    public HazelcastVerifyTask(final String key) {
        this.key = key;
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public void run() {
        IMap map = this.hazelcastInstance.getMap("presentationHazelcastDistributedCache");
        boolean localKey = map.localKeySet().contains(key);

        logger.info("Key is local: {}", localKey);
    }

}