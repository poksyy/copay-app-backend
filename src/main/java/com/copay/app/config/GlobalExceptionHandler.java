package com.copay.app.config;

import java.util.List;

import com.copay.app.exception.user.EmailAlreadyExistsException;
import com.copay.app.exception.email.EmailSendingException;
import com.copay.app.exception.group.GroupNotFoundException;
import com.copay.app.exception.group.InvalidGroupCreatorException;
import com.copay.app.exception.group.InvitedMemberNotFoundException;
import com.copay.app.exception.user.PhoneAlreadyExistsException;
import com.copay.app.exception.token.InvalidTokenException;
import com.copay.app.exception.user.*;
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

	// HTTP 400: User uniqueness violation (Phone number and email already exist).
	@ExceptionHandler(UserUniquenessException.class)

	public ResponseEntity<ValidationErrorResponse> handleUserUniquenessException(UserUniquenessException ex) {

		ValidationErrorResponse errorResponse = new ValidationErrorResponse(List.of(ex.getMessage()),
				"Phone number and email already exist.", HttpStatus.BAD_REQUEST.value());
		
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	// HTTP 404: User not found with the provided ID.
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ValidationErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {

		ValidationErrorResponse errorResponse = new ValidationErrorResponse(List.of(ex.getMessage()),
				"User does not exist.", HttpStatus.NOT_FOUND.value());

		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	// HTTP 404: Invited member doesn't have a Copay account.
	@ExceptionHandler(InvitedMemberNotFoundException.class)
	public ResponseEntity<ValidationErrorResponse> handleInvitedMemberNotFoundException(InvitedMemberNotFoundException ex) {

		ValidationErrorResponse errorResponse = new ValidationErrorResponse(List.of(ex.getMessage()), ex.getMessage(),
				HttpStatus.NOT_FOUND.value());

		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	// HTTP 401: Invalid or expired token.
	@ExceptionHandler(InvalidTokenException.class)

	public ResponseEntity<ValidationErrorResponse> handleInvalidTokenException(InvalidTokenException ex) {
		
		ValidationErrorResponse errorResponse = new ValidationErrorResponse(List.of(ex.getMessage()),
				"Invalid or expired token.", HttpStatus.UNAUTHORIZED.value());
		
		return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	}

	// HTTP 500: Error sending email.
	@ExceptionHandler(EmailSendingException.class)

	public ResponseEntity<ValidationErrorResponse> handleEmailSendingException(EmailSendingException ex) {
		
		ValidationErrorResponse errorResponse = new ValidationErrorResponse(List.of(ex.getMessage()),
				"Error sending the email.", HttpStatus.INTERNAL_SERVER_ERROR.value());
		
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// HTTP 409: Conflict when adding existing member to the group.
	@ExceptionHandler(UserAlreadyMemberException.class)
	public ResponseEntity<ValidationErrorResponse> handleUserAlreadyExistsException(UserAlreadyMemberException ex) {
		ValidationErrorResponse errorResponse = new ValidationErrorResponse(List.of(ex.getMessage()),
				"User is already on the group.", HttpStatus.CONFLICT.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
	}

	// HTTP 403: Invalid user trying to delete a group.
	@ExceptionHandler(InvalidGroupCreatorException.class)
	public ResponseEntity<ValidationErrorResponse> handleInvalidGroupCreatorException(InvalidGroupCreatorException ex) {

		ValidationErrorResponse errorResponse = new ValidationErrorResponse(List.of(ex.getMessage()),
				"You don't have the permissions to delete this group.", HttpStatus.FORBIDDEN.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
	}
	
	// HTTP 404: Group not found with the provided ID.
	@ExceptionHandler(GroupNotFoundException.class)
	public ResponseEntity<ValidationErrorResponse> handleGroupNotFoundException(GroupNotFoundException ex) {

		ValidationErrorResponse errorResponse = new ValidationErrorResponse(List.of(ex.getMessage()),
				"Specified group doesn't exist.", HttpStatus.NOT_FOUND.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	// Handle other generic exceptions.
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ValidationErrorResponse> handleRuntimeException(RuntimeException ex) {

		ValidationErrorResponse errorResponse = new ValidationErrorResponse(List.of(ex.getMessage()),
				"Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR.value());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}
}