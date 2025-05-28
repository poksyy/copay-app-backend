package com.copay.app.service;

import com.copay.app.dto.unsplash.UnsplashResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

@Service
public class UnsplashService {

    @Value("${unsplash.access.key}")
    private String accessKey;

    @Value("${unsplash.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public UnsplashService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UnsplashResponse searchPhotos(String query, int page, int perPage) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Client-ID " + accessKey);
        headers.set("Accept-Version", "v1");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = String.format("%s/search/photos?query=%s&page=%d&per_page=%d",
                apiUrl, query, page, perPage);

        try {
            ResponseEntity<UnsplashResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, UnsplashResponse.class);
            return response.getBody();
        } catch (Exception e) {
            // Log del error
            throw new RuntimeException("Error finding photos in Unsplash", e);
        }
    }
}
