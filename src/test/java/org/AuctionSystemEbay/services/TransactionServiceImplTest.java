package org.AuctionSystemEbay.services;

import org.AuctionSystemEbay.data.models.*;
import org.AuctionSystemEbay.data.repositories.*;
import org.AuctionSystemEbay.dtos.requests.*;
import org.AuctionSystemEbay.dtos.responses.*;
import org.AuctionSystemEbay.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TransactionServiceImplTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AuctionItemRepository auctionItemRepository;

    @BeforeEach
    public void setUp() {
        transactionRepository.deleteAll();
        userRepository.deleteAll();

        User testUser = new User();
        testUser.setUserId("789012");
        testUser.setUsername("testUser");
        testUser.setEmail("testUser@email.com");
        testUser.setPassword("password");
        testUser.setRole("BUYER");
        userRepository.save(testUser);
    }

    @Test
    public void createTransaction_Success() {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAuctionItemId("123456");
        transactionRequest.setBuyerId("789012");
        transactionRequest.setFinalPrice(500.0);

        TransactionResponse response = transactionService.createTransaction(transactionRequest);
        assertNotNull(response);
        assertNotNull(response.getTransactionId());
        assertEquals(500.0, response.getFinalPrice());
    }

    @Test
    public void createTransaction_BuyerNotFound_ThrowsException() {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAuctionItemId("123456");
        transactionRequest.setBuyerId("nonexistent");
        transactionRequest.setFinalPrice(500.0);

        assertThrows(RuntimeException.class, () -> transactionService.createTransaction(transactionRequest));
    }

    @Test
    public void createTransaction_InvalidRequest_ThrowsException() {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAuctionItemId(null);
        transactionRequest.setBuyerId("789012");
        transactionRequest.setFinalPrice(-10.0);

        assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(transactionRequest));
    }

    @Test
    public void getTransactionsByUserId_Success() {
        TransactionRequest transactionRequest1 = new TransactionRequest();
        transactionRequest1.setAuctionItemId("123456");
        transactionRequest1.setBuyerId("789012");
        transactionRequest1.setFinalPrice(500.0);
        transactionService.createTransaction(transactionRequest1);

        TransactionRequest transactionRequest2 = new TransactionRequest();
        transactionRequest2.setAuctionItemId("789012");
        transactionRequest2.setBuyerId("789012");
        transactionRequest2.setFinalPrice(600.0);
        transactionService.createTransaction(transactionRequest2);

        List<TransactionResponse> responses = transactionService.getTransactionsByUserId("789012");
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(500.0, responses.get(0).getFinalPrice());
        assertEquals(600.0, responses.get(1).getFinalPrice());
    }

    @Test
    void getTransactionsByUserId_NoTransactions_ReturnsEmpty() {
        List<TransactionResponse> responses = transactionService.getTransactionsByUserId("789012");
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    public void getTransactionsByUserId_InvalidUserId_ThrowsException() {
        assertThrows(UserNotFoundException.class, () -> transactionService.getTransactionsByUserId(null));
    }

    @Test
    public void getActiveTransactions_Success() {
        AuctionItem activeItem = new AuctionItem();
        activeItem.setItemId("123456");
        activeItem.setEndTime(LocalDateTime.now().plusDays(1));
        auctionItemRepository.save(activeItem);

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAuctionItemId("123456");
        transactionRequest.setBuyerId("789012");
        transactionRequest.setFinalPrice(500.0);
        transactionService.createTransaction(transactionRequest);

        List<TransactionResponse> responses = transactionService.getActiveTransactions();
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(500.0, responses.get(0).getFinalPrice());
    }

    @Test
    public void getActiveTransactions_ExpiredAuction_ReturnsEmpty() {
        AuctionItem expiredItem = new AuctionItem();
        expiredItem.setItemId("789012");
        expiredItem.setEndTime(LocalDateTime.now().minusDays(1));
        auctionItemRepository.save(expiredItem);

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAuctionItemId("789012");
        transactionRequest.setBuyerId("789012");
        transactionRequest.setFinalPrice(600.0);
        transactionService.createTransaction(transactionRequest);

        List<TransactionResponse> responses = transactionService.getActiveTransactions();
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

}