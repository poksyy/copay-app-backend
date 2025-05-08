package com.copay.app.service.password;

import com.copay.app.dto.password.request.ForgotPasswordRequestDTO;
import com.copay.app.dto.password.request.ForgotPasswordResetRequestDTO;
import com.copay.app.dto.password.request.ResetPasswordRequestDTO;
import com.copay.app.dto.password.response.ForgotPasswordResetResponseDTO;
import com.copay.app.dto.password.response.ForgotPasswordResponseDTO;
import com.copay.app.dto.password.response.ResetPasswordResponseDTO;
import org.springframework.http.ResponseEntity;

public interface PasswordService {
	
    ResponseEntity<ResetPasswordResponseDTO> resetPassword(ResetPasswordRequestDTO request, String token);
    
    ResponseEntity<ForgotPasswordResponseDTO> forgotPassword(ForgotPasswordRequestDTO request);
    
    ResponseEntity<ForgotPasswordResetResponseDTO> forgotPasswordReset(String token, ForgotPasswordResetRequestDTO request);
}
