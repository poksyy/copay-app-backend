package com.copay.app.dto.unsplash.request;

public class PhotoRequestDTO {
    private String imageUrl;
    private String imageProvider;

    public PhotoRequestDTO() {
    }

    public PhotoRequestDTO(String imageUrl, String imageProvider) {
        this.imageUrl = imageUrl;
        this.imageProvider = imageProvider;
    }

    // Getters and Setters
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageProvider() {
        return imageProvider;
    }

    public void setImageProvider(String imageProvider) {
        this.imageProvider = imageProvider;
    }
}