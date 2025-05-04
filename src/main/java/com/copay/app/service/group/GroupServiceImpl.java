package com.copay.app.service.group;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.copay.app.dto.expense.response.CreditorResponseDTO;
import com.copay.app.dto.group.request.UpdateGroupEstimatedPriceRequestDTO;
import com.copay.app.dto.group.response.GetGroupMembersResponseDTO;
import com.copay.app.dto.group.response.GroupResponseDTO;
import com.copay.app.entity.Expense;
import com.copay.app.exception.group.ExternalMemberNotFoundException;
import com.copay.app.repository.expense.ExpenseRepository;
import com.copay.app.service.expense.ExpenseServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.group.request.CreateGroupRequestDTO;
import com.copay.app.dto.group.request.UpdateGroupRegisteredMembersRequestDTO;
import com.copay.app.dto.group.request.UpdateGroupExternalMembersRequestDTO;
import com.copay.app.dto.group.auxiliary.RegisteredMemberDTO;
import com.copay.app.dto.group.auxiliary.ExternalMemberDTO;
import com.copay.app.dto.group.auxiliary.GroupOwnerDTO;
import com.copay.app.dto.group.response.GetGroupResponseDTO;
import com.copay.app.entity.Group;
import com.copay.app.entity.User;
import com.copay.app.entity.relations.ExternalMember;
import com.copay.app.entity.relations.GroupMember;
import com.copay.app.entity.relations.GroupMemberId;
import com.copay.app.exception.group.GroupNotFoundException;
import com.copay.app.exception.group.InvalidGroupCreatorException;
import com.copay.app.exception.group.InvitedMemberNotFoundException;
import com.copay.app.exception.user.UserNotFoundException;
import com.copay.app.repository.ExternalMemberRepository;
import com.copay.app.repository.GroupMemberRepository;
import com.copay.app.repository.GroupRepository;
import com.copay.app.repository.UserRepository;
import com.copay.app.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

@Service
public class GroupServiceImpl implements GroupService {

	private final GroupRepository groupRepository;

	private final GroupMemberRepository groupMemberRepository;

	private final UserRepository userRepository;

	private final ExternalMemberRepository externalMemberRepository;

	private final JwtService jwtService;

	private final ExpenseServiceImpl expenseServiceImpl;

	private final ExpenseRepository expenseRepository;

	@PersistenceContext
	private EntityManager entityManager;

	// Constructor to initialize all the instances.
	public GroupServiceImpl(GroupRepository groupRepository, GroupMemberRepository groupMemberRepository,
			UserRepository userRepository, ExternalMemberRepository externalMemberRepository, JwtService jwtService, ExpenseServiceImpl expenseServiceImpl,
			EntityManager entityManager, ExpenseRepository expenseRepository) {

		this.groupRepository = groupRepository;
		this.groupMemberRepository = groupMemberRepository;
		this.userRepository = userRepository;
		this.externalMemberRepository = externalMemberRepository;
		this.jwtService = jwtService;
		this.expenseServiceImpl = expenseServiceImpl;
		this.entityManager = entityManager;
		this.expenseRepository = expenseRepository;
	}

	@Override
	@Transactional
	public GroupResponseDTO createGroup(CreateGroupRequestDTO request) {

		// Check if the creator exists in the database and save the user_id.
		User creator = userRepository.findById(request.getCreatedBy())
				.orElseThrow(() -> new UserNotFoundException("User with ID " + request.getCreatedBy() + " not found"));

		Long paidByUserId = request.getPaidByRegisteredMemberId();
		Long paidByExternalMemberId = request.getPaidByExternalMemberId();

		if ((paidByUserId != null && paidByExternalMemberId != null) || (paidByUserId == null && paidByExternalMemberId == null)) {
			throw new IllegalArgumentException("Only one payer (either registered user or external member) should be selected.");
		}

		// Loop to check if the invited registered members have an account.
		for (String phoneNumber : request.getInvitedRegisteredMembers()) {
			userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new InvitedMemberNotFoundException(
					"This phone number owner doesn't have an account: " + phoneNumber));
		}

		// Creates a group instance.
		Group group = new Group();

		// This field will store the reference to the User entity, JPA will handle the
		// user_id automatically.
		group.setCreatedBy(creator);
		group.setName(request.getName());
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
		List<String> invitedMembers = request.getInvitedRegisteredMembers().stream()
				.filter(phone -> !phone.equals(creator.getPhoneNumber())).toList();

		// Loop to persist invited registered members into the database.
		for (String phoneNumber : invitedMembers) {

			// Find the registered members by the phone number.
			User invitedRegisteredMember = userRepository.findByPhoneNumber(phoneNumber).get();

			GroupMemberId invitedMemberId = new GroupMemberId(group, invitedRegisteredMember);

			// Validates if the user already belongs to the group.
			if (groupMemberRepository.existsById(invitedMemberId)) {
				// Skip the user if they are already a member of the group.
				continue;
			}

			// If the user is not already a member, create and add them to the group.
			GroupMember invitedGroupMember = new GroupMember(invitedMemberId);

			// Add the user to the group
			group.getRegisteredMembers().add(invitedGroupMember);
		}

		// Loop to interact with the List of the externalMembers.
		for (String externalName : request.getInvitedExternalMembers()) {

			// Create and persist an ExternalMember.
			ExternalMember externalMember = new ExternalMember();
			externalMember.setName(externalName);
			externalMember.setGroupId(group);
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

		User paidByUser = null;
		ExternalMember paidByExternalMember = null;

		if (paidByUserId != null) {

			paidByUser = userRepository.findById(paidByUserId)
					.orElseThrow(() -> new UserNotFoundException("User with ID " + paidByUserId + " not found"));
		} else {
			paidByExternalMember = externalMemberRepository.findById(paidByExternalMemberId)
					.orElseThrow(() -> new ExternalMemberNotFoundException("External Member with ID " + paidByExternalMemberId + " not found"));
		}

		expenseServiceImpl.initializeExpenseFromGroup(group, group.getEstimatedPrice(), paidByUser, paidByExternalMember);

		// Map the group instance to GroupResponseDTO.
		return mapToGroupResponseDTO(group, request.getCreatedBy(), paidByUser, paidByExternalMember);
	}

	// Helper method to map Group entity to GroupResponseDTO.
	private GroupResponseDTO mapToGroupResponseDTO(Group group, Long userId, User paidByUser, ExternalMember paidByExternalMember) {

		// Initialize an instance of the DTO that is going to be used as a response.
		GroupResponseDTO groupResponseDTO = new GroupResponseDTO();

		// Set group details
		groupResponseDTO.setGroupId(group.getGroupId());
		groupResponseDTO.setName(group.getName());
		groupResponseDTO.setDescription(group.getDescription());
		groupResponseDTO.setEstimatedPrice(group.getEstimatedPrice());
		groupResponseDTO.setCurrency(group.getCurrency());
		groupResponseDTO.setCreatedAt(group.getCreatedAt());
		groupResponseDTO.setUserIsOwner(group.getCreatedBy().getUserId().equals(userId));

		// Map group owner details (GroupOwnerDTO-> includes id and username).
		GroupOwnerDTO groupOwnerDTO = new GroupOwnerDTO();

		// Accessing Group and User data through their established relationship.
		groupOwnerDTO.setOwnerId(group.getCreatedBy().getUserId());
		groupOwnerDTO.setOwnerName(group.getCreatedBy().getUsername());
		groupResponseDTO.setGroupOwner(groupOwnerDTO);

		CreditorResponseDTO creditorDTO = null;

		if (paidByUser != null) {

			creditorDTO = new CreditorResponseDTO(paidByUser.getUserId(), paidByUser.getUsername());
		} else if (paidByExternalMember != null) {

			creditorDTO = new CreditorResponseDTO(paidByExternalMember.getExternalMembersId(), paidByExternalMember.getName());
		}
		groupResponseDTO.setCreditor(creditorDTO);

		// Map registered members (RegisteredMemberDTO -> includes id and phoneNumber).
		if (group.getRegisteredMembers() != null) {

			// Convert each group member to RegisteredMemberDTO.
			List<RegisteredMemberDTO> groupMemberResponseDTOList = group.getRegisteredMembers().stream().map(gm -> {
				User user = gm.getId().getUser();
				return new RegisteredMemberDTO(user.getUserId(), user.getUsername(), user.getPhoneNumber());
			}).collect(Collectors.toList());


			// Set the mapped list of RegisteredMemberDTO in the response DTO.
			groupResponseDTO.setRegisteredMembers(groupMemberResponseDTOList);
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

	@Override
	@Transactional(readOnly = true)
	public GetGroupResponseDTO getGroupsByUserId(Long userId) {

		// Fetch groups where the user is a member.
		List<GroupMember> groupMembers = groupMemberRepository.findByIdUserUserId(userId);

		// Transform GroupMembers into unique groups and map data for response.
		List<Group> groups = groupMembers.stream().map(gm -> gm.getId().getGroup()).distinct()
				.toList();

		// Map groups to response DTO format
		List<GroupResponseDTO> groupResponseDTOs = groups.stream()
				.map(group -> {

					// Fetch all Expenses associated with the Group
					List<Expense> expenses = expenseRepository.findByGroupId_GroupId(group.getGroupId());

					// Initialize variables for paid user or external member
					User paidByUser = null;
					ExternalMember paidByExternalMember = null;

					if (!expenses.isEmpty()) {
						// Find the first expense (or process multiple expenses if necessary)
						Expense expense = expenses.getFirst();

						// Assign the paid user or external member from the expense
						paidByUser = expense.getPaidByUser();
						paidByExternalMember = expense.getPaidByExternalMember();
					}

					// Map to GroupResponseDTO with all required params
					return mapToGroupResponseDTO(group, userId, paidByUser, paidByExternalMember);
				})
				.collect(Collectors.toList());

		// Create the response DTO
		GetGroupResponseDTO getGroupResponseDTO = new GetGroupResponseDTO();

		// Set the mapped groups list in the response DTO.
		getGroupResponseDTO.setGroups(groupResponseDTOs);

		return getGroupResponseDTO;
	}

	@Override
	@Transactional(readOnly = true)
	public GetGroupMembersResponseDTO getGroupMembersByGroup(Long groupId) {

		// Find the group by ID or throw exception if not found.
		Group group = findGroupOrThrow(groupId);

		List<RegisteredMemberDTO> registeredMembers = group.getRegisteredMembers().stream()
				.map(registeredMember -> new RegisteredMemberDTO(registeredMember.getId().getUser().getUserId(), registeredMember.getId().getUser().getUsername(), registeredMember.getId().getUser().getPhoneNumber()))
				.toList();

		List<ExternalMemberDTO> externalMembers = group.getExternalMembers().stream()
				.map(externalMember -> new ExternalMemberDTO(externalMember.getExternalMembersId(), externalMember.getName()))
				.toList();

		return new GetGroupMembersResponseDTO(registeredMembers, externalMembers);
	}

	@Override
	@Transactional
	public MessageResponseDTO deleteGroup(Long groupId, String token) {

		// Find the group by ID or throw exception if not found.
		Group group = findGroupOrThrow(groupId);

		// Invoke private method to validate the group creator.
		validateGroupCreator(group, token);

		// Delete expenses associated by the group.
		List<Expense> expenses = expenseRepository.findAllByGroupId(group);

		for (Expense expense : expenses) {
			expenseServiceImpl.deleteExpenseByGroupAndId(group.getGroupId(), expense.getExpenseId());
		}

		// Persists deletion in the database.
		groupRepository.delete(group);

		return new MessageResponseDTO("Group " + group.getName() + " deleted successfully.");
	}

	@Override
	@Transactional
	public MessageResponseDTO leaveGroup(Long groupId, String token) {

		/* TODO: validateGroupCreator() METHOD SHOULD BE USED HERE BUT ITS HARDER THAN THE OTHER IMPLEMENTATIONS SINCE
		 * TODO: METHOD ALSO HAS VALIDATION TO CHECK IF THE GROUP MEMBER EXISTS IN THE GROUP. */

		// Find the group by ID or throw exception if not found.
		Group group = findGroupOrThrow(groupId);

		// Get the phoneNumber from the current token.
		String userPhoneNumber = jwtService.getUserIdentifierFromToken(token);

		// Check if the group member exists in the group with the provided phoneNumber.
		GroupMember groupMember = group.getRegisteredMembers().stream()
				.filter(member -> member.getId().getUser().getPhoneNumber().equals(userPhoneNumber)).findFirst()
				.orElseThrow(() -> new UserNotFoundException("User with phone " + userPhoneNumber + " not found in the group"));

		// Check if the user is the owner (creator) of the group.
		if (group.getCreatedBy().getPhoneNumber().equals(userPhoneNumber)) {
			// If the user is the owner, delete the group entirely.
			groupRepository.delete(group);

			return new MessageResponseDTO("You have successfully left the group, and it has been deleted.");
		}

		// Remove the Registered member from the group.
		group.getRegisteredMembers().remove(groupMember);

		// Persists the group details in the database.
		groupRepository.save(group);

		// Return success message.
		return new MessageResponseDTO("You have successfully left the group.");
	}

	@Override
	@Transactional
	public MessageResponseDTO updateGroup(Long groupId, Map<String, Object> fields, String token) {

		// Find the group or throw an exception if not found.
		Group group = findGroupOrThrow(groupId);

		// Invoke private method to validate the group creator.
		validateGroupCreator(group, token);

		// Update only the fields present in the Map.
		updateGroupFields(group, fields);

		// Validate the group thanks to the annotations in the Group entity.
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set<ConstraintViolation<Group>> violations = validator.validate(group);

		if (!violations.isEmpty()) {
			
			// TODO: CHECK IF THIS VALIDATIOSN ARE BEING HANDLED BY METHODEXCEPTION IN GLOBALEXCEPTIONHANDLER.

			// If there are validation errors, throw an exception with the messages.
			String errorMessages = violations.stream().map(ConstraintViolation::getMessage)
					.collect(Collectors.joining(", "));
			
			// TODO: ADD CUSTOM VALIDATIONS.
			throw new IllegalArgumentException("Validation errors: " + errorMessages);
		}

		// Save the group with the updated fields.
		groupRepository.save(group);

		return new MessageResponseDTO("Group updated successfully.");
	}

	private void updateGroupFields(Group group, Map<String, Object> fields) {

		// Initialize ObjectMapper to convert values to the correct type.
		ObjectMapper mapper = new ObjectMapper();

		// Iterate through the map entries.
		fields.forEach((key, value) -> {

			// Find the corresponding field in the Group class by the name.
			Field field = ReflectionUtils.findField(Group.class, f -> f.getName().equals(key));

			if (field != null) {

				// Make the field accessible to modify its value.
				field.setAccessible(true);

				// Convert the value to the field's type.
				Object convertedValue = mapper.convertValue(value, field.getType());

				// Set the converted value into the corresponding field to avoid errors.
				ReflectionUtils.setField(field, group, convertedValue);
			}
		});
	}

	@Override
	@Transactional
	public MessageResponseDTO updateGroupEstimatedPrice(Long groupId, UpdateGroupEstimatedPriceRequestDTO request, String token) {

		// Find the group by ID or throw exception if not found.
		Group group = findGroupOrThrow(groupId);

		// Invoke private method to validate the group creator.
		validateGroupCreator(group, token);

		group.setEstimatedPrice(request.getEstimatedPrice());

		groupRepository.save(group);

		expenseServiceImpl.updateExpenseTotalAmount(group, request.getEstimatedPrice());

		return new MessageResponseDTO("Estimated price updated and shares recalculated.");
    }

	@Override
	@Transactional
	public MessageResponseDTO updateGroupRegisteredMembers(Long groupId,
														   UpdateGroupRegisteredMembersRequestDTO request, String token) {

		// Find the group by ID or throw exception if not found.
		Group group = findGroupOrThrow(groupId);

		// Invoke private method to validate the group creator.
		validateGroupCreator(group, token);

		// List invited phone numbers.
		Set<String> invitedPhoneNumbers = new HashSet<>(request.getInvitedRegisteredMembers());

		// Remove current members whose phone numbers are not in the invited list.
		removeUninvitedRegisteredMembers(group, invitedPhoneNumbers);

		// Add new registered members to the list
		addNewRegisteredMembers(group, invitedPhoneNumbers);

		User userCreditor = group.getCreatedBy();

		expenseServiceImpl.updateRegisteredUsers(group, userCreditor, null);
		expenseServiceImpl.updateExternalMembers(group, userCreditor, null);

		// Return success message.
		return new MessageResponseDTO("Group members updated successfully.");
	}

	@Override
	@Transactional
	public MessageResponseDTO updateGroupExternalMembers(Long groupId,
			UpdateGroupExternalMembersRequestDTO request, String token) {

		// Find the group by ID or throw exception if not found.
		Group group = findGroupOrThrow(groupId);

		// Invoke private method to validate the group creator.
		validateGroupCreator(group, token);

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

		User userCreditor = group.getCreatedBy();

		expenseServiceImpl.updateRegisteredUsers(group, userCreditor, null);
		expenseServiceImpl.updateExternalMembers(group, userCreditor, null);

		// Return success message.
		return new MessageResponseDTO("Group external members updated successfully.");
	}

	private void removeUninvitedRegisteredMembers(Group group, Set<String> invitedPhones) {

		// Remove current members whose phone numbers are not in the invited list.
		group.getRegisteredMembers()
				.removeIf(member -> !invitedPhones.contains(member.getId().getUser().getPhoneNumber()));
	}

	private void addNewRegisteredMembers(Group group, Set<String> invitedPhones) {

		// Get phone numbers of the current group members.
		Set<String> currentPhones = group.getRegisteredMembers().stream().map(m -> m.getId().getUser().getPhoneNumber())
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
			group.getRegisteredMembers().add(new GroupMember(id));
		}
	}

	private void validateGroupCreator(Group group, String token) {

		String userPhoneNumber = jwtService.getUserIdentifierFromToken(token);

		User user = userRepository.findByPhoneNumber(userPhoneNumber)
				.orElseThrow(() -> new UserNotFoundException("User not found"));

		if (!group.getCreatedBy().getUserId().equals(user.getUserId())) {
			throw new InvalidGroupCreatorException(
					"User " + user.getUserId() + " has no permissions to update group " + group.getGroupId());
		}
	}

	// Find group by ID or throw if not found.
	private Group findGroupOrThrow(Long groupId) {

		return groupRepository.findById(groupId)
				.orElseThrow(() -> new GroupNotFoundException("Group with ID " + groupId + " not found"));
	}

	// Extract IDs of invited external members from the request.
	private Set<Long> extractExternalMemberIds(UpdateGroupExternalMembersRequestDTO request) {

		return request.getInvitedExternalMembers().stream().map(ExternalMemberDTO::getExternalMembersId)
				.filter(Objects::nonNull).collect(Collectors.toSet());
	}

	// Remove external members that are no longer invited.
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
		newMember.setGroupId(group);

		return newMember;
	}

	// Ensure the addition of the external member to the group.
	private void addMemberToGroupIfMissing(Group group, ExternalMember member) {

        group.getExternalMembers().add(member);
	}
}