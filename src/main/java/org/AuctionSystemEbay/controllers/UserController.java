package org.AuctionSystemEbay.controllers;

import org.AuctionSystemEbay.dtos.requests.*;
import org.AuctionSystemEbay.dtos.responses.*;
import org.AuctionSystemEbay.exceptions.AuctionSystemException;
import org.AuctionSystemEbay.services.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserRequest userRequest) {
        try {
            UserResponse response = userService.createUser(userRequest);
            return new ResponseEntity<>(new ApiResponse(response, true), HttpStatus.CREATED);
        } catch (AuctionSystemException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable("userId") String userId, @RequestBody UpdateRoleRequest request) {
        try {
            UserResponse response = userService.updateUserRole(userId, request);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (AuctionSystemException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(),false), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = userService.login(loginRequest);
            return new ResponseEntity<>(new ApiResponse(response, true), HttpStatus.OK);
        } catch (AuctionSystemException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable("userId") String userId) {
        try {
            UserResponse response = userService.getUserById(userId);
            return new ResponseEntity<>(new ApiResponse(response, true), HttpStatus.OK);
        } catch (AuctionSystemException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable("email") String email) {
        try {
            UserResponse response = userService.getUserByEmail(email);
            return new ResponseEntity<>(new ApiResponse(response, true), HttpStatus.OK);
        } catch (AuctionSystemException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
    }
}