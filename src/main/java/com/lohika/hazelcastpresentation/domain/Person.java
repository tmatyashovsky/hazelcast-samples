package com.lohika.hazelcastpresentation.domain;

import java.io.Serializable;

/**
 * Simple POJO used for searching.
 *
 * @author taras.matyashovsky
 */
public class Person implements Serializable {

    private String name;

    public Person(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
