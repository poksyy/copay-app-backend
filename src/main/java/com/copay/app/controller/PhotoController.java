package com.copay.app.controller;

import com.copay.app.dto.unsplash.UnsplashResponse;
import com.copay.app.service.UnsplashService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    final private UnsplashService unsplashService;

    public PhotoController(UnsplashService unsplashService) {
        this.unsplashService = unsplashService;
    }

    @GetMapping("/search")
    public ResponseEntity<UnsplashResponse> searchPhotos(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int perPage) {

        try {
            UnsplashResponse response = unsplashService.searchPhotos(query, page, perPage);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}