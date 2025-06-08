package org.AuctionSystemEbay.dtos.responses;

import lombok.Data;

@Data
public class AuctionItemResponse {
    private String itemId;
    private String title;
    private String description;
    private Double startingBid;
    private Double currentBid;
    private Double buyItNowPrice;
    private String sellerId;
    private String endTime;
    private String status;
}