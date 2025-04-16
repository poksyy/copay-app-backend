package com.copay.app.service.user;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.copay.app.repository.UserRepository;

import java.time.LocalDateTime;

@Service
public class UserCleanupServiceImpl implements UserCleanupService {

	private final UserRepository userRepository;

	public UserCleanupServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

    // Scheduled task that runs automatically every 2 minutes.
    // This method removes users who have not completed the registration process within the last 5 minutes.
	@Transactional
	@Scheduled(fixedRate = 120000)
	public void deleteIncompleteUsers() {

		LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(5);
		int deletedUsers = userRepository.deleteIncompleteUsers(cutoffTime);
		System.out.println(deletedUsers + " Users were removed due to incomplete registration.");
	}
}
