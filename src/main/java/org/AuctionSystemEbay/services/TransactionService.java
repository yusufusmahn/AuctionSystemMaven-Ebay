package org.AuctionSystemEbay.services;


import org.AuctionSystemEbay.dtos.requests.TransactionRequest;
import org.AuctionSystemEbay.dtos.responses.TransactionResponse;

import java.util.List;


public interface TransactionService {
    TransactionResponse createTransaction(TransactionRequest transactionRequest);
    List<TransactionResponse> getTransactionsByUserId(String userId);
    List<TransactionResponse> getActiveTransactions();
}