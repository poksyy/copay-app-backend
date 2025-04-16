package com.copay.app.service.auth;

import com.copay.app.dto.auth.UserLoginRequestDTO;
import com.copay.app.dto.auth.UserRegisterStepOneDTO;
import com.copay.app.dto.auth.UserRegisterStepTwoDTO;
import com.copay.app.dto.responses.LoginResponseDTO;
import com.copay.app.dto.responses.RegisterStepOneResponseDTO;
import com.copay.app.dto.responses.RegisterStepTwoResponseDTO;

public interface AuthService {

    LoginResponseDTO loginUser(UserLoginRequestDTO request);

    RegisterStepOneResponseDTO registerStepOne(UserRegisterStepOneDTO request);

    RegisterStepTwoResponseDTO registerStepTwo(UserRegisterStepTwoDTO request, String token);

    void logout(String token);
}
