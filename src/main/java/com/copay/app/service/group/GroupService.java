package com.copay.app.service.group;

import java.util.Map;

import com.copay.app.dto.group.CreateGroupRequestDTO;
import com.copay.app.dto.group.UpdateGroupRegisteredMembersRequestDTO;
import com.copay.app.dto.group.UpdateGroupExternalMembersRequestDTO;
import com.copay.app.dto.responses.CreateGroupResponseDTO;
import com.copay.app.dto.responses.DeleteGroupResponseDTO;
import com.copay.app.dto.responses.GetGroupResponseDTO;
import com.copay.app.dto.responses.UpdateGroupResponseDTO;

public interface GroupService {
	
	CreateGroupResponseDTO createGroup(CreateGroupRequestDTO request);

	UpdateGroupResponseDTO updateGroupRegisteredMembers(Long groupId, UpdateGroupRegisteredMembersRequestDTO request);

	UpdateGroupResponseDTO updateGroupExternalMembers(Long groupId, UpdateGroupExternalMembersRequestDTO request);

	UpdateGroupResponseDTO updateGroup(Long groupId, Map<String, Object> fields);

	UpdateGroupResponseDTO leaveGroup(Long groupId, String token);

	DeleteGroupResponseDTO deleteGroup(Long groupId, String token);

	GetGroupResponseDTO getGroupsByUserId(Long userId);

}
