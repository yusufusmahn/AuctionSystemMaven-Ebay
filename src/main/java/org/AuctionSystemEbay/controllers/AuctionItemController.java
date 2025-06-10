package org.AuctionSystemEbay.controllers;

import org.AuctionSystemEbay.dtos.requests.*;
import org.AuctionSystemEbay.dtos.responses.*;
import org.AuctionSystemEbay.exceptions.AuctionSystemException;
import org.AuctionSystemEbay.services.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/api/auction-items")
public class AuctionItemController {

    @Autowired
    private AuctionItemService auctionItemService;

    @PostMapping
    public ResponseEntity<?> createAuctionItem(@RequestBody AuctionItemRequest auctionItemRequest) {
        try {
            AuctionItemResponse response = auctionItemService.createAuctionItem(auctionItemRequest);
            return new ResponseEntity<>(new ApiResponse(response, true), HttpStatus.CREATED);
        } catch (AuctionSystemException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/{itemId}")
    public ResponseEntity<?> getAuctionItemById(@PathVariable("itemId") String itemId) {
        try {
            AuctionItemResponse response = auctionItemService.getAuctionItemById(itemId);
            return new ResponseEntity<>(new ApiResponse(response, true), HttpStatus.OK);
        } catch (AuctionSystemException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<?> getAuctionItemsBySellerId(@PathVariable("sellerId") String sellerId) {
        try {
            List<AuctionItemResponse> responses = auctionItemService.getAuctionItemsBySellerId(sellerId);
            return new ResponseEntity<>(new ApiResponse(responses, true), HttpStatus.OK);
        } catch (AuctionSystemException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/active")
    public ResponseEntity<?> getActiveAuctionItems() {
        try {
            List<AuctionItemResponse> responses = auctionItemService.getActiveAuctionItems();
            return new ResponseEntity<>(new ApiResponse(responses, true), HttpStatus.OK);
        } catch (AuctionSystemException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping("/close-expired")
    public ResponseEntity<?> closeExpiredAuctions() {
        try {
            auctionItemService.closeExpiredAuctions();
            return new ResponseEntity<>(new ApiResponse("Expired auctions closed successfully", true), HttpStatus.OK);
        } catch (AuctionSystemException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
    }
}