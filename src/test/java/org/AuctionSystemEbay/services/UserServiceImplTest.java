package org.AuctionSystemEbay.services;

import org.AuctionSystemEbay.data.models.*;
import org.AuctionSystemEbay.data.repositories.*;
import org.AuctionSystemEbay.dtos.requests.*;
import org.AuctionSystemEbay.dtos.responses.*;
import org.AuctionSystemEbay.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User testUser = new User();
        testUser.setUserId("123456");
        testUser.setUsername("John Doe");
        testUser.setEmail("john@email.com");
        testUser.setPassword("password123");
        testUser.setRole("BUYER");
        userRepository.save(testUser);
    }

    @Test
    void createUser_Success() {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("jane");
        userRequest.setEmail("jane@email.com");
        userRequest.setPassword("password");
        userRequest.setRole("SELLER");

        UserResponse response = userService.createUser(userRequest);
        assertNotNull(response);
        assertNotNull(response.getUserId());
        assertEquals("Jane", response.getUsername());
        assertEquals("jane@email.com", response.getEmail());
    }

    @Test
    void createUser_DuplicateEmail_ThrowsException() {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("john");
        userRequest.setEmail("john@email.com");
        userRequest.setPassword("password");
        userRequest.setRole("BUYER");

        assertThrows(DuplicateUserException.class, () -> userService.createUser(userRequest));
    }


    @Test
    void login_Success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("john@email.com");
        loginRequest.setPassword("password123");

        LoginResponse response = userService.login(loginRequest);

        assertNotNull(response);
        assertNotNull(response.getUserId());
        assertEquals("John Doe", response.getUsername());
        assertEquals("BUYER", response.getRole());
        assertEquals("Login successful", response.getMessage());
    }

    @Test
    void login_InvalidPassword_ReturnsErrorMessage() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("john@email.com");
        loginRequest.setPassword("wrongpassword");

        LoginResponse response = userService.login(loginRequest);

        assertNotNull(response);
        assertNull(response.getUserId());
        assertNull(response.getUsername());
        assertNull(response.getRole());
        assertEquals("Invalid password", response.getMessage());
    }

    @Test
    void login_UserNotFound_ThrowsUserNotFoundException() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@email.com");
        loginRequest.setPassword("password123");

        assertThrows(UserNotFoundException.class, () -> userService.login(loginRequest));
    }

    @Test
    void getUserById_Success() {
        UserResponse response = userService.getUserById("123456");
        assertNotNull(response);
        assertEquals("John Doe", response.getUsername());
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        assertThrows(UserNotFoundException.class, () -> userService.getUserById("nonexistent"));
    }

    @Test
    void getUserByEmail_Success() {
        UserResponse response = userService.getUserByEmail("john@email.com");
        assertNotNull(response);
        assertEquals("John Doe", response.getUsername());
    }

    @Test
    void getUserByEmail_NotFound_ThrowsException() {
        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail("nonexistent@email.com"));
    }

    @Test
    void updateUserRole_Success() {
        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setNewRole("SELLER");

        UserResponse response = userService.updateUserRole("123456", request);
        assertNotNull(response);
        assertEquals("SELLER", response.getRole().toUpperCase());
        assertEquals("John Doe", response.getUsername());
    }

    @Test
    void updateUserRole_UserNotFound_ThrowsException() {
        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setNewRole("SELLER");

        assertThrows(UserNotFoundException.class, () -> userService.updateUserRole("nonexistent", request));
    }

    @Test
    void updateUserRole_InvalidRole_ThrowsException() {
        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setNewRole("ADMIN");

        assertThrows(IllegalArgumentException.class, () -> userService.updateUserRole("123456", request));
    }

    @Test
    void updateUserRole_SameRole_ThrowsException() {
        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setNewRole("BUYER");

        assertThrows(IllegalArgumentException.class, () -> userService.updateUserRole("123456", request));
    }
}