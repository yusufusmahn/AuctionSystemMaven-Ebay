package org.example.data.repositories;

import org.example.data.models.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface Transactions extends MongoRepository<Transaction, String> {
}