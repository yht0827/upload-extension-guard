package com.example.uploadextensionguard.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.uploadextensionguard.dto.CustomExtensionRequest;
import com.example.uploadextensionguard.dto.ExtensionResponse;
import com.example.uploadextensionguard.dto.ExtensionResponse.CustomExtensionDto;
import com.example.uploadextensionguard.dto.FixedExtensionRequest;
import com.example.uploadextensionguard.service.ExtensionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/extensions")
@RequiredArgsConstructor
public class ExtensionController {

	private final ExtensionService extensionService;

	@GetMapping
	public ResponseEntity<ExtensionResponse> getAllExtensions() {
		return ResponseEntity.ok(extensionService.getAllExtensions());
	}

	@PatchMapping("/fixed")
	public ResponseEntity<Void> updateFixedExtension(
		@Valid @RequestBody FixedExtensionRequest request) {
		extensionService.updateFixedExtension(request.getExtension(), request.getBlocked());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/custom")
	public ResponseEntity<CustomExtensionDto> addCustomExtension(
		@Valid @RequestBody CustomExtensionRequest request) {
		CustomExtensionDto created = extensionService.addCustomExtension(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@DeleteMapping("/custom/{id}")
	public ResponseEntity<Void> deleteCustomExtension(@PathVariable Long id) {
		extensionService.deleteCustomExtension(id);
		return ResponseEntity.noContent().build();
	}
}
