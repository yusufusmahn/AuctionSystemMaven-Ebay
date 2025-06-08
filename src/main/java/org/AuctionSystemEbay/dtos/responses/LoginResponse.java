package org.AuctionSystemEbay.dtos.responses;

import lombok.Data;

@Data
public class LoginResponse {
    private String userId;
    private String username;
    private String role;
    private String message;
}