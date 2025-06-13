package org.AuctionSystemEbay.services;

import org.AuctionSystemEbay.data.models.*;
import org.AuctionSystemEbay.data.repositories.*;
import org.AuctionSystemEbay.dtos.requests.BidRequest;
import org.AuctionSystemEbay.dtos.requests.TransactionRequest;
import org.AuctionSystemEbay.dtos.responses.*;
import org.AuctionSystemEbay.exceptions.*;
import org.AuctionSystemEbay.utils.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BidServiceImpl implements BidService {

    @Autowired
    private IdService idService;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private AuctionItemRepository auctionItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionService transactionService;


    @Override
    public BidResponse placeBid(BidRequest bidRequest) {
        if (bidRequest.getAuctionItemId() == null || bidRequest.getBidAmount() == null || bidRequest.getBidderId() == null) {
            throw new IllegalArgumentException("Auction item ID, bid amount, and bidder ID are required");
        }

        if (bidRequest.getBidAmount() <= 0) {
            throw new InvalidBidException("Bid amount must be greater than 0");
        }

        Bid bid = Mapper.toBid(bidRequest);
        bid.setBidId(idService.generateUniqueId());
        bid.setBidTime(LocalDateTime.now());

        User bidder = userRepository.findById(bidRequest.getBidderId())
                .orElseThrow(() -> new UserNotFoundException("Bidder not found with ID: " + bidRequest.getBidderId()));
        bid.setBidder(bidder);

        AuctionItem auctionItem = auctionItemRepository.findById(bid.getAuctionItemId())
                .orElseThrow(() -> new ItemNotFoundException("Auction item not found with ID: " + bid.getAuctionItemId()));

        if (auctionItem.getEndTime().isBefore(LocalDateTime.now())) {
            throw new AuctionExpiredException("Auction has expired for item ID: " + auctionItem.getItemId());
        }

        if ("CLOSED".equalsIgnoreCase(auctionItem.getStatus())) {
            throw new AuctionClosedException("Cannot place bid on a closed auction with ID: " + auctionItem.getItemId());
        }

        Double currentBid = auctionItem.getCurrentBid();
        if (currentBid == null) {
            currentBid = auctionItem.getStartingBid();
        }
        if (bid.getBidAmount() <= currentBid) {
            throw new InvalidBidException("Bid amount must be higher than current bid: " + currentBid);
        }


        Double buyItNowPrice = auctionItem.getBuyItNowPrice();
        if (buyItNowPrice != null && bid.getBidAmount() >= buyItNowPrice) {
            auctionItem.setStatus("CLOSED");
            auctionItem.setCurrentBid(bid.getBidAmount());
            auctionItemRepository.save(auctionItem);

            TransactionRequest transactionRequest = Mapper.toTransactionRequestFromAuctionAndBid(auctionItem, bid);
            transactionService.createTransaction(transactionRequest);

        } else {
            auctionItem.setCurrentBid(bid.getBidAmount());
            auctionItemRepository.save(auctionItem);
        }

        Bid savedBid = bidRepository.save(bid);
        return Mapper.toBidResponse(savedBid);

    }

    @Override
    public List<BidResponse> getBidsByAuctionItemId(String auctionItemId) {
        List<Bid> bids = bidRepository.findByAuctionItemId(auctionItemId);
        return Mapper.toBidResponseList(bids);
    }



    @Override
    public List<BidResponse> getActiveBids() {
        LocalDateTime now = LocalDateTime.now();
        List<Bid> allBids = bidRepository.findAll();
        List<BidResponse> activeBids = new ArrayList<>();

        for (Bid bid : allBids) {
            AuctionItem auctionItem = auctionItemRepository.findById(bid.getAuctionItemId()).orElse(null);
            if (auctionItem != null && !auctionItem.getEndTime().isBefore(now)) {
                activeBids.add(Mapper.toBidResponse(bid));
            }
        }

        return activeBids;
    }
}