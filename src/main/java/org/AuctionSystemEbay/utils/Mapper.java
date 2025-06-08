package org.AuctionSystemEbay.utils;


import org.AuctionSystemEbay.data.models.*;
import org.AuctionSystemEbay.dtos.requests.*;
import org.AuctionSystemEbay.dtos.responses.*;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Mapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String convertToLowerCase(String input) {
        if (input != null) {
            return input.toLowerCase();
        }
        return null;
    }

    public static String toSentenceCase(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        String[] words = input.trim().split("\\s+");
        String result = "";
        for (int i = 0; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                String word = words[i].toLowerCase();
                result += Character.toUpperCase(word.charAt(0)) + word.substring(1);
                if (i < words.length - 1) {
                    result += " ";
                }
            }
        }
        return result;
    }

    public static String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime != null) {
            return dateTime.format(DATE_TIME_FORMATTER);
        }
        return null;
    }

    public static UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setUsername(toSentenceCase(user.getUsername()));
        response.setEmail(convertToLowerCase(user.getEmail()));
        response.setRole(convertToLowerCase(user.getRole()));
        return response;
    }

    public static User toUser(UserRequest userRequest) {
        User user = new User();
        user.setUsername(toSentenceCase(userRequest.getUsername()));
        user.setEmail(convertToLowerCase(userRequest.getEmail()));
        user.setPassword(userRequest.getPassword());
        user.setRole(userRequest.getRole());
        return user;
    }

    public static LoginResponse toLoginResponse(User user, String message) {
        LoginResponse response = new LoginResponse();
        response.setUserId(user.getUserId());
        response.setUsername(toSentenceCase(user.getUsername()));
        response.setRole(convertToLowerCase(user.getRole()));
        response.setMessage(message);
        return response;
    }


}