package com.kerbart.namyserver.repositories;

import com.kerbart.namyserver.model.Prenom;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PrenomRepository extends CrudRepository<Prenom, Long> {

    List<Prenom> findByPrenom(String prenom);

    List<Prenom> findByPrenomSansAccent(String prenomSansAccent);

    List<Prenom> findByPrenomSansAccentIgnoreCase(String prenomSansAccent);

    List<Prenom> findByPrenomSansAccentIgnoreCaseContaining(String prenomSansAccent);

    List<Prenom> findAll();

    Prenom findByToken(String token);


}
