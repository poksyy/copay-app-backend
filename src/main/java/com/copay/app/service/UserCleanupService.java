package com.copay.app.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.copay.app.repository.UserRepository;

@Service
public class UserCleanupService {

	private final UserRepository userRepository;

	public UserCleanupService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

    // Scheduled task that runs automatically every 5 minutes.
    // This method removes users who have not completed the registration process.
	@Transactional
	@Scheduled(fixedRate = 300000)
	public void deleteIncompleteUsers() {
		int deletedUsers = userRepository.deleteIncompleteUsers();
		System.out.println(deletedUsers + " Users were removed due to incomplete registration.");
	}
}
