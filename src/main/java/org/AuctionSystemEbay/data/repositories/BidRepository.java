package org.AuctionSystemEbay.data.repositories;

import org.AuctionSystemEbay.data.models.Bid;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface BidRepository extends MongoRepository<Bid, String> {
        List<Bid> findByAuctionItemId(String auctionItemId);


}