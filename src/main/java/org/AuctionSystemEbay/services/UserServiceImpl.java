package org.AuctionSystemEbay.services;

import org.AuctionSystemEbay.data.models.*;
import org.AuctionSystemEbay.data.repositories.*;
import org.AuctionSystemEbay.dtos.requests.*;
import org.AuctionSystemEbay.dtos.responses.*;
import org.AuctionSystemEbay.exceptions.*;
import org.AuctionSystemEbay.utils.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IdService idService;



    @Override
    public UserResponse createUser(UserRequest userRequest) {
        if (userRequest == null || userRequest.getUsername() == null || userRequest.getEmail() == null ||
                userRequest.getPassword() == null || userRequest.getRole() == null) {
            throw new IllegalArgumentException("All user fields must be non-null");
        }

        String email = Mapper.convertToLowerCase(userRequest.getEmail());
        if (userRepository.findByEmail(email) != null) {
            throw new DuplicateUserException("Email already exists: " + email);
        }

        if (!"SELLER".equalsIgnoreCase(userRequest.getRole()) && !"BUYER".equalsIgnoreCase(userRequest.getRole())) {
            throw new UserRoleException("Role must be either 'SELLER' or 'BUYER'");
        }

        User user = Mapper.toUser(userRequest);
        user.setUserId(idService.generateUniqueId());
//        String hashedPassword = passwordEncoder.encode(userRequest.getPassword());
//        user.setPassword(hashedPassword);
        String hashedPassword = userRequest.getPassword();
        user.setPassword(hashedPassword);

        User savedUser = userRepository.save(user);
        return Mapper.toUserResponse(savedUser);
    }

    @Override
    public UserResponse updateUserRole(String userId, UpdateRoleRequest request) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (request == null || request.getNewRole() == null || request.getNewRole().trim().isEmpty()) {
            throw new IllegalArgumentException("New role cannot be null or empty");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        String newRole = request.getNewRole().toUpperCase();
        if (newRole.equals(user.getRole())) {
            throw new IllegalArgumentException("User already has the role: " + newRole);
        }

        if (!("BUYER".equals(newRole) || "SELLER".equals(newRole))) {
            throw new IllegalArgumentException("Role must be either BUYER or SELLER");
        }

        user.setRole(newRole);
        User updatedUser = userRepository.save(user);

        return Mapper.toUserResponse(updatedUser);
    }


    @Override
    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        return Mapper.toUserResponse(user);
    }


    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(Mapper.convertToLowerCase(email));
        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + email);
        }
        return Mapper.toUserResponse(user);
    }


    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        String transformedEmail = Mapper.convertToLowerCase(loginRequest.getEmail());
        User user = userRepository.findByEmail(transformedEmail);
        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + transformedEmail);
        }

        if (user.getPassword() == null || user.getPassword().trim().length() < 6) {
            throw new InvalidCredentialsException("Password must be at least 6 characters");
        }

        if (!user.verifyPassword(loginRequest.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        return Mapper.toLoginResponse(user, "Login successful");
    }

}
