package com.example.uploadextensionguard.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.uploadextensionguard.dto.CustomExtensionRequest;
import com.example.uploadextensionguard.dto.ExtensionResponse.CustomExtensionDto;
import com.example.uploadextensionguard.entity.CustomExtension;
import com.example.uploadextensionguard.exception.DuplicateExtensionException;
import com.example.uploadextensionguard.exception.ExtensionNotFoundException;
import com.example.uploadextensionguard.exception.InvalidExtensionException;
import com.example.uploadextensionguard.exception.MaxExtensionLimitException;
import com.example.uploadextensionguard.repository.CustomExtensionRepository;
import com.example.uploadextensionguard.repository.FixedExtensionRepository;

@SpringBootTest
@Transactional
class ExtensionServiceTest {

	@Autowired
	private ExtensionService extensionService;

	@Autowired
	private CustomExtensionRepository customExtensionRepository;

	@Autowired
	private FixedExtensionRepository fixedExtensionRepository;

	@BeforeEach
	void setUp() {
		customExtensionRepository.deleteAll();
	}

	@Nested
	@DisplayName("커스텀 확장자 추가")
	class AddCustomExtension {

		@Test
		@DisplayName("정상적인 확장자 추가")
		void success() {
			// given
			CustomExtensionRequest request = createRequest("pdf");

			// when
			CustomExtensionDto result = extensionService.addCustomExtension(request);

			// then
			assertThat(result.getExtension()).isEqualTo("pdf");
			assertThat(result.getWarning()).isNull();
			assertThat(customExtensionRepository.existsByExtension("pdf")).isTrue();
		}

		@Test
		@DisplayName("위험 확장자 추가 시 경고 메시지 포함")
		void dangerousExtension_returnsWarning() {
			// given
			CustomExtensionRequest request = createRequest("php");

			// when
			CustomExtensionDto result = extensionService.addCustomExtension(request);

			// then
			assertThat(result.getExtension()).isEqualTo("php");
			assertThat(result.getWarning()).contains("서버 실행 가능한 확장자");
		}

		@Test
		@DisplayName("중복 확장자 추가 시 예외")
		void duplicate_throwsException() {
			// given
			extensionService.addCustomExtension(createRequest("pdf"));
			CustomExtensionRequest duplicateRequest = createRequest("pdf");

			// when & then
			assertThatThrownBy(() -> extensionService.addCustomExtension(duplicateRequest))
				.isInstanceOf(DuplicateExtensionException.class)
				.hasMessageContaining("pdf");
		}

		@Test
		@DisplayName("고정 확장자와 충돌 시 예외")
		void conflictWithFixed_throwsException() {
			// given
			CustomExtensionRequest request = createRequest("exe");

			// when & then
			assertThatThrownBy(() -> extensionService.addCustomExtension(request))
				.isInstanceOf(DuplicateExtensionException.class)
				.hasMessageContaining("exe");
		}

		@Test
		@DisplayName("잘못된 형식 - 특수문자 포함 시 예외")
		void invalidFormat_specialChar() {
			// given
			CustomExtensionRequest request = createRequest("test@ext");

			// when & then
			assertThatThrownBy(() -> extensionService.addCustomExtension(request))
				.isInstanceOf(InvalidExtensionException.class)
				.hasMessageContaining("영문 소문자와 숫자만");
		}

		@Test
		@DisplayName("잘못된 형식 - 공백 포함 시 예외")
		void invalidFormat_withSpace() {
			// given
			CustomExtensionRequest request = createRequest("te st");

			// when & then
			assertThatThrownBy(() -> extensionService.addCustomExtension(request))
				.isInstanceOf(InvalidExtensionException.class);
		}

		@Test
		@DisplayName("최대 개수(200개) 초과 시 예외")
		void maxLimit_throwsException() {
			// given - 200개 추가
			for (int i = 0; i < 200; i++) {
				customExtensionRepository.save(CustomExtension.create("ext" + i));
			}

			CustomExtensionRequest request = createRequest("ext200");

			// when & then
			assertThatThrownBy(() -> extensionService.addCustomExtension(request))
				.isInstanceOf(MaxExtensionLimitException.class);
		}
	}

	@Nested
	@DisplayName("커스텀 확장자 삭제")
	class DeleteCustomExtension {

		@Test
		@DisplayName("정상 삭제")
		void success() {
			// given
			CustomExtensionDto created = extensionService.addCustomExtension(createRequest("pdf"));
			assertThat(customExtensionRepository.existsByExtension("pdf")).isTrue();

			// when
			extensionService.deleteCustomExtension(created.getId());

			// then
			assertThat(customExtensionRepository.existsByExtension("pdf")).isFalse();
		}

		@Test
		@DisplayName("존재하지 않는 확장자 삭제 시 예외")
		void notFound_throwsException() {
			// when & then
			assertThatThrownBy(() -> extensionService.deleteCustomExtension(999L))
				.isInstanceOf(ExtensionNotFoundException.class)
				.hasMessageContaining("존재하지 않는");
		}
	}

	@Nested
	@DisplayName("고정 확장자 상태 변경")
	class UpdateFixedExtension {

		@Test
		@DisplayName("차단 상태로 변경")
		void updateToBlocked() {
			// when
			extensionService.updateFixedExtension("exe", true);

			// then
			var fixed = fixedExtensionRepository.findById("exe").orElseThrow();
			assertThat(fixed.isBlocked()).isTrue();
		}

		@Test
		@DisplayName("차단 해제 상태로 변경")
		void updateToUnblocked() {
			// given
			extensionService.updateFixedExtension("exe", true);

			// when
			extensionService.updateFixedExtension("exe", false);

			// then
			var fixed = fixedExtensionRepository.findById("exe").orElseThrow();
			assertThat(fixed.isBlocked()).isFalse();
		}

		@Test
		@DisplayName("존재하지 않는 고정 확장자 변경 시 예외")
		void notFound_throwsException() {
			// when & then
			assertThatThrownBy(() -> extensionService.updateFixedExtension("notexist", true))
				.isInstanceOf(ExtensionNotFoundException.class);
		}
	}

	private CustomExtensionRequest createRequest(String extension) {
		return new CustomExtensionRequest(extension);
	}
}