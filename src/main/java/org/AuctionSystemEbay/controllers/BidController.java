package org.AuctionSystemEbay.controllers;

import org.AuctionSystemEbay.dtos.requests.BidRequest;
import org.AuctionSystemEbay.dtos.responses.ApiResponse;
import org.AuctionSystemEbay.dtos.responses.BidResponse;
import org.AuctionSystemEbay.exceptions.AuctionSystemException;
import org.AuctionSystemEbay.services.BidService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/api/bids")
public class BidController {

    @Autowired
    private BidService bidService;

    @PostMapping
    public ResponseEntity<?> placeBid(@RequestBody BidRequest bidRequest) {
        try {
            BidResponse response = bidService.placeBid(bidRequest);
            return new ResponseEntity<>(new ApiResponse(response, true), HttpStatus.CREATED);
        } catch (AuctionSystemException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/auction/{auctionItemId}")
    public ResponseEntity<?> getBidsByAuctionItemId(@PathVariable("auctionItemId") String auctionItemId) {
        try {
            List<BidResponse> responses = bidService.getBidsByAuctionItemId(auctionItemId);
            return new ResponseEntity<>(new ApiResponse(responses, true), HttpStatus.OK);
        } catch (AuctionSystemException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/active")
    public ResponseEntity<?> getActiveBids() {
        try {
            List<BidResponse> responses = bidService.getActiveBids();
            return new ResponseEntity<>(new ApiResponse(responses, true), HttpStatus.OK);
        } catch (AuctionSystemException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}