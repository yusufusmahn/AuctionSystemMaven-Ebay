package org.AuctionSystemEbay.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "bids")
public class Bid {
    @Id
    private String bidId;
    private Double bidAmount;
    @DBRef
    private User bidder;
    private String auctionItemId;
    private LocalDateTime bidTime;
}