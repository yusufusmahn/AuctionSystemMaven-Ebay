package org.AuctionSystemEbay.services;


import org.AuctionSystemEbay.dtos.requests.TransactionRequest;
import org.AuctionSystemEbay.dtos.responses.TransactionResponse;


public interface TransactionService {
    TransactionResponse createTransaction(TransactionRequest transactionRequest);
}