package com.example.uploadextensionguard.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.uploadextensionguard.entity.ExtensionLock;
import com.example.uploadextensionguard.entity.FixedExtension;
import com.example.uploadextensionguard.repository.ExtensionLockRepository;
import com.example.uploadextensionguard.repository.FixedExtensionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

	private static final List<String> FIXED_EXTENSIONS = List.of(
		"bat", "cmd", "com", "cpl", "exe", "scr", "js"
	);

	private final FixedExtensionRepository fixedExtensionRepository;
	private final ExtensionLockRepository extensionLockRepository;

	@Override
	@Transactional
	public void run(String... args) {
		initializeFixedExtensions();
		initializeExtensionLock();
	}

	private void initializeFixedExtensions() {
		if (fixedExtensionRepository.count() == 0) {
			log.info("Initializing fixed extensions...");
			List<FixedExtension> extensions = FIXED_EXTENSIONS.stream()
				.map(FixedExtension::new)
				.toList();
			fixedExtensionRepository.saveAll(extensions);
			log.info("Fixed extensions initialized: {}", FIXED_EXTENSIONS);
		}
	}

	private void initializeExtensionLock() {
		if (!extensionLockRepository.existsById("LOCK")) {
			log.info("Initializing extension lock...");
			extensionLockRepository.save(new ExtensionLock("LOCK"));
			log.info("Extension lock initialized");
		}
	}
}
