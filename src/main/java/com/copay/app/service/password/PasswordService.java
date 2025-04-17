package com.copay.app.service.password;

import com.copay.app.dto.password.request.ForgotPasswordDTO;
import com.copay.app.dto.password.request.ForgotPasswordResetDTO;
import com.copay.app.dto.password.request.ResetPasswordDTO;
import com.copay.app.dto.password.response.ForgotPasswordResetResponseDTO;
import com.copay.app.dto.password.response.ForgotPasswordResponseDTO;
import com.copay.app.dto.password.response.ResetPasswordResponseDTO;
import org.springframework.http.ResponseEntity;

public interface PasswordService {
	
    ResponseEntity<ResetPasswordResponseDTO> resetPassword(ResetPasswordDTO request, String token);
    
    ResponseEntity<ForgotPasswordResponseDTO> forgotPassword(ForgotPasswordDTO request);
    
    ResponseEntity<ForgotPasswordResetResponseDTO> forgotPasswordReset(String token, ForgotPasswordResetDTO request);
}
