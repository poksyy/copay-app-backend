package com.copay.app.service.photo;

import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.unsplash.response.UnsplashResponseDTO;

public interface PhotoService {

    /**
     * Search for photos using Unsplash API
     * 
     * @param query The search query
     * @param page The page number
     * @param perPage Number of photos per page
     * @return UnsplashResponseDTO containing search results
     */
    UnsplashResponseDTO searchPhotos(String query, int page, int perPage);
    
    /**
     * Set a photo for a group
     * 
     * @param groupId The ID of the group
     * @param imageUrl The URL of the image
     * @param imageProvider The provider of the image
     * @param token Authorization token
     * @return MessageResponseDTO with the result of the operation
     */
    MessageResponseDTO updateGroupPhoto(Long groupId, String imageUrl, String imageProvider, String token);
    
    /**
     * Remove a photo from a group
     * 
     * @param groupId The ID of the group
     * @param token Authorization token
     * @return MessageResponseDTO with the result of the operation
     */
    MessageResponseDTO removeGroupPhoto(Long groupId, String token);
}