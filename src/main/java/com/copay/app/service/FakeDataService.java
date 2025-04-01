package com.copay.app.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.copay.app.entity.User;
import com.copay.app.repository.UserRepository;

import net.datafaker.Faker;

@Service
public class FakeDataService {
	private final Faker faker = new Faker();
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public FakeDataService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public List<User> generateFakeUsers(int count) {
		List<User> users = IntStream.range(0, count).mapToObj(i -> new User(
				faker.name().fullName(),
				faker.internet().emailAddress(),
				passwordEncoder.encode("Test12345"),
				faker.phoneNumber().cellPhone().replaceAll("[^0-9]", "")
		)).collect(Collectors.toList());

		return userRepository.saveAll(users);
	}
	
	public void clearUsersFakeData() {
	    userRepository.deleteAll();
	}
}
