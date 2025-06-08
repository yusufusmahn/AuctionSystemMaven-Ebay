package org.AuctionSystemEbay.services;

import org.AuctionSystemEbay.dtos.requests.BidRequest;
import org.AuctionSystemEbay.dtos.responses.BidResponse;

import java.util.List;

public interface BidService {
    BidResponse placeBid(BidRequest bidRequest);
    List<BidResponse> getBidsByAuctionItemId(String auctionItemId);
}