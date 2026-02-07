package com.example.uploadextensionguard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.uploadextensionguard.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ExtensionNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleExtensionNotFound(ExtensionNotFoundException e) {
		return ResponseEntity
			.status(HttpStatus.NOT_FOUND)
			.body(new ErrorResponse(e.getMessage()));
	}

	@ExceptionHandler(DuplicateExtensionException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateExtension(DuplicateExtensionException e) {
		return ResponseEntity
			.status(HttpStatus.CONFLICT)
			.body(new ErrorResponse(e.getMessage()));
	}

	@ExceptionHandler(MaxExtensionLimitException.class)
	public ResponseEntity<ErrorResponse> handleMaxExtensionLimit(MaxExtensionLimitException e) {
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorResponse(e.getMessage()));
	}

	@ExceptionHandler(InvalidExtensionException.class)
	public ResponseEntity<ErrorResponse> handleInvalidExtension(InvalidExtensionException e) {
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorResponse(e.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
		String message = e.getBindingResult().getFieldErrors().stream()
			.findFirst()
			.map(error -> error.getDefaultMessage())
			.orElse("잘못된 요청입니다");
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorResponse(message));
	}
}
