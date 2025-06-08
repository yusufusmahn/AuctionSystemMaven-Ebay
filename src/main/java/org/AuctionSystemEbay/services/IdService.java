package org.AuctionSystemEbay.services;

import org.AuctionSystemEbay.data.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class IdService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuctionItemRepository auctionItemRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private static final SecureRandom secureRandom = new SecureRandom();

    public String generateUniqueId() {
        String id;
        do {
            int code = secureRandom.nextInt(100000000);
            id = String.format("%06d", code);
        } while (userRepository.existsById(id) ||
                 auctionItemRepository.existsById(id) ||
                 bidRepository.existsById(id) ||
                 transactionRepository.existsById(id));
        return id;
    }
}