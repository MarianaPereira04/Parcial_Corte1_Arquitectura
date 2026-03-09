package com.iglesia.service;

import com.iglesia.Church;
import com.iglesia.ChurchRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ChurchService {
    private final ChurchRepository churchRepository;

    public ChurchService(ChurchRepository churchRepository) {
        this.churchRepository = churchRepository;
    }

    public Church create(String name, String address) {
        if (churchRepository.count() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe una iglesia registrada");
        }
        Church church = new Church();
        church.setName(name);
        church.setAddress(address);
        churchRepository.save(church);
        return church;
    }

    public Church getChurch() {
        return churchRepository.findAll()
            .stream()
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay iglesia registrada"));
    }
}
