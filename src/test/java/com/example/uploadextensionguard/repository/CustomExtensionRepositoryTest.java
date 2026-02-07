package com.example.uploadextensionguard.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.example.uploadextensionguard.config.JpaConfig;
import com.example.uploadextensionguard.entity.CustomExtension;

@DataJpaTest
@Import(JpaConfig.class)
@DisplayName("CustomExtensionRepository 테스트")
class CustomExtensionRepositoryTest {

	@Autowired
	private CustomExtensionRepository customExtensionRepository;

	@Nested
	@DisplayName("existsByExtension")
	class ExistsByExtension {

		@Test
		@DisplayName("존재하는 확장자 - true")
		void exists() {
			// given
			customExtensionRepository.save(CustomExtension.create("pdf"));

			// when & then
			assertThat(customExtensionRepository.existsByExtension("pdf")).isTrue();
		}

		@Test
		@DisplayName("존재하지 않는 확장자 - false")
		void notExists() {
			// when & then
			assertThat(customExtensionRepository.existsByExtension("notexist")).isFalse();
		}
	}

	@Nested
	@DisplayName("findAllByOrderByCreatedAtDesc")
	class FindAllByOrderByCreatedAtDesc {

		@Test
		@DisplayName("생성일 내림차순 정렬")
		void orderedByCreatedAtDesc() throws InterruptedException {
			// given
			customExtensionRepository.save(CustomExtension.create("first"));
			Thread.sleep(10);
			customExtensionRepository.save(CustomExtension.create("second"));
			Thread.sleep(10);
			customExtensionRepository.save(CustomExtension.create("third"));

			// when
			List<CustomExtension> result = customExtensionRepository.findAllByOrderByCreatedAtDesc();

			// then
			assertThat(result).hasSize(3);
			assertThat(result.get(0).getExtension()).isEqualTo("third");
			assertThat(result.get(1).getExtension()).isEqualTo("second");
			assertThat(result.get(2).getExtension()).isEqualTo("first");
		}

		@Test
		@DisplayName("데이터 없을 때 빈 리스트")
		void emptyList() {
			// when
			List<CustomExtension> result = customExtensionRepository.findAllByOrderByCreatedAtDesc();

			// then
			assertThat(result).isEmpty();
		}
	}

	@Nested
	@DisplayName("count")
	class Count {

		@Test
		@DisplayName("저장된 개수 반환")
		void countExtensions() {
			// given
			customExtensionRepository.save(CustomExtension.create("pdf"));
			customExtensionRepository.save(CustomExtension.create("doc"));
			customExtensionRepository.save(CustomExtension.create("xls"));

			// when & then
			assertThat(customExtensionRepository.count()).isEqualTo(3);
		}
	}
}
