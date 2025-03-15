package com.copay.app.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.copay.app.validation.ValidationErrorResponse;

// Global exception handler to manage different exceptions in the application.
@RestControllerAdvice
public class GlobalExceptionHandler {

	// HTTP 400: Phone number already exists.
	@ExceptionHandler(PhoneAlreadyExistsException.class)
	public ResponseEntity<ValidationErrorResponse> handlePhoneAlreadyExists(PhoneAlreadyExistsException ex) {

		// Constructing response with error details
		ValidationErrorResponse errorResponse = new ValidationErrorResponse(List.of(ex.getMessage()),
				"Phone number already exists, please change it.", HttpStatus.BAD_REQUEST.value());

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	// HTTP 400: Email already exists.
	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<ValidationErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {

		ValidationErrorResponse errorResponse = new ValidationErrorResponse(List.of(ex.getMessage()),
				"Email already exists, please change it.", HttpStatus.BAD_REQUEST.value());

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	// HTTP 403: User is trying to modify password of another user.
	@ExceptionHandler(UserPermissionException.class)
	public ResponseEntity<ValidationErrorResponse> handleUserPermissionException(UserPermissionException ex) {

		ValidationErrorResponse errorResponse = new ValidationErrorResponse(List.of(ex.getMessage()),
				"You can only update your own password.", HttpStatus.FORBIDDEN.value());

		return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
	}

	// HTTP 400: Incorrect password provided.
	@ExceptionHandler(IncorrectPasswordException.class)
	public ResponseEntity<ValidationErrorResponse> handleIncorrectPasswordException(IncorrectPasswordException ex) {

		ValidationErrorResponse errorResponse = new ValidationErrorResponse(List.of(ex.getMessage()),
				"Current password is incorrect.", HttpStatus.BAD_REQUEST.value());

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	// Handle other generic exceptions.
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ValidationErrorResponse> handleRuntimeException(RuntimeException ex) {

		ValidationErrorResponse errorResponse = new ValidationErrorResponse(List.of(ex.getMessage()),
				"Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR.value());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}

	// HTTP 400: User uniqueness violation (Phone number and email already exist).
	@ExceptionHandler(UserUniquenessException.class)
	public ResponseEntity<ValidationErrorResponse> handleUserUniquenessException(UserUniquenessException ex) {
		ValidationErrorResponse errorResponse = new ValidationErrorResponse(List.of(ex.getMessage()),
				"Phone number and email already exist.", HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	// HTTP 404: User not found with the provided ID.
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ValidationErrorResponse> handleUserNotFound(UserNotFoundException ex) {

		ValidationErrorResponse errorResponse = new ValidationErrorResponse(List.of(ex.getMessage()),
				"User does not exist.", HttpStatus.NOT_FOUND.value());

		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

}