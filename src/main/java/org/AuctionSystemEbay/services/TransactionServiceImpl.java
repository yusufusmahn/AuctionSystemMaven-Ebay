package org.AuctionSystemEbay.services;

import org.AuctionSystemEbay.data.models.*;
import org.AuctionSystemEbay.data.repositories.*;
import org.AuctionSystemEbay.dtos.requests.*;
import org.AuctionSystemEbay.dtos.responses.*;
import org.AuctionSystemEbay.exceptions.UserNotFoundException;
import org.AuctionSystemEbay.utils.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuctionItemRepository auctionItemRepository;

    @Autowired
    private IdService idService;

    @Override
    public TransactionResponse createTransaction(TransactionRequest transactionRequest) {
        if (transactionRequest == null || transactionRequest.getAuctionItemId() == null ||
                transactionRequest.getBuyerId() == null || transactionRequest.getFinalPrice() == null ||
                transactionRequest.getFinalPrice() <= 0) {
            throw new IllegalArgumentException("Invalid transaction request: all fields must be non-null and final price must be positive");
        }


        User buyer = userRepository.findById(transactionRequest.getBuyerId())
                .orElseThrow(() -> new UserNotFoundException("Buyer not found: " + transactionRequest.getBuyerId()));

        Transaction transaction = Mapper.toTransaction(transactionRequest);
        transaction.setTransactionId(idService.generateUniqueId());
        transaction.setBuyer(buyer);
        Transaction savedTransaction = transactionRepository.save(transaction);
        return Mapper.toTransactionResponse(savedTransaction);
    }



    @Override
    public List<TransactionResponse> getTransactionsByUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new UserNotFoundException("User ID cannot be null or empty");
        }
        List<Transaction> allTransactions = transactionRepository.findAll();
        List<TransactionResponse> userTransactions = new ArrayList<>();

        for (Transaction transaction : allTransactions) {
            if (transaction.getBuyer() != null && userId.equals(transaction.getBuyer().getUserId())) {
                userTransactions.add(Mapper.toTransactionResponse(transaction));
            }
        }

        return userTransactions;
    }


    @Override
    public List<TransactionResponse> getActiveTransactions() {
        LocalDateTime now = LocalDateTime.now();
        List<Transaction> allTransactions = transactionRepository.findAll();
        List<TransactionResponse> activeTransactions = new ArrayList<>();

        for (Transaction transaction : allTransactions) {
            AuctionItem auctionItem = auctionItemRepository.findById(transaction.getAuctionItemId()).orElse(null);
            if (auctionItem != null && !auctionItem.getEndTime().isBefore(now)) {
                activeTransactions.add(Mapper.toTransactionResponse(transaction));
            }
        }

        return activeTransactions;
    }


}