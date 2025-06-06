package org.example.data.repositories;

import org.example.data.models.Bid;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface Bids extends MongoRepository<Bid, String> {
}