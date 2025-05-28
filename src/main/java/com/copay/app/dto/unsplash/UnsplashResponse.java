package com.copay.app.dto.unsplash;

public class UnsplashResponse {
    private int total;
    private int total_pages;
    private java.util.List<UnsplashPhoto> results;

    public UnsplashResponse() {}

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

    public java.util.List<UnsplashPhoto> getResults() {
        return results;
    }

    public void setResults(java.util.List<UnsplashPhoto> results) {
        this.results = results;
    }
}