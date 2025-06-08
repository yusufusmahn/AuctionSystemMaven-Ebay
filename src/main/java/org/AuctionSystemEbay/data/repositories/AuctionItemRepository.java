package org.AuctionSystemEbay.data.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuctionItemRepository extends MongoRepository<org.AuctionSystemEbay.data.models.AuctionItem, String> {
}