package com.copay.app.service.group;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.copay.app.dto.expense.response.CreditorResponseDTO;
import com.copay.app.dto.group.auxiliary.*;
import com.copay.app.dto.group.request.UpdateGroupEstimatedPriceRequestDTO;
import com.copay.app.dto.group.response.GetGroupMembersResponseDTO;
import com.copay.app.dto.group.response.GroupResponseDTO;
import com.copay.app.entity.Expense;
import com.copay.app.exception.group.*;
import com.copay.app.repository.expense.ExpenseRepository;

import com.copay.app.service.expense.ExpenseService;
import com.copay.app.service.expense.GroupExpenseService;
import com.copay.app.service.notification.NotificationService;
import com.copay.app.service.query.GroupQueryService;
import com.copay.app.service.query.UserQueryService;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.group.request.CreateGroupRequestDTO;
import com.copay.app.dto.group.request.UpdateGroupRegisteredMembersRequestDTO;
import com.copay.app.dto.group.request.UpdateGroupExternalMembersRequestDTO;
import com.copay.app.dto.group.response.GetGroupResponseDTO;
import com.copay.app.entity.Group;
import com.copay.app.entity.User;
import com.copay.app.entity.relations.ExternalMember;
import com.copay.app.entity.relations.GroupMember;
import com.copay.app.entity.relations.GroupMemberId;
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

	private final ExpenseRepository expenseRepository;

	private final JwtService jwtService;

	private final GroupExpenseService groupExpenseService;

	private final ExpenseService expenseService;

	private final UserQueryService userQueryService;

	private final GroupQueryService groupQueryService;

	private final NotificationService notificationService;

	@PersistenceContext
	private EntityManager entityManager;

	// Constructor to initialize all the instances.
	public GroupServiceImpl(GroupRepository groupRepository, GroupMemberRepository groupMemberRepository,
                            UserRepository userRepository, ExternalMemberRepository externalMemberRepository, ExpenseRepository expenseRepository, JwtService jwtService, GroupExpenseService groupExpenseService,
                            ExpenseService expenseService, UserQueryService userQueryService, GroupQueryService groupQueryService, NotificationService notificationService, EntityManager entityManager) {

		this.groupRepository = groupRepository;
		this.groupMemberRepository = groupMemberRepository;
		this.userRepository = userRepository;
		this.externalMemberRepository = externalMemberRepository;
		this.expenseRepository = expenseRepository;
		this.jwtService = jwtService;
		this.groupExpenseService = groupExpenseService;
		this.expenseService = expenseService;
        this.userQueryService = userQueryService;
        this.groupQueryService = groupQueryService;
        this.notificationService = notificationService;
        this.entityManager = entityManager;
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
	public GroupResponseDTO getGroupByGroupId(Long groupId) {

		// Find the group by ID or throw exception if not found.
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new GroupNotFoundException("Group with ID " + groupId + " not found"));

		// Fetch all expenses associated with the group.
		List<Expense> expenses = expenseRepository.findByGroupId_GroupId(groupId);

		User paidByUser = null;
		ExternalMember paidByExternalMember = null;

		if (!expenses.isEmpty()) {
			Expense expense = expenses.getFirst();
			paidByUser = expense.getPaidByUser();
			paidByExternalMember = expense.getPaidByExternalMember();
		}

		// Get the creator ID to determine if the user is the owner (used in DTO mapping)
		Long userId = group.getCreatedBy().getUserId();

		// Map the group entity to a response DTO and return it.
		return mapToGroupResponseDTO(group, userId, paidByUser, paidByExternalMember);
	}

	@Override
	@Transactional(readOnly = true)
	public GetGroupMembersResponseDTO getGroupMembersByGroup(Long groupId) {

		// Find group via GroupQueryService, which delegates exception handling to GroupValidator.
		Group group = groupQueryService.getGroupById(groupId);

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
	public GroupResponseDTO createGroup(CreateGroupRequestDTO request) {

		// Find user via UserQueryService, which delegates exception handling to UserValidator.
		User creator = userQueryService.getUserById(request.getCreatedBy());

		// Validate only one payer (only one can have Payer=true)
		boolean hasRegisteredPayer = request.getInvitedRegisteredMembers().stream().anyMatch(InvitedRegisteredMemberDTO::isCreditor);
		boolean hasExternalPayer = request.getInvitedExternalMembers().stream().anyMatch(InvitedExternalMemberDTO::isCreditor);

		if ((hasRegisteredPayer && hasExternalPayer) || (!hasRegisteredPayer && !hasExternalPayer)) {
			throw new InvalidPayerSelectionException("Exactly one payer must be selected, either registered or external.");
		}

		// Loop to check if the invited registered members have an account.
		for (InvitedRegisteredMemberDTO member : request.getInvitedRegisteredMembers()) {
			userRepository.findByPhoneNumber(member.getPhoneNumber()).orElseThrow(() ->
					new InvitedMemberNotFoundException("This phone number owner doesn't have an account: " + member.getPhoneNumber()));
		}

		// Create a group instance.
		Group group = new Group();

		group.setCreatedBy(creator);
		group.setName(request.getName());
		group.setDescription(request.getDescription());
		group.setEstimatedPrice(request.getEstimatedPrice());
		group.setCurrency(request.getCurrency());

		// Persists the group details in the database.
		group = groupRepository.save(group);

		// Create the composite key for the group-member relationship with the group and user.
		GroupMemberId creatorMemberId = new GroupMemberId(group, creator);
		// Instantiate the GroupMember entity using the composite key.
		GroupMember creatorGroupMember = new GroupMember(creatorMemberId);

		// Persist the new group member to the repository, saving the relationship.
		groupMemberRepository.save(creatorGroupMember);

		// Process all invited registered members into the database.
		for (InvitedRegisteredMemberDTO registeredMember : request.getInvitedRegisteredMembers()) {

			User invitedRegisteredMember = userRepository.findByPhoneNumber(registeredMember.getPhoneNumber()).get();

			GroupMemberId memberId = new GroupMemberId(group, invitedRegisteredMember);

			// Validates if the user already belongs to the group and skip the user if is already a member of the group.
			if (groupMemberRepository.existsById(memberId)) continue;

			// If the user is not already a member, create and add them to the group.
			GroupMember groupMember = new GroupMember(memberId);

			// Add the user to the group
			group.getRegisteredMembers().add(groupMember);
		}

		// Loop to interact with the List of the externalMembers.
		for (InvitedExternalMemberDTO externalMember : request.getInvitedExternalMembers()) {

			// Create and persist an ExternalMember.
			ExternalMember external = new ExternalMember();

			external.setName(externalMember.getName());
			external.setGroupId(group);
			external.setJoinedAt(LocalDateTime.now());

			// Add the external member to the group
			group.getExternalMembers().add(external);
		}

		/*
		 * Explicitly merge the 'group' entity to flush changes and synchronize them
		 * with the database. This is necessary because we avoid using
		 * 'groupMemberRepository.save()' and 'externalMemberRepository.save()' in favor
		 * of adding members directly to the group using the 'add()' method, which
		 * relies on cascading.
		 */
		entityManager.merge(group);

		// Identify the payer among registered or external members.
		User paidByUser = null;
		ExternalMember paidByExternalMember = null;

		// Check if any registered member is marked as the payer.
		Optional<InvitedRegisteredMemberDTO> payerRegistered = request.getInvitedRegisteredMembers().stream()
				.filter(InvitedRegisteredMemberDTO::isCreditor)
				.findFirst();

		// Check if any external member is marked as the payer.
		Optional<InvitedExternalMemberDTO> payerExternal = request.getInvitedExternalMembers().stream()
				.filter(InvitedExternalMemberDTO::isCreditor)
				.findFirst();

		if (payerRegistered.isPresent()) {

			// Get the registered payer user from the database by phone number.
			String phone = payerRegistered.get().getPhoneNumber();
			paidByUser = userRepository.findByPhoneNumber(phone)
					.orElseThrow(() -> new UserNotFoundException("Registered creditor user with phone " + phone + " not found"));
		} else {

			// Get the external payer member from the persisted group by name.
			String payerName = payerExternal.get().getName();
			paidByExternalMember = group.getExternalMembers().stream()
					.filter(em -> em.getName().equals(payerName))
					.findFirst()
					.orElseThrow(() -> new ExternalMemberNotFoundException("External member creditor with name " + payerName + " not found"));
		}

		// Create expense for the group through the groupExpenseService interface.
		groupExpenseService.initializeExpenseFromGroup(group, group.getEstimatedPrice(), paidByUser, paidByExternalMember);

		// Send notifications to all invited registered members.
		for (InvitedRegisteredMemberDTO registeredMember : request.getInvitedRegisteredMembers()) {
			User invitedUser = userRepository.findByPhoneNumber(registeredMember.getPhoneNumber()).get();
			String notificationMessage = String.format(
				"You have been added to group '%s' by %s", 
				group.getName(), 
				creator.getUsername()
			);
			notificationService.createNotification(invitedUser, notificationMessage);
		}

		// Map the group instance to GroupResponseDTO.
		return mapToGroupResponseDTO(group, request.getCreatedBy(), paidByUser, paidByExternalMember);
	}

	@Override
	@Transactional
	public MessageResponseDTO updateGroup(Long groupId, Map<String, Object> fields, String token) {

		// Find the group by ID or throw exception if not found.
		Group group = groupQueryService.getGroupById(groupId);

		// Validates if the user is the group creator.
		groupQueryService.validateGroupCreator(group, token);

		// Update only the fields present in the Map.
		updateGroupFields(group, fields);

		// Validate the group thanks to the annotations in the Group entity.
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set<ConstraintViolation<Group>> violations = validator.validate(group);

		// Annotation validations through entities have to be managed manually.
		if (!violations.isEmpty()) {

			// If there are validation errors, throw an exception with the messages.
			String errorMessages = violations.stream().map(ConstraintViolation::getMessage)
					.collect(Collectors.joining(", "));

			throw new InvalidGroupUpdateException("Validation errors: " + errorMessages);
		}

		// Save the group with the updated fields.
		groupRepository.save(group);

		return new MessageResponseDTO("Group updated successfully.");
	}

	// This only updats name or description of the group.
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

		// Find group via GroupQueryService, which delegates exception handling to GroupValidator.
		Group group = groupQueryService.getGroupById(groupId);

		// Validates if the user is the group creator.
		groupQueryService.validateGroupCreator(group, token);

		group.setEstimatedPrice(request.getEstimatedPrice());

		groupRepository.save(group);

		// Updates the new expense total through the groupExpenseService interface.
		groupExpenseService.updateExpenseTotalAmount(group, request.getEstimatedPrice());

		// Send notifications to all registered members.
		for (GroupMember member : group.getRegisteredMembers()) {
			User user = member.getId().getUser();

			// Don't send notification to the group creator (they already know they updated the price).
			if (!user.getUserId().equals(group.getCreatedBy().getUserId())) {

				String notificationMessage = String.format(
					"The estimated price for group '%s' has been updated to %.2f %s", 
					group.getName(), 
					group.getEstimatedPrice(),
					group.getCurrency()
				);
				notificationService.createNotification(user, notificationMessage);
			}
		}

		return new MessageResponseDTO("Estimated price updated and shares recalculated.");
    }

	@Override
	@Transactional
	public MessageResponseDTO updateGroupRegisteredMembers(Long groupId, UpdateGroupRegisteredMembersRequestDTO request, String token) {

		// Find group via GroupQueryService, which delegates exception handling to GroupValidator.
		Group group = groupQueryService.getGroupById(groupId);

		// Validates if the user is the group creator.
		groupQueryService.validateGroupCreator(group, token);

		// List invited phone numbers.
		Set<String> invitedPhoneNumbers = new HashSet<>(request.getInvitedRegisteredMembers());

		// Remove current members whose phone numbers are not in the invited list.
		removeUninvitedRegisteredMembers(group, invitedPhoneNumbers);

		// Add new registered members to the list
		addNewRegisteredMembers(group, invitedPhoneNumbers);

		User userCreditor = group.getCreatedBy();

		// Update the expense group members and the money distribution through the expenseService interface.
		groupExpenseService.updateExpenseGroupMembers(group, userCreditor, null);

		for (String phone : request.getInvitedRegisteredMembers()) {
			User user = userRepository.findByPhoneNumber(phone)
					.orElseThrow(() -> new UserNotFoundException(
							"User with phone " + phone + " not found"));
			String notificationMessage = String.format(
					"The member list for group '%s' has been updated. Please check the group details—" +
							"the estimated price and your share may have changed.",
					group.getName()
			);
			notificationService.createNotification(user, notificationMessage);
		}

		// Return success message.
		return new MessageResponseDTO("Group members updated successfully.");
	}

	@Override
	@Transactional
	public MessageResponseDTO updateGroupExternalMembers(Long groupId,
			UpdateGroupExternalMembersRequestDTO request, String token) {

		// Find group via GroupQueryService, which delegates exception handling to GroupValidator.
		Group group = groupQueryService.getGroupById(groupId);

		// Validates if the user is the group creator.
		groupQueryService.validateGroupCreator(group, token);

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
			group.getExternalMembers().add(member);
		}

		User userCreditor = group.getCreatedBy();

		// Update the expense group members and the money distribution through the expenseService interface.
		groupExpenseService.updateExpenseGroupMembers(group, userCreditor, null);

		for (GroupMember member : group.getRegisteredMembers()) {
			User user = member.getId().getUser();
			String notificationMessage = String.format(
					"The member list for group '%s' has been updated. Please check the group details—" +
							"the estimated price and your share may have changed.",
					group.getName()
			);
			notificationService.createNotification(user, notificationMessage);
		}

		// Return success message.
		return new MessageResponseDTO("Group members updated successfully.");
	}

	@Override
	@Transactional
	public MessageResponseDTO deleteGroup(Long groupId, String token) {

		// Find group via GroupQueryService, which delegates exception handling to GroupValidator.
		Group group = groupQueryService.getGroupById(groupId);

		// Validates if the user is the group creator.
		groupQueryService.validateGroupCreator(group, token);

		// Delete expenses associated by the group.
		List<Expense> expenses = expenseRepository.findAllByGroupId(group);

		// Deletes the expenses of the deleted group through the expenseService interface.
		for (Expense expense : expenses) {
			expenseService.deleteExpenseByGroupAndId(group.getGroupId(), expense.getExpenseId());
		}

		// Persists deletion in the database.
		groupRepository.delete(group);

		return new MessageResponseDTO("Group " + group.getName() + " deleted successfully.");
	}

	@Override
	@Transactional
	public MessageResponseDTO leaveGroup(Long groupId, String token) {

		// Find group via GroupQueryService, which delegates exception handling to GroupValidator.
		Group group = groupQueryService.getGroupById(groupId);

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

		// Return a success message.
		return new MessageResponseDTO("You have successfully left the group.");
	}

	// Helper method to map Group entity to GroupResponseDTO.
	private GroupResponseDTO mapToGroupResponseDTO(Group group, Long userId, User paidByUser, ExternalMember paidByExternalMember) {

		// Initialize an instance of the DTO that is going to be used as a response.
		GroupResponseDTO groupResponseDTO = new GroupResponseDTO();

		// Set group details.
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

		// Map external members (ExternalMemberDTO -> includes externalMembersId and name).
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

	private void removeUninvitedRegisteredMembers(Group group, Set<String> invitedPhones) {

		// Verify if a debtor is being removed.
		User creditorUser = expenseRepository.findByGroupId_GroupId(group.getGroupId())
				.stream()
				.findFirst()
				.map(Expense::getPaidByUser)
				.orElse(null);

		if (creditorUser != null) {
			boolean isCreditorInvited = invitedPhones.contains(creditorUser.getPhoneNumber());

			// If the removed member is the creditor, throw a custom exception.
			if (!isCreditorInvited) {

				throw new InvalidCreditorRemovalException("The creditor can't be removed from the group.");
			}
		}

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

			// Find user via userQueryService, which delegates exception handling to userValidator.
			User user = userQueryService.getUserByPhone(phone);

			// Create and add a new group member to the group.
			GroupMemberId id = new GroupMemberId(group, user);
			group.getRegisteredMembers().add(new GroupMember(id));

			// Send notification to the newly added member
			String notificationMessage = String.format(
				"You have been added to group '%s' by %s", 
				group.getName(), 
				group.getCreatedBy().getUsername()
			);
			notificationService.createNotification(user, notificationMessage);
		}
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

		// Validates if the provided externalMemberID exists in the database.
		if (dto.getExternalMembersId() != null) {
			return externalMemberRepository.findById(dto.getExternalMembersId()).orElseThrow(
					() -> new ExternalMemberNotFoundException("External member with ID " + dto.getExternalMembersId() + " not found"));
		}

		ExternalMember newMember = new ExternalMember();

		newMember.setGroupId(group);

		return newMember;
	}
}
