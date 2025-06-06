package org.example.data.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuctionItems extends MongoRepository<org.example.data.models.AuctionItem, String> {
}