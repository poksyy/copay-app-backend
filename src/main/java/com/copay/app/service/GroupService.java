package com.copay.app.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.copay.app.dto.group.CreateGroupRequestDTO;
import com.copay.app.dto.group.UpdateGroupCopayMembersRequestDTO;
import com.copay.app.dto.group.UpdateGroupDescriptionRequestDTO;
import com.copay.app.dto.group.UpdateGroupEstimatedPriceRequestDTO;
import com.copay.app.dto.group.UpdateGroupExternalMembersRequestDTO;
import com.copay.app.dto.group.UpdateGroupNameRequestDTO;
import com.copay.app.dto.group.auxiliary.CopayMemberDTO;
import com.copay.app.dto.group.auxiliary.ExternalMemberDTO;
import com.copay.app.dto.group.auxiliary.GroupOwnerDTO;
import com.copay.app.dto.responses.CreateGroupResponseDTO;
import com.copay.app.dto.responses.DeleteGroupResponseDTO;
import com.copay.app.dto.responses.GetGroupResponseDTO;
import com.copay.app.dto.responses.UpdateGroupResponseDTO;
import com.copay.app.entity.Group;
import com.copay.app.entity.User;
import com.copay.app.entity.relations.ExternalMember;
import com.copay.app.entity.relations.GroupMember;
import com.copay.app.entity.relations.GroupMemberId;
import com.copay.app.exception.GroupNotFoundException;
import com.copay.app.exception.InvalidGroupCreatorException;
import com.copay.app.exception.InvitedMemberNotFoundException;
import com.copay.app.exception.UserNotFoundException;
import com.copay.app.repository.ExternalMemberRepository;
import com.copay.app.repository.GroupMemberRepository;
import com.copay.app.repository.GroupRepository;
import com.copay.app.repository.UserRepository;
import java.util.Objects;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class GroupService {

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private GroupMemberRepository groupMemberRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ExternalMemberRepository externalMemberRepository;

	@Autowired
	private JwtService jwtService;

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public CreateGroupResponseDTO createGroup(CreateGroupRequestDTO request) {

		// Check if the creator exists on the database and saves the user_id.
		User creator = userRepository.findById(request.getCreatedBy())
				.orElseThrow(() -> new UserNotFoundException("User with ID " + request.getCreatedBy() + " not found"));

		// Loop to check if the invited Copay members have an account.
		for (String phoneNumber : request.getInvitedCopayMembers()) {
			userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new InvitedMemberNotFoundException(
					"This phone number owner doesn't have an account: " + phoneNumber));
		}

		// Creates a group instance.
		Group group = new Group();

		// This field will store the reference to the User entity, JPA will handle the
		// user_id automatically.
		group.setCreatedBy(creator);
		group.setName(request.getGroupName());
		group.setDescription(request.getDescription());
		group.setEstimatedPrice(request.getEstimatedPrice());
		group.setCurrency(request.getCurrency());

		// Persists the group details in the database.
		group = groupRepository.save(group);

		// Create the composite key for the group-member relationship with the group and
		// user.
		GroupMemberId creatorMemberId = new GroupMemberId(group, creator);
		// Instantiate the GroupMember entity using the composite key.
		GroupMember creatorGroupMember = new GroupMember(creatorMemberId);

		// Persist the new group member to the repository, saving the relationship.
		groupMemberRepository.save(creatorGroupMember);

		// Excludes the creator of the group from the invitedMembers list.
		List<String> invitedMembers = request.getInvitedCopayMembers().stream()
				.filter(phone -> !phone.equals(creator.getPhoneNumber())).collect(Collectors.toList());

		// Loop to persist invited copay members into the database.
		for (String phoneNumber : invitedMembers) {

			// Find the users by the phone number.
			User invitedCopayMember = userRepository.findByPhoneNumber(phoneNumber).get();

			GroupMemberId invitedMemberId = new GroupMemberId(group, invitedCopayMember);

			// Validates if the user already belongs to the group.
			if (groupMemberRepository.existsById(invitedMemberId)) {
				// Skip the user if they are already a member of the group.
				continue;
			}

			// If the user is not already a member, create and add them to the group.
			GroupMember invitedGroupMember = new GroupMember(invitedMemberId);

			// Add the user to the group
			group.getGroupMembers().add(invitedGroupMember);
		}

		// Loop to interact with the List of the externalMembers.
		for (String externalName : request.getInvitedExternalMembers()) {

			// Create and persist an ExternalMember.
			ExternalMember externalMember = new ExternalMember();
			externalMember.setName(externalName);
			externalMember.setGroup(group);
			externalMember.setJoinedAt(LocalDateTime.now());

			// Add the external member to the group
			group.getExternalMembers().add(externalMember);
		}

		/*
		 * Explicitly merge the 'group' entity to flush changes and synchronize them
		 * with the database. This is necessary because we avoid using
		 * 'groupMemberRepository.save()' and 'externalMemberRepository.save()' in favor
		 * of adding members directly to the group using the 'add()' method, which
		 * relies on cascading.
		 */
		entityManager.merge(group);

		// Map the group instance to GroupResponseDTO.
		return mapToGroupResponseDTO(group);
	}

	// Helper method to map Group entity to GroupResponseDTO.
	private CreateGroupResponseDTO mapToGroupResponseDTO(Group group) {

		// Initialize an instance of the DTO that is going to be used as a response.
		CreateGroupResponseDTO groupResponseDTO = new CreateGroupResponseDTO();

		// Set group details
		groupResponseDTO.setGroupId(group.getGroupId());
		groupResponseDTO.setGroupName(group.getName());
		groupResponseDTO.setDescription(group.getDescription());
		groupResponseDTO.setEstimatedPrice(group.getEstimatedPrice());
		groupResponseDTO.setCurrency(group.getCurrency());
		groupResponseDTO.setCreatedAt(group.getCreatedAt());

		// Map group owner details (GroupOwnerDTO-> includes id and username).
		GroupOwnerDTO groupOwnerDTO = new GroupOwnerDTO();

		// Accessing Group and User data through their established relationship.
		groupOwnerDTO.setOwnerId(group.getCreatedBy().getUserId());
		groupOwnerDTO.setOwnerName(group.getCreatedBy().getUsername());
		groupResponseDTO.setGroupOwner(groupOwnerDTO);

		// Map registered members (CopayMemberDTO -> includes id and phoneNumber).
		if (group.getGroupMembers() != null) {

			// Convert each group member to CopayMemberDTO.
			List<CopayMemberDTO> groupMemberResponseDTOList = group.getGroupMembers().stream().map(gm -> {
				User user = gm.getId().getUser();
				return new CopayMemberDTO(user.getUserId(), user.getPhoneNumber());
			}).collect(Collectors.toList());

			// Set the mapped list of CopayMemberDTOs in the response DTO.
			groupResponseDTO.setCopayMembers(groupMemberResponseDTOList);
		}

		// Map external members (ExternalMemberDTO -> includes externalMembersId and
		// name).
		if (group.getExternalMembers() != null) {

			// Convert each external member to ExternalMemberDTO.
			List<ExternalMemberDTO> externalList = group.getExternalMembers().stream()
					.map(externalMember -> new ExternalMemberDTO(externalMember.getExternalMembersId(),
							externalMember.getName()))
					.collect(Collectors.toList());

			// Set the mapped list of ExternalMemberDTOs in the response DTO.
			groupResponseDTO.setExternalMembers(externalList);
		}

		return groupResponseDTO;
	}

	// readOnly since it's a GET request.
	@Transactional(readOnly = true)
	public GetGroupResponseDTO getGroupsByUserId(Long userId) {

		// Fetch groups where the user is a member.
		List<GroupMember> groupMembers = groupMemberRepository.findByIdUserUserId(userId);

		// Transform GroupMembers into unique groups and map data for response.
		List<Group> groups = groupMembers.stream().map(gm -> gm.getId().getGroup()).distinct()
				.collect(Collectors.toList());

		// Map groups to response DTO format, including external members.
		List<CreateGroupResponseDTO> createGroupResponseDTO = groups.stream().map(this::mapToGroupResponseDTO)
				.collect(Collectors.toList());

		GetGroupResponseDTO getGroupResponseDTO = new GetGroupResponseDTO();

		// Set the mapped groups list in the response DTO.
		getGroupResponseDTO.setGroups(createGroupResponseDTO);

		return getGroupResponseDTO;
	}

	public DeleteGroupResponseDTO deleteGroup(Long groupId, String token) {

		// Find the group by ID or throw exception if not found.
		Group group = findGroupOrThrow(groupId);
		
		// Get the email from the current token.
		String userPhoneNumber = jwtService.getUserIdentifierFromToken(token);

		// Find user by email through the temporal token.
		User user = userRepository.findByPhoneNumber(userPhoneNumber)
				.orElseThrow(() -> new UserNotFoundException("User not found"));

		// Validate that the deletion is being done by the group creator.
		if (!group.getCreatedBy().getUserId().equals(user.getUserId())) {
			throw new InvalidGroupCreatorException(
					"User " + user.getUserId() + " has no permissions to delete group " + group.getGroupId());
		}

		// Persists deletion in the database.
		groupRepository.delete(group);

		return new DeleteGroupResponseDTO("Group " + group.getName() + " deleted successfully.");
	}

	@Transactional
	public UpdateGroupResponseDTO updateGroupName(Long groupId, UpdateGroupNameRequestDTO request) {

		// Find the group by ID or throw exception if not found.
		Group group = findGroupOrThrow(groupId);
		
		// Update the group's name.
		group.setName(request.getName());

		// Save the updated group.
		groupRepository.save(group);

		// Return success message.
		return new UpdateGroupResponseDTO("Group name updated successfully.");
	}

	@Transactional
	public UpdateGroupResponseDTO updateGroupDescription(Long groupId, UpdateGroupDescriptionRequestDTO request) {

		// Find the group by ID or throw exception if not found.
		Group group = findGroupOrThrow(groupId);
		
		// Update the group's description.
		group.setDescription(request.getDescription());

		// Save the updated group.
		groupRepository.save(group);

		// Return success message.
		return new UpdateGroupResponseDTO("Group description updated successfully.");
	}

	@Transactional
	public UpdateGroupResponseDTO updateGroupEstimatedPrice(Long groupId, UpdateGroupEstimatedPriceRequestDTO request) {

		// Find the group by ID or throw exception if not found.
		Group group = findGroupOrThrow(groupId);
		// Update the group's estimated price.
		group.setEstimatedPrice(request.getEstimatedPrice());

		// Save the updated group.
		groupRepository.save(group);

		// Return success message.
		return new UpdateGroupResponseDTO("Group estimated price updated successfully.");
	}

	@Transactional
	public UpdateGroupResponseDTO updateGroupCopayMembers(Long groupId, UpdateGroupCopayMembersRequestDTO request) {

		// Find the group or throw an exception if not found.
		Group group = findGroupOrThrow(groupId);

		// Collect the invited phone numbers from the request.
		Set<String> invitedPhoneNumbers = new HashSet<>(request.getInvitedCopayMembers());

		// Remove members who are no longer invited.
		removeUninvitedCopayMembers(group, invitedPhoneNumbers);

		// Add newly invited members who are not already in the group.
		addNewCopayMembers(group, invitedPhoneNumbers);

		// Return success message.
		return new UpdateGroupResponseDTO("Group Copay members updated successfully.");
	}

	@Transactional
	public UpdateGroupResponseDTO updateGroupExternalMembers(Long groupId,
			UpdateGroupExternalMembersRequestDTO request) {

		// Find the group by ID or throw exception if not found.
		Group group = findGroupOrThrow(groupId);

		// Extract the set of external member IDs from the request.
		Set<Long> newIds = extractExternalMemberIds(request);

		// Remove members from the group that are not in the new list.
		removeDeletedExternalMembers(group, newIds);

		// Iterate through the incoming member DTOs.
		for (ExternalMemberDTO dto : request.getInvitedExternalMembers()) {

			// Get existing member or create a new one.
			ExternalMember member = resolveOrCreateExternalMember(dto, group);

			// Update the member's name.
			member.setName(dto.getName());

			// Add member to the group if it's not already present.
			addMemberToGroupIfMissing(group, member);
		}

		// Return success message.
		return new UpdateGroupResponseDTO("Group external members updated successfully.");
	}

	private void removeUninvitedCopayMembers(Group group, Set<String> invitedPhones) {
		
		// Remove current members whose phone numbers are not in the invited list.
		group.getGroupMembers().removeIf(member -> !invitedPhones.contains(member.getId().getUser().getPhoneNumber()));
	}

	private void addNewCopayMembers(Group group, Set<String> invitedPhones) {
		
		// Get phone numbers of the current group members.
		Set<String> currentPhones = group.getGroupMembers().stream().map(m -> m.getId().getUser().getPhoneNumber())
				.collect(Collectors.toSet());

		// Iterate through invited phone numbers.
		for (String phone : invitedPhones) {

			// Skip if the member already exists in the group.
			if (currentPhones.contains(phone)) {
				continue;
			}

			// Find the user by phone number or throw exception if not found.
			User user = userRepository.findByPhoneNumber(phone).orElseThrow(
					() -> new UserNotFoundException("This phone number doesn't have an account: " + phone));

			// Create and add a new group member to the group.
			GroupMemberId id = new GroupMemberId(group, user);
			group.getGroupMembers().add(new GroupMember(id));
		}
	}

	// Find group by ID or throw if not found.
	private Group findGroupOrThrow(Long groupId) {

		return groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("Group not found"));
	}

	// Extract IDs of invited external members from the request.
	private Set<Long> extractExternalMemberIds(UpdateGroupExternalMembersRequestDTO request) {

		return request.getInvitedExternalMembers().stream().map(ExternalMemberDTO::getExternalMembersId)
				.filter(Objects::nonNull).collect(Collectors.toSet());
	}

	// Remove external members that are not in the new ID list.
	private void removeDeletedExternalMembers(Group group, Set<Long> newIds) {

		group.getExternalMembers().removeIf(member -> !newIds.contains(member.getExternalMembersId()));
	}

	// Return existing external member or create a new one.
	private ExternalMember resolveOrCreateExternalMember(ExternalMemberDTO dto, Group group) {

		// TODO: Add custom validation.
		// Validates if the provided externalMemberID exists in the database.
		if (dto.getExternalMembersId() != null) {
			return externalMemberRepository.findById(dto.getExternalMembersId()).orElseThrow(
					() -> new RuntimeException("External member with ID " + dto.getExternalMembersId() + " not found"));
		}

		ExternalMember newMember = new ExternalMember();
		newMember.setGroup(group);

		return newMember;
	}

	// Ensure the addition of the external member to the group.
	private void addMemberToGroupIfMissing(Group group, ExternalMember member) {

		if (!group.getExternalMembers().contains(member)) {

			group.getExternalMembers().add(member);
		}
	}
}