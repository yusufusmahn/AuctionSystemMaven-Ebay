package org.AuctionSystemEbay.services;


import org.AuctionSystemEbay.data.models.*;
import org.AuctionSystemEbay.data.repositories.*;
import org.AuctionSystemEbay.dtos.requests.*;
import org.AuctionSystemEbay.dtos.responses.*;
import org.AuctionSystemEbay.exceptions.*;
import org.AuctionSystemEbay.utils.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuctionItemServiceImpl implements AuctionItemService {

    @Autowired
    private IdService idService;

    @Autowired
    private AuctionItemRepository auctionItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private TransactionService transactionService;


    @Override
    public AuctionItemResponse createAuctionItem(AuctionItemRequest auctionItemRequest) {
        if (auctionItemRequest.getTitle() == null || auctionItemRequest.getStartingBid() == null || auctionItemRequest.getSellerId() == null) {
            throw new IllegalArgumentException("Title, starting bid, and seller ID are required");
        }

        if (auctionItemRequest.getTitle().length() > 50) {
            throw new IllegalArgumentException("Title cannot exceed 100 characters");
        }

        if (auctionItemRequest.getStartingBid() <= 0) {
            throw new AuctionItemCreationException("Starting bid must be greater than 0");
        }

        LocalDateTime now = LocalDateTime.now();
        if (auctionItemRequest.getEndTime() == null || auctionItemRequest.getEndTime().isBefore(now)) {
            throw new AuctionItemCreationException("End time must be in the future");
        }

        AuctionItem auctionItem = Mapper.toAuctionItem(auctionItemRequest);
        auctionItem.setItemId(idService.generateUniqueId());

        User seller = userRepository.findById(auctionItemRequest.getSellerId())
                .orElseThrow(() -> new AuctionItemCreationException("Seller not found with ID: " + auctionItemRequest.getSellerId()));

        if (!"SELLER".equalsIgnoreCase(seller.getRole())) {
            throw new AuctionItemCreationException("Only users with SELLER role can create auctions");
        }

        auctionItem.setSeller(seller);
        AuctionItem savedItem = auctionItemRepository.save(auctionItem);
        return Mapper.toAuctionItemResponse(savedItem);
    }


    @Override
    public AuctionItemResponse getAuctionItemById(String itemId) {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new IllegalArgumentException("Item ID cannot be null or empty");
        }

        AuctionItem auctionItem = auctionItemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Auction item not found with ID: " + itemId));
        return Mapper.toAuctionItemResponse(auctionItem);
    }

    @Override
    public List<AuctionItemResponse> getAuctionItemsBySellerId(String sellerId) {
        if (sellerId == null || sellerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Seller ID cannot be null or empty");
        }

        if (!userRepository.existsById(sellerId)) {
            throw new SellerNotFoundException("Seller not found with ID: " + sellerId);
        }

        List<AuctionItem> items = auctionItemRepository.findBySellerUserId(sellerId);
        return Mapper.toAuctionItemResponseList(items);
    }


    @Override
    public List<AuctionItemResponse> getActiveAuctionItems() {
        LocalDateTime now = LocalDateTime.now();
        List<AuctionItem> activeItems = auctionItemRepository.findByStatusAndEndTimeAfter("ACTIVE", now);
        return Mapper.toAuctionItemResponseList(activeItems);
    }


    @Override
    public void closeExpiredAuctions() {
        LocalDateTime now = LocalDateTime.now();
        List<AuctionItem> activeAuctions = auctionItemRepository.findByStatusAndEndTimeBefore("ACTIVE", now);

        for (AuctionItem auctionItem : activeAuctions) {
            auctionItem.setStatus("CLOSED");
            auctionItemRepository.save(auctionItem);

            List<Bid> bids = bidRepository.findByAuctionItemId(auctionItem.getItemId());
            if (bids != null && !bids.isEmpty()) {
                Bid winningBid = bids.get(0);
                for (Bid bid : bids) {
                    if (bid.getBidAmount() > winningBid.getBidAmount() ||
                            (bid.getBidAmount().equals(winningBid.getBidAmount()) && bid.getBidTime().isBefore(winningBid.getBidTime()))) {
                        winningBid = bid;
                    }
                }
                if (winningBid.getBidder() != null) {
                    TransactionRequest transactionRequest = Mapper.toTransactionRequestFromAuctionAndBid(auctionItem, winningBid);
                    transactionService.createTransaction(transactionRequest);
                }
            }
        }
    }


}