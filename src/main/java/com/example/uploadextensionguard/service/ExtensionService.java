package com.example.uploadextensionguard.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.uploadextensionguard.dto.CustomExtensionRequest;
import com.example.uploadextensionguard.dto.ExtensionResponse;
import com.example.uploadextensionguard.dto.ExtensionResponse.CustomExtensionDto;
import com.example.uploadextensionguard.dto.ExtensionResponse.FixedExtensionDto;
import com.example.uploadextensionguard.entity.CustomExtension;
import com.example.uploadextensionguard.entity.FixedExtension;
import com.example.uploadextensionguard.exception.DuplicateExtensionException;
import com.example.uploadextensionguard.exception.ExtensionNotFoundException;
import com.example.uploadextensionguard.exception.MaxExtensionLimitException;
import com.example.uploadextensionguard.repository.CustomExtensionRepository;
import com.example.uploadextensionguard.repository.FixedExtensionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExtensionService {

	private static final int MAX_CUSTOM_EXTENSIONS = 200;

	private final FixedExtensionRepository fixedExtensionRepository;
	private final CustomExtensionRepository customExtensionRepository;

	public ExtensionResponse getAllExtensions() {
		List<FixedExtensionDto> fixed = fixedExtensionRepository.findAllByOrderByExtensionAsc()
			.stream()
			.map(e -> new FixedExtensionDto(e.getExtension(), e.isBlocked()))
			.toList();

		List<CustomExtensionDto> custom = customExtensionRepository.findAllByOrderByCreatedAtDesc()
			.stream()
			.map(e -> new CustomExtensionDto(e.getId(), e.getExtension()))
			.toList();

		return new ExtensionResponse(fixed, custom);
	}

	@Transactional
	public void updateFixedExtension(String extension, boolean blocked) {
		FixedExtension fixedExtension = fixedExtensionRepository.findById(extension)
			.orElseThrow(() -> new ExtensionNotFoundException(extension));
		fixedExtension.updateBlocked(blocked); // JPA dirty checking
	}

	@Transactional
	public CustomExtensionDto addCustomExtension(CustomExtensionRequest request) {
		CustomExtension customExtension = CustomExtension.create(request.getExtension());

		checkDuplicate(customExtension.getExtension());
		checkMaxLimit();

		try {
			CustomExtension saved = customExtensionRepository.save(customExtension);
			return new CustomExtensionDto(saved.getId(), saved.getExtension());
		} catch (DataIntegrityViolationException e) {
			if (e.getMessage() != null && e.getMessage().toLowerCase().contains("unique")) {
				throw new DuplicateExtensionException(customExtension.getExtension());
			}
			throw e;
		}
	}

	@Transactional
	public void deleteCustomExtension(Long id) {
		CustomExtension customExtension = customExtensionRepository.findById(id)
			.orElseThrow(() -> new ExtensionNotFoundException(id));
		customExtensionRepository.delete(customExtension);
	}

	private void checkDuplicate(String extension) {
		if (fixedExtensionRepository.existsById(extension)) {
			throw new DuplicateExtensionException(extension);
		}
		if (customExtensionRepository.existsByExtension(extension)) {
			throw new DuplicateExtensionException(extension);
		}
	}

	private void checkMaxLimit() {
		if (customExtensionRepository.count() >= MAX_CUSTOM_EXTENSIONS) {
			throw new MaxExtensionLimitException();
		}
	}
}
