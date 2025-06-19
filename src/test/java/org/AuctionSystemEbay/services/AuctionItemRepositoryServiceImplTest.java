package org.AuctionSystemEbay.services;

import static org.junit.jupiter.api.Assertions.*;


import org.AuctionSystemEbay.data.models.*;
import org.AuctionSystemEbay.data.repositories.*;
import org.AuctionSystemEbay.data.repositories.AuctionItemRepository;
import org.AuctionSystemEbay.dtos.requests.*;
import org.AuctionSystemEbay.dtos.responses.*;
import org.AuctionSystemEbay.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
 public class AuctionItemRepositoryServiceImplTest {

    @Autowired
    private AuctionItemService auctionItemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuctionItemRepository auctionItemRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    public void setUp() {
        auctionItemRepository.deleteAll();
        userRepository.deleteAll();
        bidRepository.deleteAll();

        User testUser = new User();
        testUser.setUserId("123456");
        testUser.setUsername("testUser");
        testUser.setEmail("testUser@gmail.com");
        testUser.setPassword("password");
        testUser.setRole("SELLER");
        userRepository.save(testUser);

        User buyer = new User();
        buyer.setUserId("789012");
        buyer.setUsername("NewBuyer");
        buyer.setEmail("buyer@gmail.com");
        buyer.setPassword("password");
        buyer.setRole("BUYER");
        userRepository.save(buyer);

        AuctionItem testItem = new AuctionItem();
        testItem.setItemId("654321");
        testItem.setTitle("Laptop");
        testItem.setDescription("New laptop");
        testItem.setStartingBid(100.0);
        testItem.setCurrentBid(100.0);
        testItem.setBuyItNowPrice(500.0);
        testItem.setSeller(testUser);
        testItem.setEndTime(LocalDateTime.now().plusDays(1));
        testItem.setStatus("ACTIVE");
        auctionItemRepository.save(testItem);
    }

    @Test
    public void createAuctionItem_Success() {
        AuctionItemRequest auctionItemRequest = new AuctionItemRequest();
        auctionItemRequest.setTitle("Phone");
        auctionItemRequest.setDescription("New phone");
        auctionItemRequest.setStartingBid(200.0);
        auctionItemRequest.setBuyItNowPrice(600.0);
        auctionItemRequest.setSellerId("123456");
        auctionItemRequest.setEndTime(LocalDateTime.now().plusDays(2));

        AuctionItemResponse response = auctionItemService.createAuctionItem(auctionItemRequest);
        assertNotNull(response);
        assertNotNull(response.getItemId());
        assertEquals("Phone", response.getTitle());
    }

    @Test
    public void createAuctionItem_SellerNotFound_ThrowsException() {
        AuctionItemRequest auctionItemRequest = new AuctionItemRequest();
        auctionItemRequest.setTitle("Phone");
        auctionItemRequest.setDescription("New phone");
        auctionItemRequest.setStartingBid(200.0);
        auctionItemRequest.setBuyItNowPrice(600.0);
        auctionItemRequest.setSellerId("nonexistent");
        auctionItemRequest.setEndTime(LocalDateTime.now().plusDays(2));

        assertThrows(RuntimeException.class, () -> auctionItemService.createAuctionItem(auctionItemRequest));
    }

    @Test
    public void createAuctionItem_BuyerRole_ThrowsException() {
        AuctionItemRequest auctionItemRequest = new AuctionItemRequest();
        auctionItemRequest.setTitle("Phone");
        auctionItemRequest.setDescription("New phone");
        auctionItemRequest.setStartingBid(200.0);
        auctionItemRequest.setBuyItNowPrice(600.0);
        auctionItemRequest.setSellerId("789012");
        auctionItemRequest.setEndTime(LocalDateTime.now().plusDays(2));

        assertThrows(AuctionItemCreationException.class, () -> auctionItemService.createAuctionItem(auctionItemRequest));
    }


    @Test
    public void getAuctionItemById_Success() {
        AuctionItemResponse response = auctionItemService.getAuctionItemById("654321");
        assertNotNull(response);
        assertEquals("Laptop", response.getTitle());
    }

    @Test
    public void getAuctionItemById_NotFound_ThrowsException() {
        assertThrows(ItemNotFoundException.class, () -> auctionItemService.getAuctionItemById("nonexistent"));
    }

    @Test
    public void getAuctionItemsBySellerId_Success() {
        List<AuctionItemResponse> responses = auctionItemService.getAuctionItemsBySellerId("123456");
        assertFalse(responses.isEmpty());
        assertEquals("Laptop", responses.get(0).getTitle());
    }

    @Test
    public void getAuctionItemsBySellerId_SellerNotFound_ThrowsException() {
        assertThrows(SellerNotFoundException.class, () -> auctionItemService.getAuctionItemsBySellerId("nonexistent"));
    }

    @Test
    public void getActiveAuctionItems_Success() {
        List<AuctionItemResponse> responses = auctionItemService.getActiveAuctionItems();
        assertFalse(responses.isEmpty());
        assertEquals("ACTIVE", responses.get(0).getStatus());
    }

    @Test
    public void closeExpiredAuctions_Success() {
        AuctionItem expiredItem = new AuctionItem();
        expiredItem.setItemId("987654");
        expiredItem.setTitle("Expired Laptop");
        expiredItem.setDescription("Old laptop");
        expiredItem.setStartingBid(50.0);
        expiredItem.setCurrentBid(50.0);
        expiredItem.setBuyItNowPrice(200.0);
        expiredItem.setSeller(userRepository.findById("123456").get());
        expiredItem.setEndTime(LocalDateTime.now().minusDays(2));
        expiredItem.setStatus("ACTIVE");
        auctionItemRepository.save(expiredItem);

        Bid bid = new Bid();
        bid.setBidId("bid1");
        bid.setAuctionItemId("987654");
        bid.setBidAmount(75.0);
        bid.setBidder(userRepository.findById("789012").get());
        LocalDateTime bidTime = LocalDateTime.now().minusHours(1);
        bid.setBidTime(bidTime);
        bidRepository.save(bid);

        Bid savedBid = bidRepository.findById("bid1").orElse(null);
        assertNotNull(savedBid);
        assertNotNull(savedBid.getBidTime());
        System.out.println("Saved bid time: " + savedBid.getBidTime());

        auctionItemService.closeExpiredAuctions();

        AuctionItem updatedItem = auctionItemRepository.findById("987654").orElse(null);
        assertNotNull(updatedItem);
        assertEquals("CLOSED", updatedItem.getStatus());

        List<Transaction> transactions = transactionRepository.findAll();
        assertFalse(transactions.isEmpty());
        assertEquals(75.0, transactions.get(0).getFinalPrice());

    }

}


