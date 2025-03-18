package com.copay.app.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.copay.app.entity.User;
import com.copay.app.repository.UserRepository;

import net.datafaker.Faker;

@Service
public class FakeDataService {
	private final Faker faker = new Faker();
	private final UserRepository userRepository;

	public FakeDataService(UserRepository userRepository) {
		this.userRepository = userRepository;

	}

	public List<User> generateFakeUsers(int count) {
		List<User> users = IntStream.range(0, count).mapToObj(i -> new User(faker.name().fullName(),
				faker.internet().emailAddress(), faker.phoneNumber().cellPhone())).collect(Collectors.toList());

		return userRepository.saveAll(users);
	}
	
	public void clearUsersFakeData() {
	    userRepository.deleteAll();
	}
}
