package com.kerbart.namyserver.repositories;

import com.kerbart.namyserver.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findByPhoneNumber(String phoneNumber);

    User findByToken(String token);

}
