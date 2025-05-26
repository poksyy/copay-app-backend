package com.copay.app.service.user;

import java.util.List;

import com.copay.app.dto.MessageResponseDTO;
import com.copay.app.dto.user.response.profile.EmailResponseDTO;
import com.copay.app.dto.user.response.profile.PhoneNumberResponseDTO;
import com.copay.app.dto.user.response.profile.UsernameResponseDTO;
import com.copay.app.dto.user.request.CreateUserRequestDTO;
import com.copay.app.dto.user.response.UserResponseDTO;
import com.copay.app.dto.user.request.UpdateUserRequestDTO;
import com.copay.app.dto.user.request.profile.UpdateEmailDTO;
import com.copay.app.dto.user.request.profile.UpdatePhoneNumberDTO;
import com.copay.app.dto.user.request.profile.UpdateUsernameDTO;

public interface UserService {

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserById(Long id);

    UserResponseDTO getUserByPhoneDTO(String phoneNumber);

    UserResponseDTO createUser(CreateUserRequestDTO request);

    UserResponseDTO updateUserById(Long id, UpdateUserRequestDTO request);

    UsernameResponseDTO updateUsername(Long id, UpdateUsernameDTO request);

    PhoneNumberResponseDTO updatePhoneNumber(Long id, UpdatePhoneNumberDTO request);

    EmailResponseDTO updateEmail(Long id, UpdateEmailDTO request);

    MessageResponseDTO deleteUser(Long userId);
}
