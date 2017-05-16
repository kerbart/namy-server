package com.kerbart.namyserver.services;

import com.kerbart.namyserver.model.Like;
import com.kerbart.namyserver.model.Prenom;
import com.kerbart.namyserver.model.User;
import com.kerbart.namyserver.repositories.PrenomRepository;
import com.kerbart.namyserver.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository("likeService")
@Transactional
public class LikeService {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private PrenomRepository prenomRepository;

	@Autowired
	private UserRepository userRepo;


	public Like registerLike(String userToken, String prenomToken,  Boolean liked) {
		Like like = new Like();
		User user = userRepo.findByToken(userToken);
		Prenom prenom = prenomRepository.findByToken(prenomToken);
		if (user != null && prenom != null) {
			like.setPrenom(prenom);
			like.setUser(user);
			like.setLiked(liked);
			em.persist(like);
		}
		return like;
	}

}
