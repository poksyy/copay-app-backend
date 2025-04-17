package com.copay.app.service.password;

import com.copay.app.dto.password.*;
import com.copay.app.dto.responses.ForgotPasswordResetResponseDTO;
import com.copay.app.dto.responses.ForgotPasswordResponseDTO;
import com.copay.app.dto.responses.ResetPasswordResponseDTO;
import org.springframework.http.ResponseEntity;

public interface PasswordService {
	
    ResponseEntity<ResetPasswordResponseDTO> resetPassword(ResetPasswordDTO request, String token);
    
    ResponseEntity<ForgotPasswordResponseDTO> forgotPassword(ForgotPasswordDTO request);
    
    ResponseEntity<ForgotPasswordResetResponseDTO> forgotPasswordReset(String token, ForgotPasswordResetDTO request);
}
