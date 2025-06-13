package org.AuctionSystemEbay.services;

import org.AuctionSystemEbay.data.models.*;
import org.AuctionSystemEbay.data.repositories.*;
import org.AuctionSystemEbay.dtos.requests.BidRequest;
import org.AuctionSystemEbay.dtos.responses.BidResponse;
import org.AuctionSystemEbay.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BidServiceImplTest {

    @Autowired
    private BidService bidService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuctionItemRepository auctionItemRepository;

    @Autowired
    private BidRepository bidRepository;

    @BeforeEach
    void setUp() {
        bidRepository.deleteAll();
        auctionItemRepository.deleteAll();
        userRepository.deleteAll();

        User testUser = new User();
        testUser.setUserId("123456");
        testUser.setUsername("John Doe");
        testUser.setEmail("john@email.com");
        testUser.setPassword("password123");
        testUser.setRole("BUYER");
        userRepository.save(testUser);

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
    void placeBid_Success() {
        BidRequest bidRequest = new BidRequest();
        bidRequest.setBidAmount(150.0);
        bidRequest.setBidderId("123456");
        bidRequest.setAuctionItemId("654321");

        BidResponse response = bidService.placeBid(bidRequest);
        assertNotNull(response);
        assertNotNull(response.getBidId());
        assertEquals(150.0, response.getBidAmount());
    }

    @Test
    void placeBid_BidderNotFound_ThrowsException() {
        BidRequest bidRequest = new BidRequest();
        bidRequest.setBidAmount(150.0);
        bidRequest.setBidderId("nonexistent");
        bidRequest.setAuctionItemId("654321");

        assertThrows(RuntimeException.class, () -> bidService.placeBid(bidRequest));
    }

    @Test
    void placeBid_AuctionExpired_ThrowsException() {
        AuctionItem expiredItem = new AuctionItem();
        expiredItem.setItemId("987654");
        expiredItem.setTitle("Expired Laptop");
        expiredItem.setDescription("Old laptop");
        expiredItem.setStartingBid(50.0);
        expiredItem.setCurrentBid(50.0);
        expiredItem.setBuyItNowPrice(200.0);
        expiredItem.setSeller(userRepository.findById("123456").get());
        expiredItem.setEndTime(LocalDateTime.now().minusDays(1));
        expiredItem.setStatus("ACTIVE");
        auctionItemRepository.save(expiredItem);

        BidRequest bidRequest = new BidRequest();
        bidRequest.setBidAmount(75.0);
        bidRequest.setBidderId("123456");
        bidRequest.setAuctionItemId("987654");

        assertThrows(AuctionExpiredException.class, () -> bidService.placeBid(bidRequest));
    }

    @Test
    void placeBid_InvalidBidAmount_ThrowsException() {
        BidRequest bidRequest = new BidRequest();
        bidRequest.setBidAmount(50.0);
        bidRequest.setBidderId("123456");
        bidRequest.setAuctionItemId("654321");

        assertThrows(InvalidBidException.class, () -> bidService.placeBid(bidRequest));
    }

    @Test
    void placeBid_ItemNotFound_ThrowsException() {
        BidRequest bidRequest = new BidRequest();
        bidRequest.setBidAmount(150.0);
        bidRequest.setBidderId("123456");
        bidRequest.setAuctionItemId("nonexistent");

        assertThrows(ItemNotFoundException.class, () -> bidService.placeBid(bidRequest));
    }

    @Test
    void getBidsByAuctionItemId_Success() {
        BidRequest bidRequest = new BidRequest();
        bidRequest.setBidAmount(150.0);
        bidRequest.setBidderId("123456");
        bidRequest.setAuctionItemId("654321");
        bidService.placeBid(bidRequest);

        List<BidResponse> responses = bidService.getBidsByAuctionItemId("654321");
        assertFalse(responses.isEmpty());
        assertEquals(150.0, responses.get(0).getBidAmount());
    }

    @Test
    void getBidsByAuctionItemId_NoBids_ReturnsEmpty() {
        List<BidResponse> responses = bidService.getBidsByAuctionItemId("654321");
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void getActiveBids_ActiveAndExpiredAuctions_ReturnsOnlyActive() {
        Bid activeBid = new Bid();
        activeBid.setBidId("bid1");
        activeBid.setAuctionItemId("654321");
        activeBid.setBidAmount(200.0);
        activeBid.setBidder(userRepository.findById("123456").get());
        bidRepository.save(activeBid);

        AuctionItem expiredItem = new AuctionItem();
        expiredItem.setItemId("987654");
        expiredItem.setEndTime(LocalDateTime.now().minusDays(1));
        auctionItemRepository.save(expiredItem);

        Bid expiredBid = new Bid();
        expiredBid.setBidId("bid2");
        expiredBid.setAuctionItemId("987654");
        expiredBid.setBidAmount(75.0);
        expiredBid.setBidder(userRepository.findById("123456").get());
        bidRepository.save(expiredBid);

        List<BidResponse> responses = bidService.getActiveBids();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(200.0, responses.get(0).getBidAmount());
    }
}