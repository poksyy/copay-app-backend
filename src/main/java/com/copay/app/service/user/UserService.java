package com.copay.app.service.user;

import java.util.List;
import com.copay.app.dto.responses.profile.EmailResponseDTO;
import com.copay.app.dto.responses.profile.PhoneNumberResponseDTO;
import com.copay.app.dto.responses.profile.UsernameResponseDTO;
import com.copay.app.dto.user.UserCreateDTO;
import com.copay.app.dto.user.UserDeleteDTO;
import com.copay.app.dto.user.UserResponseDTO;
import com.copay.app.dto.user.UserUpdateDTO;
import com.copay.app.dto.user.profile.UpdateEmailDTO;
import com.copay.app.dto.user.profile.UpdatePhoneNumberDTO;
import com.copay.app.dto.user.profile.UpdateUsernameDTO;

public interface UserService {

    UserResponseDTO getUserByIdDTO(Long id);

    UserResponseDTO getUserByPhoneDTO(String phoneNumber);

    UserResponseDTO createUser(UserCreateDTO request);

    UserResponseDTO updateUser(Long id, UserUpdateDTO request);

    UserResponseDTO updateUser(String email, UserUpdateDTO request);

    UserDeleteDTO deleteUser(Long userId);

    List<UserResponseDTO> getAllUsers();

    UsernameResponseDTO updateUsername(Long id, UpdateUsernameDTO request);

    PhoneNumberResponseDTO updatePhoneNumber(Long id, UpdatePhoneNumberDTO request);

    EmailResponseDTO updateEmail(Long id, UpdateEmailDTO request);
}
