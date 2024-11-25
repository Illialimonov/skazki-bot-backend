package com.example.ilia.tgbotapi.repo;

import com.example.ilia.tgbotapi.models.AudioBook;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudioBookRepository extends MongoRepository<AudioBook, String> {
}
