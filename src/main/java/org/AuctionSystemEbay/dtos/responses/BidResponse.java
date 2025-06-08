package org.AuctionSystemEbay.dtos.responses;

import lombok.Data;

@Data
public class BidResponse {
    private String bidId;
    private Double bidAmount;
    private String bidderId;
    private String auctionItemId;
    private String bidTime;
}