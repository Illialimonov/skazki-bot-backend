package com.example.ilia.tgbotapi.repo;

import com.example.ilia.tgbotapi.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByTgId(String tgId);

    boolean existsByTgId(String tgId);
}

