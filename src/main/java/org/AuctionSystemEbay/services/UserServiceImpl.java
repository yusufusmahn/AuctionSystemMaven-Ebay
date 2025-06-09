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
        User savedUser = userRepository.save(user);
        return Mapper.toUserResponse(savedUser);
    }


}