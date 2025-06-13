package org.AuctionSystemEbay.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "auctionItems")
public class AuctionItem {
    @Id
    private String itemId;
    private String title;
    private String description;
    private Double startingBid;
    private Double currentBid = 0.0;
    private Double buyItNowPrice;
    @DBRef
    private User seller;
    private LocalDateTime endTime;
    private String status= "ACTIVE";
}