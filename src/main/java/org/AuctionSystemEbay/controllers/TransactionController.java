package org.AuctionSystemEbay.controllers;

import org.AuctionSystemEbay.dtos.requests.TransactionRequest;
import org.AuctionSystemEbay.dtos.responses.ApiResponse;
import org.AuctionSystemEbay.dtos.responses.TransactionResponse;
import org.AuctionSystemEbay.exceptions.AuctionSystemException;
import org.AuctionSystemEbay.services.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody TransactionRequest transactionRequest) {
        try {
            TransactionResponse response = transactionService.createTransaction(transactionRequest);
            return new ResponseEntity<>(new ApiResponse(response, true), HttpStatus.CREATED);
        } catch (AuctionSystemException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getTransactionsByUser(@PathVariable("userId") String userId) {
        try {
            List<TransactionResponse> responses = transactionService.getTransactionsByUserId(userId);
            return new ResponseEntity<>(new ApiResponse(responses, true), HttpStatus.OK);
        } catch (AuctionSystemException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/active")
    public ResponseEntity<?> getActiveTransactions() {
        try {
            List<TransactionResponse> responses = transactionService.getActiveTransactions();
            return new ResponseEntity<>(new ApiResponse(responses, true), HttpStatus.OK);
        } catch (AuctionSystemException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}