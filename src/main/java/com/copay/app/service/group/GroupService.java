package com.copay.app.service.group;

import java.util.Map;

import com.copay.app.dto.group.request.CreateGroupRequestDTO;
import com.copay.app.dto.group.request.UpdateGroupRegisteredMembersRequestDTO;
import com.copay.app.dto.group.request.UpdateGroupExternalMembersRequestDTO;
import com.copay.app.dto.group.response.CreateGroupResponseDTO;
import com.copay.app.dto.group.response.DeleteGroupResponseDTO;
import com.copay.app.dto.group.response.GetGroupResponseDTO;
import com.copay.app.dto.group.response.UpdateGroupResponseDTO;

public interface GroupService {
	
	CreateGroupResponseDTO createGroup(CreateGroupRequestDTO request);

	UpdateGroupResponseDTO updateGroupRegisteredMembers(Long groupId, UpdateGroupRegisteredMembersRequestDTO request);

	UpdateGroupResponseDTO updateGroupExternalMembers(Long groupId, UpdateGroupExternalMembersRequestDTO request);

	UpdateGroupResponseDTO updateGroup(Long groupId, Map<String, Object> fields);

	UpdateGroupResponseDTO leaveGroup(Long groupId, String token);

	DeleteGroupResponseDTO deleteGroup(Long groupId, String token);

	GetGroupResponseDTO getGroupsByUserId(Long userId);

}
