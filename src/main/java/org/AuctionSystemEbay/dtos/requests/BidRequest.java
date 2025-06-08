package org.AuctionSystemEbay.dtos.requests;

import lombok.Data;

@Data
public class BidRequest {
    private Double bidAmount;
    private String bidderId;
    private String auctionItemId;
}