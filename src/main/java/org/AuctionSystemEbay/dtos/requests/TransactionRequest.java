package org.AuctionSystemEbay.dtos.requests;

import lombok.Data;

@Data
public class TransactionRequest {
    private String auctionItemId;
    private String buyerId;
    private Double finalPrice;
}