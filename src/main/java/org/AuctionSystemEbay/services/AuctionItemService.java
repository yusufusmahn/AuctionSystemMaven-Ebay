package org.AuctionSystemEbay.services;

import org.AuctionSystemEbay.dtos.requests.AuctionItemRequest;
import org.AuctionSystemEbay.dtos.responses.AuctionItemResponse;

import java.util.List;

public interface AuctionItemService {
    AuctionItemResponse createAuctionItem(AuctionItemRequest auctionItemRequest);
    AuctionItemResponse getAuctionItemById(String itemId);
    List<AuctionItemResponse> getAuctionItemsBySellerId(String sellerId);
    List<AuctionItemResponse> getActiveAuctionItems();
    void closeExpiredAuctions();
}