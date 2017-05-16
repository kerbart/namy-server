package com.kerbart.namyserver.model;

import javax.persistence.*;

@Entity
public class PrenomMetaData {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	Long id;

	@Column
	String prenomOrigin;

	@Column
	Long longueure;

	@Column
	String origine;
	
	@Column
	Double frequence;

	@ManyToOne
	Prenom prenom;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPrenomOrigin() {
		return prenomOrigin;
	}

	public void setPrenomOrigin(String prenomOrigin) {
		this.prenomOrigin = prenomOrigin;
	}

	public Long getLongueure() {
		return longueure;
	}

	public void setLongueure(Long longueure) {
		this.longueure = longueure;
	}

	public String getOrigine() {
		return origine;
	}

	public void setOrigine(String origine) {
		this.origine = origine;
	}

	public Double getFrequence() {
		return frequence;
	}

	public void setFrequence(Double frequence) {
		this.frequence = frequence;
	}

	public Prenom getPrenom() {
		return prenom;
	}

	public void setPrenom(Prenom prenom) {
		this.prenom = prenom;
	}
}
