package org.AuctionSystemEbay.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String transactionId;
    private String auctionItemId;
    @DBRef
    private User buyer;
    private Double finalPrice;
    private LocalDateTime transactionTime;
}