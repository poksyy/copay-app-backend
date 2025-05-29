package com.copay.app.service.photo;

import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.unsplash.response.UnsplashResponseDTO;
import com.copay.app.service.UnsplashService;
import com.copay.app.service.group.GroupService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PhotoServiceImpl implements PhotoService {

    private final UnsplashService unsplashService;
    private final GroupService groupService;

    public PhotoServiceImpl(UnsplashService unsplashService, GroupService groupService) {
        this.unsplashService = unsplashService;
        this.groupService = groupService;
    }

    @Override
    public UnsplashResponseDTO searchPhotos(String query, int page, int perPage) {
        return unsplashService.searchPhotos(query, page, perPage);
    }

    @Override
    public MessageResponseDTO updateGroupPhoto(Long groupId, String imageUrl, String imageProvider, String token) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("imageUrl", imageUrl);
        fields.put("imageProvider", imageProvider);

        return groupService.updateGroup(groupId, fields, token);
    }

    @Override
    public MessageResponseDTO removeGroupPhoto(Long groupId, String token) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("imageUrl", null);
        fields.put("imageProvider", null);

        return groupService.updateGroup(groupId, fields, token);
    }
}