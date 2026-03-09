package com.iglesia.service;

import com.iglesia.Church;
import com.iglesia.ChurchRepository;
import com.iglesia.Person;
import com.iglesia.PersonRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PersonService {
    private final PersonRepository personRepository;
    private final ChurchRepository churchRepository;

    public PersonService(PersonRepository personRepository, ChurchRepository churchRepository) {
        this.personRepository = personRepository;
        this.churchRepository = churchRepository;
    }

    public Person create(String firstName, String lastName, String document, String phone, String email) {
        Church church = requireChurch();
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setDocument(document);
        person.setPhone(phone);
        person.setEmail(email);
        person.setChurch(church);
        personRepository.save(person);
        return person;
    }

    public List<Person> list() {
        Church church = requireChurch();
        return personRepository.findAllByChurchId(church.getId());
    }

    private Church requireChurch() {
        return churchRepository.findAll()
            .stream()
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe registrar una iglesia primero"));
    }
}
