package com.kerbart.namyserver.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "likes")
public class Like {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	Long id;

	@ManyToOne
	User user;
	
	@ManyToOne
    Prenom prenom;
	
	@Column
	Boolean liked;

	@Column
	Long ordre;

	@Column
	@Temporal(TemporalType.DATE)
	Date date;

	public Like() {
		this.date = new Date();
		this.ordre = 0L;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Prenom getPrenom() {
		return prenom;
	}

	public void setPrenom(Prenom prenom) {
		this.prenom = prenom;
	}

	public Boolean getLiked() {
		return liked;
	}

	public void setLiked(Boolean liked) {
		this.liked = liked;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Long getOrdre() {
		return ordre;
	}

	public void setOrdre(Long ordre) {
		this.ordre = ordre;
	}
}
