package com.copay.app.service.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.service.query.UserQueryService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.copay.app.dto.user.response.profile.EmailResponseDTO;
import com.copay.app.dto.user.response.profile.PhoneNumberResponseDTO;
import com.copay.app.dto.user.response.profile.UsernameResponseDTO;
import com.copay.app.dto.user.request.CreateUserRequestDTO;
import com.copay.app.dto.user.response.UserResponseDTO;
import com.copay.app.dto.user.request.UpdateUserRequestDTO;
import com.copay.app.dto.user.request.profile.UpdateEmailRequestDTO;
import com.copay.app.dto.user.request.profile.UpdatePhoneNumberRequestDTO;
import com.copay.app.dto.user.request.profile.UpdateUsernameRequestDTO;
import com.copay.app.entity.User;
import com.copay.app.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserAvailabilityServiceImpl userAvailabilityServiceImpl;

    private final UserQueryService userQueryService;

    // Constructor.
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           UserAvailabilityServiceImpl userAvailabilityServiceImpl, UserQueryService userQueryService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userAvailabilityServiceImpl = userAvailabilityServiceImpl;
        this.userQueryService = userQueryService;
    }

    @Override
    @Transactional
    public UserResponseDTO createUser(CreateUserRequestDTO request) {

        User newUser = new User();

        newUser.setEmail(request.getEmail());
        newUser.setPhoneNumber(request.getPhoneNumber());

        // Validate user credentials availability (only unique fields in database).
        userAvailabilityServiceImpl.checkUserExistence(newUser);

        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setCompleted(true);

        User savedUser = userRepository.save(newUser);

        return new UserResponseDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByIdDTO(Long id) {

        // Find user via UserQueryService, which delegates exception handling to UserValidator.
        User user = userQueryService.getUserById(id);

        return new UserResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByPhoneDTO(String phoneNumber) {

        // Find user via UserQueryService, which delegates exception handling to UserValidator.
        User user = userQueryService.getUserByPhone(phoneNumber);

        return new UserResponseDTO(user);
    }

    @Override
    @Transactional
    // TODO: USE REFLECTION UTILS IN THIS METHOD TO UPDATE EACH USER ENTITY FIELD THROUGH 1 SINGLE ENDPOINT
    public UserResponseDTO updateUser(Long id, UpdateUserRequestDTO request) {

        // Find user via UserQueryService, which delegates exception handling to UserValidator.
        User user = userQueryService.getUserById(id);

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Validate user credentials availability (only unique fields in database).
        userAvailabilityServiceImpl.checkUserExistence(user);

        User updatedUser = userRepository.save(user);

        return new UserResponseDTO(updatedUser);
    }

    @Override
    @Transactional
    public MessageResponseDTO deleteUser(Long id) {

        // Find user via UserQueryService, which delegates exception handling to UserValidator.
        User user = userQueryService.getUserById(id);

        userRepository.delete(user);

        return new MessageResponseDTO("Your account has been deleted successfully.");
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {

        List<User> users = userRepository.findAll();

        return users.stream().map(UserResponseDTO::new).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UsernameResponseDTO updateUsername(Long id, UpdateUsernameRequestDTO request) {

        // Find user via UserQueryService, which delegates exception handling to UserValidator.
        User user = userQueryService.getUserById(id);

        user.setUsername(request.getUsername());

        User updatedUser = userRepository.save(user);

        return new UsernameResponseDTO(updatedUser);
    }

    @Override
    @Transactional
    public PhoneNumberResponseDTO updatePhoneNumber(Long id, UpdatePhoneNumberRequestDTO request) {

        // Find user via UserQueryService, which delegates exception handling to UserValidator.
        User user = userQueryService.getUserById(id);

        // Checks if email exists via UserQueryService, which delegates exception handling to UserValidator.
        userQueryService.existsUserByPhone(request.getPhoneNumber());

        user.setPhoneNumber(request.getPhoneNumber());

        User updatedUser = userRepository.save(user);

        return new PhoneNumberResponseDTO(updatedUser);
    }

    @Override
    @Transactional
    public EmailResponseDTO updateEmail(Long id, UpdateEmailRequestDTO request) {

        // Find user via UserQueryService, which delegates exception handling to UserValidator.
        User user = userQueryService.getUserById(id);

        // Checks if email exists via UserQueryService, which delegates exception handling to UserValidator.
        userQueryService.existsUserByEmail(request.getEmail());

        user.setEmail(request.getEmail());

        User updatedUser = userRepository.save(user);

        return new EmailResponseDTO(updatedUser);
    }
}
