package com.github.kafkadlq;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonService {

    private final List<Person> persons = new ArrayList<>();

    public void save(final Person person) {
        persons.add(person);
    }

    public List<Person> getPersons() {
        return persons;
    }
}

