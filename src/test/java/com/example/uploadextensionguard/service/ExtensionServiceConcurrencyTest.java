package com.example.uploadextensionguard.service;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.uploadextensionguard.dto.CustomExtensionRequest;
import com.example.uploadextensionguard.entity.CustomExtension;
import com.example.uploadextensionguard.entity.ExtensionLock;
import com.example.uploadextensionguard.repository.CustomExtensionRepository;
import com.example.uploadextensionguard.repository.ExtensionLockRepository;

@SpringBootTest
@DisplayName("ExtensionService 동시성 테스트")
class ExtensionServiceConcurrencyTest {

	@Autowired
	private ExtensionService extensionService;

	@Autowired
	private CustomExtensionRepository customExtensionRepository;

	@Autowired
	private ExtensionLockRepository extensionLockRepository;

	@BeforeEach
	void setUp() {
		customExtensionRepository.deleteAll();
		if (!extensionLockRepository.existsById("LOCK")) {
			extensionLockRepository.save(new ExtensionLock("LOCK"));
		}
	}

	@Test
	@DisplayName("동시에 같은 확장자 추가 시 하나만 성공")
	void duplicateAddConcurrency() throws InterruptedException {
		// given
		int threadCount = 10;
		String extension = "concurrent";
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failCount = new AtomicInteger(0);
		List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

		// when
		for (int i = 0; i < threadCount; i++) {
			executor.submit(() -> {
				try {
					extensionService.addCustomExtension(new CustomExtensionRequest(extension));
					successCount.incrementAndGet();
				} catch (Exception e) {
					failCount.incrementAndGet();
					exceptions.add(e);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executor.shutdown();

		// then
		assertThat(successCount.get()).isEqualTo(1);
		assertThat(failCount.get()).isEqualTo(threadCount - 1);
		assertThat(customExtensionRepository.existsByExtension(extension)).isTrue();
		assertThat(customExtensionRepository.findAllByOrderByCreatedAtDesc()
			.stream()
			.filter(e -> e.getExtension().equals(extension))
			.count()).isEqualTo(1);
	}

	@Test
	@DisplayName("200개 제한 동시 추가 - 비관적 락으로 제한 유지")
	void maxLimitConcurrency() throws InterruptedException {
		// given - 195개 미리 추가
		for (int i = 0; i < 195; i++) {
			customExtensionRepository.save(CustomExtension.create("pre" + i));
		}

		int threadCount = 10;
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failCount = new AtomicInteger(0);

		// when - 동시에 10개 추가 시도 (195 + 10 = 205 > 200)
		for (int i = 0; i < threadCount; i++) {
			final int index = i;
			executor.submit(() -> {
				try {
					extensionService.addCustomExtension(new CustomExtensionRequest("new" + index));
					successCount.incrementAndGet();
				} catch (Exception e) {
					failCount.incrementAndGet();
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executor.shutdown();

		// then
		assertThat(successCount.get() + failCount.get()).isEqualTo(threadCount);
		assertThat(successCount.get()).isEqualTo(5); // 195 + 5 = 200
		assertThat(customExtensionRepository.count()).isEqualTo(200);
	}
}
