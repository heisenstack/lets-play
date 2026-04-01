package com.lets_play.lets_play.repository;

import org.springframework.stereotype.Repository;
import com.lets_play.lets_play.model.User;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
}
