package com.lohika.hazelcastpresentation.cache.processor;

import java.util.Map;

import com.hazelcast.map.AbstractEntryProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple update entry processor to be executed.
 *
 * @author taras.matyashovsky
 */
public class UpdateEntryProcessor extends AbstractEntryProcessor<String, Double> {

    private final Logger logger = LoggerFactory.getLogger(UpdateEntryProcessor.class);

    public UpdateEntryProcessor() {
        super(true);
    }

    @Override
    public Object process(Map.Entry<String, Double> entry) {
        logger.info("Executing process for entry {}", entry);

        entry.setValue(entry.getValue() * 0.95);
        return null;
    }

}
