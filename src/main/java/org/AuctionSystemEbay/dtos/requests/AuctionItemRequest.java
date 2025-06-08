package org.AuctionSystemEbay.dtos.requests;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuctionItemRequest {
    private String title;
    private String description;
    private Double startingBid;
    private Double buyItNowPrice;
    private String sellerId;
    private LocalDateTime endTime;
}