package com.copay.app.dto.unsplash.response;

public class UnsplashUserResponseDTO {
    private String name;
    private String username;

    public UnsplashUserResponseDTO() {}

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}