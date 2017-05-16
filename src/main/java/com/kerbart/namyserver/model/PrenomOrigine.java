package com.kerbart.namyserver.model;

import javax.persistence.*;

/**
 * Created by damien on 01/03/2017.
 */
@Entity
public class PrenomOrigine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    Long id;

    @Column
    String prenom;

    @Column
    String origine;

    @Column
    String subOrigine;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getOrigine() {
        return origine;
    }

    public void setOrigine(String origine) {
        this.origine = origine;
    }

    public String getSubOrigine() {
        return subOrigine;
    }

    public void setSubOrigine(String subOrigine) {
        this.subOrigine = subOrigine;
    }
}
