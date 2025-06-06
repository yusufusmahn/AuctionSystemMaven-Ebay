package org.example.data.repositories;

import org.example.data.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface Users extends MongoRepository<User, String> {
    User findByEmail(String email);
}