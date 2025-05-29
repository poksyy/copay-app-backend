package com.copay.app.controller;

import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.unsplash.request.PhotoRequestDTO;
import com.copay.app.dto.unsplash.response.UnsplashResponseDTO;
import com.copay.app.service.photo.PhotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    final private PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping("/search")
    public ResponseEntity<UnsplashResponseDTO> searchPhotos(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int perPage) {

        UnsplashResponseDTO response = photoService.searchPhotos(query, page, perPage);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/group/{groupId}")
    public ResponseEntity<MessageResponseDTO> setGroupPhoto(
            @PathVariable Long groupId,
            @RequestBody PhotoRequestDTO photoRequest) {

        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        MessageResponseDTO response = photoService.updateGroupPhoto(
                groupId,
                photoRequest.getImageUrl(),
                photoRequest.getImageProvider(),
                token);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/group/{groupId}")
    public ResponseEntity<MessageResponseDTO> removeGroupPhoto(
            @PathVariable Long groupId) {

        // Get the token from the SecurityContextHolder.
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        MessageResponseDTO response = photoService.removeGroupPhoto(groupId, token);
        return ResponseEntity.ok(response);
    }
}