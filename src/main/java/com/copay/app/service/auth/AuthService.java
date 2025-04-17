package com.copay.app.service.auth;

import com.copay.app.dto.auth.request.UserLoginRequestDTO;
import com.copay.app.dto.auth.request.UserRegisterStepOneDTO;
import com.copay.app.dto.auth.request.UserRegisterStepTwoDTO;
import com.copay.app.dto.auth.response.LoginResponseDTO;
import com.copay.app.dto.auth.response.RegisterStepOneResponseDTO;
import com.copay.app.dto.auth.response.RegisterStepTwoResponseDTO;

public interface AuthService {

    LoginResponseDTO loginUser(UserLoginRequestDTO request);

    RegisterStepOneResponseDTO registerStepOne(UserRegisterStepOneDTO request);

    RegisterStepTwoResponseDTO registerStepTwo(UserRegisterStepTwoDTO request, String token);

    void logout(String token);
}
