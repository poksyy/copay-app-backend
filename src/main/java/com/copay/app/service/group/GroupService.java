package com.copay.app.service.group;

import java.util.Map;

import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.group.request.CreateGroupRequestDTO;
import com.copay.app.dto.group.request.UpdateGroupEstimatedPriceRequestDTO;
import com.copay.app.dto.group.request.UpdateGroupRegisteredMembersRequestDTO;
import com.copay.app.dto.group.request.UpdateGroupExternalMembersRequestDTO;
import com.copay.app.dto.group.response.GetGroupMembersResponseDTO;
import com.copay.app.dto.group.response.GetGroupResponseDTO;
import com.copay.app.dto.group.response.GroupResponseDTO;

public interface GroupService {

	GetGroupResponseDTO getAllGroupsByUserId(Long userId, String token);

	GroupResponseDTO getGroupByGroupId(Long groupId, String token);

	GetGroupMembersResponseDTO getGroupMembersByGroupId(Long groupId, String token);

	GroupResponseDTO createGroup(CreateGroupRequestDTO request, String token);

	MessageResponseDTO updateGroup(Long groupId, Map<String, Object> fields, String token);

	MessageResponseDTO updateGroupEstimatedPrice(Long groupId, UpdateGroupEstimatedPriceRequestDTO request, String token);

	MessageResponseDTO updateGroupRegisteredMembers(Long groupId, UpdateGroupRegisteredMembersRequestDTO request, String token);

	MessageResponseDTO updateGroupExternalMembers(Long groupId, UpdateGroupExternalMembersRequestDTO request, String token);

	MessageResponseDTO leaveGroup(Long groupId, String token);

	MessageResponseDTO deleteGroup(Long groupId, String token);

}
