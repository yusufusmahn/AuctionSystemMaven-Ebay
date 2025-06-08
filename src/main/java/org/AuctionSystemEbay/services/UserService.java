package org.AuctionSystemEbay.services;


import org.AuctionSystemEbay.dtos.requests.LoginRequest;
import org.AuctionSystemEbay.dtos.requests.UserRequest;
import org.AuctionSystemEbay.dtos.responses.LoginResponse;
import org.AuctionSystemEbay.dtos.responses.UserResponse;

public interface UserService {
    UserResponse createUser(UserRequest userRequest);
    UserResponse getUserById(String userId);
    UserResponse getUserByEmail(String email);
    LoginResponse login(LoginRequest loginRequest);
}