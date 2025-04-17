package com.copay.app.service.user;

import java.util.List;
import com.copay.app.dto.user.response.profile.EmailResponseDTO;
import com.copay.app.dto.user.response.profile.PhoneNumberResponseDTO;
import com.copay.app.dto.user.response.profile.UsernameResponseDTO;
import com.copay.app.dto.user.request.UserCreateDTO;
import com.copay.app.dto.user.request.UserDeleteDTO;
import com.copay.app.dto.user.response.UserResponseDTO;
import com.copay.app.dto.user.request.UserUpdateDTO;
import com.copay.app.dto.user.request.profile.UpdateEmailDTO;
import com.copay.app.dto.user.request.profile.UpdatePhoneNumberDTO;
import com.copay.app.dto.user.request.profile.UpdateUsernameDTO;

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
