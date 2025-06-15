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
    public void setUp() {
        userRepository.deleteAll();

        User testUser = new User();
        testUser.setUserId("123456");
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@gmail.com");
        testUser.setPassword("password");
        testUser.setRole("BUYER");
        userRepository.save(testUser);
    }

    @Test
    public void createUser_Success() {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("testuser2");
        userRequest.setEmail("testuser2@gmail.com");
        userRequest.setPassword("password");
        userRequest.setRole("SELLER");

        UserResponse response = userService.createUser(userRequest);
        assertNotNull(response);
        assertNotNull(response.getUserId());
        assertEquals("Testuser2", response.getUsername());
        assertEquals("testuser2@gmail.com", response.getEmail());
    }

    @Test
    public void createUser_DuplicateEmail_ThrowsException() {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("testuser");
        userRequest.setEmail("testuser@gmail.com");
        userRequest.setPassword("password");
        userRequest.setRole("BUYER");

        assertThrows(DuplicateUserException.class, () -> userService.createUser(userRequest));
    }

    @Test
    public void login_Success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("testuser@gmail.com");
        loginRequest.setPassword("password");

        LoginResponse response = userService.login(loginRequest);

        assertNotNull(response);
        assertNotNull(response.getUserId());
        assertEquals("Testuser", response.getUsername());
        assertEquals("buyer", response.getRole());
        assertEquals("Login successful", response.getMessage());
    }

    @Test
    public void login_InvalidPassword_ReturnsErrorMessage() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("testuser@gmail.com");
        loginRequest.setPassword("wrongpassword");

        assertThrows(InvalidCredentialsException.class, () -> userService.login(loginRequest));
    }

    @Test
    public void login_UserNotFound_ThrowsUserNotFoundException() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@gmail.com");
        loginRequest.setPassword("password");

        assertThrows(UserNotFoundException.class, () -> userService.login(loginRequest));
    }

    @Test
    public void getUserById_Success() {
        UserResponse response = userService.getUserById("123456");
        assertNotNull(response);
        assertEquals("Testuser", response.getUsername());
    }

    @Test
    public void getUserById_NotFound_ThrowsException() {
        assertThrows(UserNotFoundException.class, () -> userService.getUserById("nonexistent"));
    }

    @Test
    public void getUserByEmail_Success() {
        UserResponse response = userService.getUserByEmail("testuser@gmail.com");
        assertNotNull(response);
        assertEquals("Testuser", response.getUsername());
    }

    @Test
    public void getUserByEmail_NotFound_ThrowsException() {
        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail("nonexistent@email.com"));
    }

    @Test
    public void updateUserRole_Success() {
        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setNewRole("SELLER");

        UserResponse response = userService.updateUserRole("123456", request);
        assertNotNull(response);
        assertEquals("seller", response.getRole());
        assertEquals("Testuser", response.getUsername());
    }

    @Test
    public void updateUserRole_UserNotFound_ThrowsException() {
        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setNewRole("SELLER");

        assertThrows(UserNotFoundException.class, () -> userService.updateUserRole("nonexistent", request));
    }

    @Test
    public void updateUserRole_InvalidRole_ThrowsException() {
        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setNewRole("ADMIN");

        assertThrows(IllegalArgumentException.class, () -> userService.updateUserRole("123456", request));
    }

    @Test
    public void updateUserRole_SameRole_ThrowsException() {
        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setNewRole("BUYER");

        assertThrows(IllegalArgumentException.class, () -> userService.updateUserRole("123456", request));
    }
}