package org.AuctionSystemEbay.services;

import org.AuctionSystemEbay.data.models.*;
import org.AuctionSystemEbay.data.repositories.*;
import org.AuctionSystemEbay.dtos.requests.*;
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
public class BidServiceImplTest {

    @Autowired
    private BidService bidService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuctionItemRepository auctionItemRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private UserService userService;

    private User seller;
    private User bidder;

    @BeforeEach
    public void setUp() {
        bidRepository.deleteAll();
        auctionItemRepository.deleteAll();
        userRepository.deleteAll();

        seller = new User();
        seller.setUserId("seller1");
        seller.setUsername("NewSeller");
        seller.setEmail("newseller@gmail.com");
        seller.setPassword("password");
        seller.setRole("SELLER");
        userRepository.save(seller);

        bidder = new User();
        bidder.setUserId("bidder123");
        bidder.setUsername("NewBuyer");
        bidder.setEmail("newbuyer@gmail.com");
        bidder.setPassword("password");
        bidder.setRole("BUYER");
        userRepository.save(bidder);

        AuctionItem testItem = new AuctionItem();
        testItem.setItemId("item1");
        testItem.setTitle("Laptop");
        testItem.setDescription("New laptop");
        testItem.setStartingBid(100.0);
        testItem.setCurrentBid(100.0);
        testItem.setBuyItNowPrice(500.0);
        testItem.setSeller(seller);
        testItem.setEndTime(LocalDateTime.now().plusDays(1));
        testItem.setStatus("ACTIVE");
        auctionItemRepository.save(testItem);
    }

    @Test
    public void placeBid_Success() {
        BidRequest bidRequest = new BidRequest();
        bidRequest.setBidAmount(150.0);
        bidRequest.setBidderId("bidder123");
        bidRequest.setAuctionItemId("item1");

        BidResponse response = bidService.placeBid(bidRequest);
        assertNotNull(response);
        assertNotNull(response.getBidId());
        assertEquals(150.0, response.getBidAmount());
    }

    @Test
    public void placeBid_BidderNotFound_ThrowsException() {
        BidRequest bidRequest = new BidRequest();
        bidRequest.setBidAmount(150.0);
        bidRequest.setBidderId("nonexistent");
        bidRequest.setAuctionItemId("item1");

        assertThrows(UserNotFoundException.class, () -> bidService.placeBid(bidRequest));
    }

    @Test
    public void placeBid_AuctionExpired_ThrowsException() {
        AuctionItem expiredItem = new AuctionItem();
        expiredItem.setItemId("expired1");
        expiredItem.setTitle("Expired Laptop");
        expiredItem.setDescription("Old laptop");
        expiredItem.setStartingBid(50.0);
        expiredItem.setCurrentBid(50.0);
        expiredItem.setBuyItNowPrice(200.0);
        expiredItem.setSeller(seller);
        expiredItem.setEndTime(LocalDateTime.now().minusDays(1));
        expiredItem.setStatus("ACTIVE");
        auctionItemRepository.save(expiredItem);

        BidRequest bidRequest = new BidRequest();
        bidRequest.setBidAmount(75.0);
        bidRequest.setBidderId("bidder123");
        bidRequest.setAuctionItemId("expired1");

        assertThrows(AuctionExpiredException.class, () -> bidService.placeBid(bidRequest));
    }

    @Test
    public void placeBid_InvalidBidAmount_ThrowsException() {
        BidRequest bidRequest = new BidRequest();
        bidRequest.setBidAmount(50.0);
        bidRequest.setBidderId("bidder123");
        bidRequest.setAuctionItemId("item1");

        assertThrows(InvalidBidException.class, () -> bidService.placeBid(bidRequest));
    }

    @Test
    public void placeBid_ItemNotFound_ThrowsException() {
        BidRequest bidRequest = new BidRequest();
        bidRequest.setBidAmount(150.0);
        bidRequest.setBidderId("bidder123");
        bidRequest.setAuctionItemId("nonexistent");

        assertThrows(ItemNotFoundException.class, () -> bidService.placeBid(bidRequest));
    }

    @Test
    public void getBidsByAuctionItemId_Success() {
        BidRequest bidRequest = new BidRequest();
        bidRequest.setBidAmount(150.0);
        bidRequest.setBidderId("bidder123");
        bidRequest.setAuctionItemId("item1");
        bidService.placeBid(bidRequest);

        List<BidResponse> responses = bidService.getBidsByAuctionItemId("item1");
        assertFalse(responses.isEmpty());
        assertEquals(150.0, responses.get(0).getBidAmount());
    }

    @Test
    public void getBidsByAuctionItemId_NoBids_ReturnsEmpty() {
        List<BidResponse> responses = bidService.getBidsByAuctionItemId("item1");
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    public void getActiveBids_ActiveAndExpiredAuctions_ReturnsOnlyActive() {
        Bid activeBid = new Bid();
        activeBid.setBidId("bid1");
        activeBid.setAuctionItemId("item1");
        activeBid.setBidAmount(200.0);
        activeBid.setBidder(bidder);
        bidRepository.save(activeBid);

        AuctionItem expiredItem = new AuctionItem();
        expiredItem.setItemId("expired2");
        expiredItem.setTitle("Expired Item");
        expiredItem.setEndTime(LocalDateTime.now().minusDays(1));
        expiredItem.setSeller(seller);
        auctionItemRepository.save(expiredItem);

        Bid expiredBid = new Bid();
        expiredBid.setBidId("bid2");
        expiredBid.setAuctionItemId("expired2");
        expiredBid.setBidAmount(75.0);
        expiredBid.setBidder(bidder);
        bidRepository.save(expiredBid);

        List<BidResponse> responses = bidService.getActiveBids();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(200.0, responses.get(0).getBidAmount());
    }

    @Test
    public void placeBid_SellerCannotBidOnOwnItem_ThrowsInvalidBidException() {
        UpdateRoleRequest updateRoleRequest = new UpdateRoleRequest();
        updateRoleRequest.setNewRole("BUYER");
        userService.updateUserRole("seller1", updateRoleRequest);


        BidRequest bidRequest = new BidRequest();
        bidRequest.setAuctionItemId("item1");
        bidRequest.setBidderId("seller1");
        bidRequest.setBidAmount(150.0);

        InvalidBidException exception = assertThrows(InvalidBidException.class, () -> {
            bidService.placeBid(bidRequest);
        });

        assertEquals("You cannot bid on an item you listed for auction", exception.getMessage());
    }
}