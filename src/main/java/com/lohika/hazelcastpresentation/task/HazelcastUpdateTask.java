package com.lohika.hazelcastpresentation.task;

import java.io.Serializable;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Update task to be executed on Hazelcast nodes.
 *
 * @author taras.matyashosky
 */
public class HazelcastUpdateTask implements Runnable, Serializable, HazelcastInstanceAware {

    private final Logger logger = LoggerFactory.getLogger(HazelcastUpdateTask.class);

    private final String key;
    private transient HazelcastInstance hazelcastInstance;

    public HazelcastUpdateTask(final String key) {
        this.key = key;
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public void run() {
        logger.info("Executing task for key {}", key);

        IMap<String, Double> map = this.hazelcastInstance.getMap("updatePresentationHazelcastDistributedCache");
        Double value = map.get(key);
        map.put(key, value * 0.95);
    }

}
