package org.AuctionSystemEbay.utils;


import org.AuctionSystemEbay.data.models.*;
import org.AuctionSystemEbay.dtos.requests.*;
import org.AuctionSystemEbay.dtos.responses.*;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Mapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String convertToLowerCase(String input) {
        if (input != null) {
            return input.toLowerCase();
        }
        return null;
    }

    public static String toSentenceCase(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        String[] words = input.trim().split("\\s+");
        String result = "";
        for (int i = 0; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                String word = words[i].toLowerCase();
                result += Character.toUpperCase(word.charAt(0)) + word.substring(1);
                if (i < words.length - 1) {
                    result += " ";
                }
            }
        }
        return result;
    }

    public static String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime != null) {
            return dateTime.format(DATE_TIME_FORMATTER);
        }
        return null;
    }

    public static UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setUsername(toSentenceCase(user.getUsername()));
        response.setEmail(convertToLowerCase(user.getEmail()));
        response.setRole(convertToLowerCase(user.getRole()));
        return response;
    }

    public static User toUser(UserRequest userRequest) {
        User user = new User();
        user.setUsername(toSentenceCase(userRequest.getUsername()));
        user.setEmail(convertToLowerCase(userRequest.getEmail()));
        user.setPassword(userRequest.getPassword());
        user.setRole(userRequest.getRole());
        return user;
    }

    public static LoginResponse toLoginResponse(User user, String message) {
        LoginResponse response = new LoginResponse();
        response.setUserId(user.getUserId());
        response.setUsername(toSentenceCase(user.getUsername()));
        response.setRole(convertToLowerCase(user.getRole()));
        response.setMessage(message);
        return response;
    }

    public static AuctionItemResponse toAuctionItemResponse(AuctionItem item) {
        AuctionItemResponse response = new AuctionItemResponse();
        response.setItemId(item.getItemId());
        response.setTitle(item.getTitle());
        response.setDescription(item.getDescription());
        response.setStartingBid(item.getStartingBid());
        response.setCurrentBid(item.getCurrentBid());
        response.setBuyItNowPrice(item.getBuyItNowPrice());
        if (item.getSeller() != null) {
            response.setSellerId(item.getSeller().getUserId());
        } else {
            response.setSellerId(null);
        }
        response.setEndTime(formatLocalDateTime(item.getEndTime()));
        response.setStatus(item.getStatus());
        return response;
    }

    public static AuctionItem toAuctionItem(AuctionItemRequest itemRequest) {
        AuctionItem item = new AuctionItem();
        item.setTitle(itemRequest.getTitle());
        item.setDescription(itemRequest.getDescription());
        item.setStartingBid(itemRequest.getStartingBid());
        item.setBuyItNowPrice(itemRequest.getBuyItNowPrice());
        item.setEndTime(itemRequest.getEndTime());
        item.setStatus("ACTIVE");
        return item;
    }

    public static List<AuctionItemResponse> toAuctionItemResponseList(List<AuctionItem> items) {
        List<AuctionItemResponse> responses = new ArrayList<>();
        for (AuctionItem item : items) {
            responses.add(toAuctionItemResponse(item));
        }
        return responses;
    }

    public static BidResponse toBidResponse(Bid bid) {
        BidResponse response = new BidResponse();
        response.setBidId(bid.getBidId());
        response.setBidAmount(bid.getBidAmount());
        if (bid.getBidder() != null) {
            response.setBidderId(bid.getBidder().getUserId());
        } else {
            response.setBidderId(null);
        }
        response.setAuctionItemId(bid.getAuctionItemId());
        response.setBidTime(formatLocalDateTime(bid.getBidTime()));
        return response;
    }

    public static Bid toBid(BidRequest bidRequest) {
        Bid bid = new Bid();
        bid.setBidAmount(bidRequest.getBidAmount());
        bid.setAuctionItemId(bidRequest.getAuctionItemId());
        bid.setBidTime(LocalDateTime.now());
        return bid;
    }

    public static List<BidResponse> toBidResponseList(List<Bid> bids) {
        List<BidResponse> responses = new ArrayList<>();
        for (Bid bid : bids) {
            responses.add(toBidResponse(bid));
        }
        return responses;
    }

    public static TransactionResponse toTransactionResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setTransactionId(transaction.getTransactionId());
        response.setAuctionItemId(transaction.getAuctionItemId());
        if (transaction.getBuyer() != null) {
            response.setBuyerId(transaction.getBuyer().getUserId());
        } else {
            response.setBuyerId(null);
        }
        response.setFinalPrice(transaction.getFinalPrice());
        response.setTransactionTime(formatLocalDateTime(transaction.getTransactionTime()));
        return response;
    }

    public static Transaction toTransaction(TransactionRequest transactionRequest) {
        Transaction transaction = new Transaction();
        transaction.setAuctionItemId(transactionRequest.getAuctionItemId());
        transaction.setFinalPrice(transactionRequest.getFinalPrice());
        transaction.setTransactionTime(LocalDateTime.now());
        return transaction;
    }


    public static TransactionRequest toTransactionRequestFromAuctionAndBid(AuctionItem auctionItem, Bid bid) {
        if (auctionItem == null || bid == null || bid.getBidder() == null) {
            throw new IllegalArgumentException("Auction item, bid, and bidder cannot be null");
        }
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAuctionItemId(auctionItem.getItemId());
        transactionRequest.setBuyerId(bid.getBidder().getUserId());
        transactionRequest.setFinalPrice(bid.getBidAmount());
        return transactionRequest;
    }



}