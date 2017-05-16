package com.kerbart.namyserver.model;

import java.util.Date;
import javax.persistence.*;

/**
 * Created by damien on 01/03/2017.
 */
@Entity
public class PrenomOccurence {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    Long id;

    @ManyToOne
    Prenom prenom;

    @Temporal(TemporalType.DATE)
    Date annee;

    @Column
    Long nombre;

    @Column
    Boolean masculin;

    @Column
    Boolean feminin;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getAnnee() {
        return annee;
    }

    public void setAnnee(Date annee) {
        this.annee = annee;
    }

    public Long getNombre() {
        return nombre;
    }

    public void setNombre(Long nombre) {
        this.nombre = nombre;
    }

    public Boolean getMasculin() {
        return masculin;
    }

    public void setMasculin(Boolean masculin) {
        this.masculin = masculin;
    }

    public Boolean getFeminin() {
        return feminin;
    }

    public void setFeminin(Boolean feminin) {
        this.feminin = feminin;
    }

    public Prenom getPrenom() {
        return prenom;
    }

    public void setPrenom(Prenom prenom) {
        this.prenom = prenom;
    }
}
