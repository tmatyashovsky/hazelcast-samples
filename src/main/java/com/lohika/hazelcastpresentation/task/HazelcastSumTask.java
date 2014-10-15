package com.lohika.hazelcastpresentation.task;

import java.io.Serializable;
import java.util.concurrent.Callable;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task that calculates sum of cache values located on local node in not optimal way.
 *
 * @author taras.matyashovsky
 */
public class HazelcastSumTask implements Callable<Double>, Serializable, HazelcastInstanceAware {

    private final Logger logger = LoggerFactory.getLogger(HazelcastSumTask.class);
    private transient HazelcastInstance hazelcastInstance;

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public Double call() throws Exception {
        IMap<String, Double> map = this.hazelcastInstance.getMap("sumPresentationHazelcastDistributedCache");

        double sum = 0;

        for (String key : map.localKeySet()) {
            sum += map.get(key);
        }

        logger.info("Member {} calculated sum for {} local keys", hazelcastInstance.getCluster().getLocalMember(),
            map.localKeySet().size());

        return sum;
    }

}

