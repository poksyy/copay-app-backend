package com.copay.app.service.group;

import java.util.Map;

import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.group.request.CreateGroupRequestDTO;
import com.copay.app.dto.group.request.UpdateGroupRegisteredMembersRequestDTO;
import com.copay.app.dto.group.request.UpdateGroupExternalMembersRequestDTO;
import com.copay.app.dto.group.response.CreateGroupResponseDTO;
import com.copay.app.dto.group.response.GetGroupResponseDTO;

public interface GroupService {
	
	GetGroupResponseDTO getGroupsByUserId(Long userId);
	
	CreateGroupResponseDTO createGroup(CreateGroupRequestDTO request);

	MessageResponseDTO updateGroupRegisteredMembers(Long groupId, UpdateGroupRegisteredMembersRequestDTO request);

	MessageResponseDTO updateGroupExternalMembers(Long groupId, UpdateGroupExternalMembersRequestDTO request);

	MessageResponseDTO updateGroup(Long groupId, Map<String, Object> fields);

	MessageResponseDTO leaveGroup(Long groupId, String token);

	MessageResponseDTO deleteGroup(Long groupId, String token);
}
