package org.AuctionSystemEbay.data.repositories;

import org.AuctionSystemEbay.data.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
    User findByUsername(String username);
//  Optional<User> findByEmail(String email);

}