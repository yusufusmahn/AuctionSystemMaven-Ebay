package org.AuctionSystemEbay.data.repositories;

import org.AuctionSystemEbay.data.models.AuctionItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuctionItemRepository extends MongoRepository<org.AuctionSystemEbay.data.models.AuctionItem, String> {
    List<AuctionItem> findBySellerUserId(String userId);
    List<AuctionItem> findByStatus(String status);
    List<AuctionItem> findByStatusAndEndTimeAfter(String status, LocalDateTime endTime);
    List<AuctionItem> findByStatusAndEndTimeBefore(String status, LocalDateTime endTime);
}