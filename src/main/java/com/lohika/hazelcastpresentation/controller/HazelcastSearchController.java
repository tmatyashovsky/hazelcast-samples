package com.lohika.hazelcastpresentation.controller;

import java.util.Set;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lohika.hazelcastpresentation.domain.Person;

import static com.hazelcast.query.Predicates.like;

/**
 * Controller that handles all search requests related to statically defined Hazelcast cache.
 *
 * @author taras.matyashovsky
 */
@RestController
public class HazelcastSearchController {

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    ResponseEntity<String> fillPersonsMap() {
        IMap<String, Person> persons = hazelcastInstance.getMap("personsMap");

        persons.put("taras", new Person("matyashovsky"));
        persons.put("vladimir", new Person("tsukur"));
        persons.put("zenyk", new Person("matchyshyn"));

        return new ResponseEntity<String>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/search/{startsWith}", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<Set<Person>> searchPersonsLike(@PathVariable String startsWith) {
        IMap<String, Person> persons = hazelcastInstance.getMap("personsMap");

        Predicate likePredicate = like("name", startsWith + "%");
        Set<Person> matchingValues = (Set<Person>) persons.values(likePredicate);

        return new ResponseEntity<Set<Person>>(matchingValues, HttpStatus.OK);
    }

}
