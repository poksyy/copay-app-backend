package com.copay.app.service;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import com.copay.app.validation.ValidationErrorResponse;

import java.util.ArrayList;
import java.util.List;

public class ValidationService {

	public static ValidationErrorResponse validate(BindingResult result) {

		// Check if there are validation errors.
		if (result.hasErrors()) {
			// Create a list to hold error messages.
			List<String> errorMessages = new ArrayList<>();

			// Iterate over all errors and collect their messages.
			for (ObjectError error : result.getAllErrors()) {
				errorMessages.add(error.getDefaultMessage());
			}
			
			// Return the errorrs to the AuthController.
			return new ValidationErrorResponse(errorMessages, "Validation failed", HttpStatus.BAD_REQUEST.value());
		}

		return null;
	}
}
