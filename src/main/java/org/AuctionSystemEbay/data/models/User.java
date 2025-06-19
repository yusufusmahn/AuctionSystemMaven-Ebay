package org.AuctionSystemEbay.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String userId;
    private String username;
    private String email;
    private String password;
    private String role;

    public boolean verifyPassword(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }

}