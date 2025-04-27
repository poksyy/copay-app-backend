package com.copay.app.config;

import java.util.List;

import com.copay.app.exception.user.EmailAlreadyExistsException;
import com.copay.app.exception.email.EmailSendingException;
import com.copay.app.exception.group.GroupNotFoundException;
import com.copay.app.exception.group.InvalidGroupCreatorException;
import com.copay.app.exception.group.InvitedMemberNotFoundException;
import com.copay.app.exception.user.PhoneAlreadyExistsException;
import com.copay.app.exception.user.*;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    // HTTP 404: Expense not found with the provided ID.
    //@ExceptionHandler(ExpenseNotFoundException.class)
    //public ResponseEntity<ValidationErrorResponse> handleExpenseNotFoundExepction(ExpenseNotFoundException ex) {
//
    //		ValidationErrorResponse errorResponse = new ValidationErrorResponse(List.of(ex.getMessage()),
    //			"Specified expense doesn't exist.", HttpStatus.NOT_FOUND.value());
//
    //		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    //}

    // HTTP 400: Validation of the JSON fields.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ValidationErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof UnrecognizedPropertyException unrecognizedEx) {
            String invalidField = unrecognizedEx.getPropertyName();
            String message = "Invalid JSON field: " + invalidField;

            ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                    List.of(message),
                    "Deserialization error. Invalid fields were provided.",
                    HttpStatus.BAD_REQUEST.value()
            );

            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Generic fallback if the cause isn't UnrecognizedPropertyException.
        ValidationErrorResponse fallbackResponse = new ValidationErrorResponse(
                List.of("Malformed JSON request."),
                "Deserialization error.",
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.badRequest().body(fallbackResponse);
    }

    // HTTP 400: Validation errors in request DTOs
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest().body(
                new ValidationErrorResponse(
                        errors,
                        "Validation failed for request body",
                        HttpStatus.BAD_REQUEST.value()
                )
        );
    }

    // Handle other generic exceptions.
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ValidationErrorResponse> handleRuntimeException(RuntimeException ex) {

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(List.of(ex.getMessage()),
                "Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}