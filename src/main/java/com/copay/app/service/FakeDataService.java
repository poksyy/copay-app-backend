package com.copay.app.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.copay.app.entity.Group;
import com.copay.app.repository.GroupRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.copay.app.entity.User;
import com.copay.app.repository.UserRepository;

import net.datafaker.Faker;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FakeDataService {
	private final Faker faker = new Faker();
	private final UserRepository userRepository;
	private final GroupRepository groupRepository;
	private final PasswordEncoder passwordEncoder;

	// Constructor
	public FakeDataService(UserRepository userRepository, GroupRepository groupRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.groupRepository = groupRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public List<User> generateFakeUsers(int count) {

		List<User> users = IntStream.range(0, count).mapToObj(i -> new User(
				faker.name().fullName(),
				faker.internet().emailAddress(),
				passwordEncoder.encode("Test12345"),
				"+" + faker.number().numberBetween(1, 100),
				faker.phoneNumber().cellPhone().replaceAll("[^0-9]", "")
		)).collect(Collectors.toList());

		return userRepository.saveAll(users);
	}

	@Transactional
	public void clearFakeUsers() {
	    userRepository.deleteAll();
	}

	@Transactional
	public List<Group> generateFakeGroups(int count) {
        final User finalCreator = userRepository.findAll().getFirst();

		List<Group> groups = IntStream.range(0, count).mapToObj(i -> {
			Group group = new Group();
			group.setName(faker.team().name());
			group.setCreatedBy(finalCreator);
			group.setCurrency("EUR");
			group.setDescription(faker.lorem().sentence(5));
			group.setEstimatedPrice((float) faker.number().randomDouble(2, 100, 5000));
			group.setImageUrl(faker.internet().image());
			group.setImageProvider("faker");
			return group;
		}).collect(Collectors.toList());

		return groupRepository.saveAll(groups);
	}

	@Transactional
	public void clearFakeGroups() {
		groupRepository.deleteAll();
	}
}
