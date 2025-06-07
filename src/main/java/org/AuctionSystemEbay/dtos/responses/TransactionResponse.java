package org.AuctionSystemEbay.dtos.responses;

import lombok.Data;

@Data
public class TransactionResponse {
    private String transactionId;
    private String auctionItemId;
    private String buyerId;
    private Double finalPrice;
    private String transactionTime;
}