package com.copay.app.service.group;

import java.util.Map;

import com.copay.app.dto.group.request.CreateGroupRequestDTO;
import com.copay.app.dto.group.request.UpdateGroupRegisteredMembersRequestDTO;
import com.copay.app.dto.group.request.UpdateGroupExternalMembersRequestDTO;
import com.copay.app.dto.group.response.CreateGroupResponseDTO;
import com.copay.app.dto.group.response.GetGroupResponseDTO;
import com.copay.app.dto.group.response.GroupMessageResponseDTO;

public interface GroupService {
	
	GetGroupResponseDTO getGroupsByUserId(Long userId);
	
	CreateGroupResponseDTO createGroup(CreateGroupRequestDTO request);

	GroupMessageResponseDTO updateGroupRegisteredMembers(Long groupId, UpdateGroupRegisteredMembersRequestDTO request);

	GroupMessageResponseDTO updateGroupExternalMembers(Long groupId, UpdateGroupExternalMembersRequestDTO request);

	GroupMessageResponseDTO updateGroup(Long groupId, Map<String, Object> fields);

	GroupMessageResponseDTO leaveGroup(Long groupId, String token);

	GroupMessageResponseDTO deleteGroup(Long groupId, String token);
}
