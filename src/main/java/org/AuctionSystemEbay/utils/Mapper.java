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




}