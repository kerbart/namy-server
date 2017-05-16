package com.kerbart.namyserver.repositories;

import com.kerbart.namyserver.model.Like;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface LikeRepository extends CrudRepository<Like, Long> {

	@Query("SELECT l FROM Like l WHERE l.user.token = :userToken and l.liked = true " )
    List<Like> getLikes(@Param("userToken") String userToken);


}
