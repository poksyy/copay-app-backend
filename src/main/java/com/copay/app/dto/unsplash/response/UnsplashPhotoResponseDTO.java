package com.copay.app.dto.unsplash.response;

public class UnsplashPhotoResponseDTO {
    private String id;
    private String description;
    private UnsplashUrlsResponseDTO urls;
    private UnsplashUserResponseDTO user;

    public UnsplashPhotoResponseDTO() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UnsplashUrlsResponseDTO getUrls() {
        return urls;
    }

    public void setUrls(UnsplashUrlsResponseDTO urls) {
        this.urls = urls;
    }

    public UnsplashUserResponseDTO getUser() {
        return user;
    }

    public void setUser(UnsplashUserResponseDTO user) {
        this.user = user;
    }
}