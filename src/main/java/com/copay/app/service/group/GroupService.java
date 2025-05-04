package com.copay.app.service.group;

import java.util.Map;

import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.group.request.CreateGroupRequestDTO;
import com.copay.app.dto.group.request.UpdateGroupRegisteredMembersRequestDTO;
import com.copay.app.dto.group.request.UpdateGroupExternalMembersRequestDTO;
import com.copay.app.dto.group.response.GetGroupResponseDTO;
import com.copay.app.dto.group.response.GroupResponseDTO;

public interface GroupService {

	GetGroupResponseDTO getGroupsByUserId(Long userId);

	GroupResponseDTO createGroup(CreateGroupRequestDTO request);

	MessageResponseDTO updateGroup(Long groupId, Map<String, Object> fields, String token);

	MessageResponseDTO updateGroupRegisteredMembers(Long groupId, UpdateGroupRegisteredMembersRequestDTO request, String token);

	MessageResponseDTO updateGroupExternalMembers(Long groupId, UpdateGroupExternalMembersRequestDTO request, String token);

	MessageResponseDTO leaveGroup(Long groupId, String token);

	MessageResponseDTO deleteGroup(Long groupId, String token);

}
