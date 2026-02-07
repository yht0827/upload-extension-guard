package com.example.uploadextensionguard.exception;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.uploadextensionguard.dto.ErrorResponse;

@DisplayName("GlobalExceptionHandler 테스트")
class GlobalExceptionHandlerTest {

	private GlobalExceptionHandler handler;

	@BeforeEach
	void setUp() {
		handler = new GlobalExceptionHandler();
	}

	@Nested
	@DisplayName("ExtensionNotFoundException")
	class HandleExtensionNotFound {

		@Test
		@DisplayName("404 응답 반환")
		void returns404() {
			// given
			ExtensionNotFoundException exception = new ExtensionNotFoundException("pdf");

			// when
			ResponseEntity<ErrorResponse> response = handler.handleExtensionNotFound(exception);

			// then
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
			assertThat(response.getBody()).isNotNull();
			assertThat(response.getBody().message()).contains("pdf");
		}

		@Test
		@DisplayName("Long id로 생성된 예외도 처리")
		void withLongId() {
			// given
			ExtensionNotFoundException exception = new ExtensionNotFoundException(999L);

			// when
			ResponseEntity<ErrorResponse> response = handler.handleExtensionNotFound(exception);

			// then
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
			assertThat(response.getBody().message()).contains("999");
		}
	}

	@Nested
	@DisplayName("DuplicateExtensionException")
	class HandleDuplicateExtension {

		@Test
		@DisplayName("409 응답 반환")
		void returns409() {
			// given
			DuplicateExtensionException exception = new DuplicateExtensionException("pdf");

			// when
			ResponseEntity<ErrorResponse> response = handler.handleDuplicateExtension(exception);

			// then
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
			assertThat(response.getBody()).isNotNull();
			assertThat(response.getBody().message()).contains("pdf");
		}
	}

	@Nested
	@DisplayName("MaxExtensionLimitException")
	class HandleMaxExtensionLimit {

		@Test
		@DisplayName("400 응답 반환")
		void returns400() {
			// given
			MaxExtensionLimitException exception = new MaxExtensionLimitException();

			// when
			ResponseEntity<ErrorResponse> response = handler.handleMaxExtensionLimit(exception);

			// then
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			assertThat(response.getBody()).isNotNull();
			assertThat(response.getBody().message()).contains("200");
		}
	}

	@Nested
	@DisplayName("InvalidExtensionException")
	class HandleInvalidExtension {

		@Test
		@DisplayName("400 응답 반환")
		void returns400() {
			// given
			InvalidExtensionException exception = new InvalidExtensionException("잘못된 형식입니다");

			// when
			ResponseEntity<ErrorResponse> response = handler.handleInvalidExtension(exception);

			// then
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			assertThat(response.getBody()).isNotNull();
			assertThat(response.getBody().message()).isEqualTo("잘못된 형식입니다");
		}
	}
}
