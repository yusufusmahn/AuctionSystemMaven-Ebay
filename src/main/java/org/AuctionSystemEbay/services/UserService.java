package org.AuctionSystemEbay.services;

import org.AuctionSystemEbay.dtos.requests.*;
import org.AuctionSystemEbay.dtos.responses.*;

import java.util.Optional;

public interface UserService {
    UserResponse createUser(UserRequest userRequest);
    UserResponse getUserById(String userId);
    UserResponse getUserByEmail(String email);
    LoginResponse login(LoginRequest loginRequest);
    UserResponse updateUserRole(String userId, UpdateRoleRequest request);
}