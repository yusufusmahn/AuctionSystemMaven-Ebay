package org.AuctionSystemEbay.data.repositories;

import org.AuctionSystemEbay.data.models.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
}