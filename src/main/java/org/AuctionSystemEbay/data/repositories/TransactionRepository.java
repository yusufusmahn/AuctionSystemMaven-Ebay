package org.AuctionSystemEbay.data.repositories;

import org.AuctionSystemEbay.data.models.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

}