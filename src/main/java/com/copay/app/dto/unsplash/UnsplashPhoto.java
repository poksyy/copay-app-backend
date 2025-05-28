package com.copay.app.dto.unsplash;

public class UnsplashPhoto {
    private String id;
    private String description;
    private UnsplashUrls urls;
    private UnsplashUser user;

    public UnsplashPhoto() {}

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

    public UnsplashUrls getUrls() {
        return urls;
    }

    public void setUrls(UnsplashUrls urls) {
        this.urls = urls;
    }

    public UnsplashUser getUser() {
        return user;
    }

    public void setUser(UnsplashUser user) {
        this.user = user;
    }
}