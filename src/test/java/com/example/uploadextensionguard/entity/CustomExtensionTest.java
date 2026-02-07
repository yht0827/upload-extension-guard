package com.example.uploadextensionguard.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.uploadextensionguard.exception.InvalidExtensionException;

@DisplayName("CustomExtension 도메인 테스트")
class CustomExtensionTest {

	@Nested
	@DisplayName("create - 정상 생성")
	class Create {

		@Test
		@DisplayName("정상적인 확장자 생성")
		void success() {
			// when
			CustomExtension extension = CustomExtension.create("pdf");

			// then
			assertThat(extension.getExtension()).isEqualTo("pdf");
		}

		@Test
		@DisplayName("숫자 포함 확장자 생성")
		void withNumber() {
			// when
			CustomExtension extension = CustomExtension.create("mp3");

			// then
			assertThat(extension.getExtension()).isEqualTo("mp3");
		}
	}

	@Nested
	@DisplayName("normalizeExtension - 정규화")
	class NormalizeExtension {

		@Test
		@DisplayName("대문자를 소문자로 변환")
		void toLowerCase() {
			assertThat(CustomExtension.normalizeExtension("PDF")).isEqualTo("pdf");
			assertThat(CustomExtension.normalizeExtension("Pdf")).isEqualTo("pdf");
		}

		@Test
		@DisplayName("앞뒤 공백 제거")
		void trim() {
			assertThat(CustomExtension.normalizeExtension("  pdf  ")).isEqualTo("pdf");
		}

		@Test
		@DisplayName("앞의 점(.) 제거")
		void removeDot() {
			assertThat(CustomExtension.normalizeExtension(".pdf")).isEqualTo("pdf");
		}

		@Test
		@DisplayName("복합 정규화")
		void combined() {
			assertThat(CustomExtension.normalizeExtension("  .PDF  ")).isEqualTo("pdf");
		}

		@Test
		@DisplayName("null 입력 시 빈 문자열 반환")
		void nullInput() {
			assertThat(CustomExtension.normalizeExtension(null)).isEqualTo("");
		}
	}

	@Nested
	@DisplayName("validate - 검증 실패")
	class ValidateFailure {

		@ParameterizedTest
		@NullAndEmptySource
		@ValueSource(strings = {"  ", ".", " . "})
		@DisplayName("빈 확장자 시 예외")
		void empty(String input) {
			assertThatThrownBy(() -> CustomExtension.create(input))
				.isInstanceOf(InvalidExtensionException.class)
				.hasMessageContaining("확장자를 입력해주세요");
		}

		@Test
		@DisplayName("20자 초과 시 예외")
		void tooLong() {
			String longExtension = "a".repeat(21);

			assertThatThrownBy(() -> CustomExtension.create(longExtension))
				.isInstanceOf(InvalidExtensionException.class)
				.hasMessageContaining("20자 이하");
		}

		@ParameterizedTest
		@ValueSource(strings = {"test@ext", "test.ext", "test ext", "한글", "test!", "test#"})
		@DisplayName("영문 소문자/숫자 외 문자 포함 시 예외")
		void invalidPattern(String input) {
			assertThatThrownBy(() -> CustomExtension.create(input))
				.isInstanceOf(InvalidExtensionException.class)
				.hasMessageContaining("영문 소문자와 숫자만");
		}
	}

	@Nested
	@DisplayName("validate - 경계값")
	class ValidateBoundary {

		@Test
		@DisplayName("20자 확장자는 유효")
		void maxLength() {
			String maxLengthExtension = "a".repeat(20);

			CustomExtension extension = CustomExtension.create(maxLengthExtension);

			assertThat(extension.getExtension()).hasSize(20);
		}

		@Test
		@DisplayName("1자 확장자는 유효")
		void minLength() {
			CustomExtension extension = CustomExtension.create("a");

			assertThat(extension.getExtension()).isEqualTo("a");
		}
	}
}
