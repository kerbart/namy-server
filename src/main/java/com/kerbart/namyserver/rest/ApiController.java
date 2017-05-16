package com.kerbart.namyserver.rest;

import com.kerbart.namyserver.model.Prenom;
import com.kerbart.namyserver.repositories.PrenomRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by damien on 12/05/2017.
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    @Inject
    PrenomRepository prenomRepository;

    @GetMapping("firstname/info/{firstName}")
    public List<Prenom>  getInfo(@PathVariable("firstName") String firstName) {
    return prenomRepository.findByPrenomSansAccentIgnoreCase(firstName.toUpperCase());
    }

}
