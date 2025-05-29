package com.copay.app.dto.unsplash.response;

public class UnsplashResponseDTO {
    private int total;
    private int total_pages;
    private java.util.List<UnsplashPhotoResponseDTO> results;

    public UnsplashResponseDTO() {}

    // Getters and Setters
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public java.util.List<UnsplashPhotoResponseDTO> getResults() {
        return results;
    }

    public void setResults(java.util.List<UnsplashPhotoResponseDTO> results) {
        this.results = results;
    }
}