package com.copay.app.service.auth;

import com.copay.app.dto.auth.request.GoogleLoginRequestDTO;
import com.copay.app.dto.auth.request.UserLoginRequestDTO;
import com.copay.app.dto.auth.request.UserRegisterStepOneRequestDTO;
import com.copay.app.dto.auth.request.UserRegisterStepTwoRequestDTO;
import com.copay.app.dto.auth.response.LoginResponseDTO;
import com.copay.app.dto.auth.response.RegisterStepOneResponseDTO;
import com.copay.app.dto.auth.response.RegisterStepTwoResponseDTO;

public interface AuthService {

    LoginResponseDTO loginUser(UserLoginRequestDTO request);

    RegisterStepOneResponseDTO registerStepOne(UserRegisterStepOneRequestDTO request);

    RegisterStepTwoResponseDTO registerStepTwo(UserRegisterStepTwoRequestDTO request, String token);

    void logout(String token);

    LoginResponseDTO loginWithGoogle(GoogleLoginRequestDTO request);
}
