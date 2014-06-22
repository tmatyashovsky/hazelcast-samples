package com.lohika.hazelcastpresentation.task;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.Callable;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task that calculates average of cache values located on local node in not optimal way.
 *
 * @author taras.matyashovsky
 */
public class HazelcastAverageTask implements Callable<BigDecimal>, Serializable, HazelcastInstanceAware {

    private final Logger logger = LoggerFactory.getLogger(HazelcastAverageTask.class);
    private transient HazelcastInstance hazelcastInstance;

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public BigDecimal call() throws Exception {
        IMap<String, Double> map = this.hazelcastInstance.getMap("averagePresentationHazelcastDistributedCache");

        BigDecimal sum = BigDecimal.ZERO;

        for (Map.Entry<String, Double> entry : map.entrySet()) {
            boolean localKey = map.localKeySet().contains(entry.getKey());

            if (localKey) {
                sum = sum.add(BigDecimal.valueOf(entry.getValue()));
            }
        }

        logger.info("Member {} calculated average for {} local keys", hazelcastInstance.getCluster().getLocalMember(),
            map.localKeySet().size());

        return sum.divide(BigDecimal.valueOf(map.size()), 2, RoundingMode.HALF_UP);
    }

}

