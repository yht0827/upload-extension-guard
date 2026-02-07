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
import com.example.uploadextensionguard.dto.ErrorResponse;
import com.example.uploadextensionguard.dto.ExtensionResponse;
import com.example.uploadextensionguard.dto.ExtensionResponse.CustomExtensionDto;
import com.example.uploadextensionguard.dto.FixedExtensionRequest;
import com.example.uploadextensionguard.service.ExtensionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Extension", description = "파일 확장자 차단 API")
@RestController
@RequestMapping("/api/extensions")
@RequiredArgsConstructor
public class ExtensionController {

	private final ExtensionService extensionService;

	@Operation(summary = "전체 확장자 조회", description = "고정 확장자와 커스텀 확장자 목록을 조회합니다")
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@GetMapping
	public ResponseEntity<ExtensionResponse> getAllExtensions() {
		return ResponseEntity.ok(extensionService.getAllExtensions());
	}

	@Operation(summary = "고정 확장자 차단 상태 변경", description = "고정 확장자의 차단 여부를 토글합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "변경 성공"),
		@ApiResponse(responseCode = "404", description = "확장자를 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PatchMapping("/fixed")
	public ResponseEntity<Void> updateFixedExtension(
		@Valid @RequestBody FixedExtensionRequest request) {
		extensionService.updateFixedExtension(request.getExtension(), request.getBlocked());
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "커스텀 확장자 추가", description = "새로운 커스텀 확장자를 추가합니다 (최대 200개)")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "추가 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 (형식 오류, 200개 초과)",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "409", description = "중복된 확장자",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PostMapping("/custom")
	public ResponseEntity<CustomExtensionDto> addCustomExtension(
		@Valid @RequestBody CustomExtensionRequest request) {
		CustomExtensionDto created = extensionService.addCustomExtension(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@Operation(summary = "커스텀 확장자 삭제", description = "커스텀 확장자를 삭제합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "삭제 성공"),
		@ApiResponse(responseCode = "404", description = "확장자를 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@DeleteMapping("/custom/{id}")
	public ResponseEntity<Void> deleteCustomExtension(@PathVariable Long id) {
		extensionService.deleteCustomExtension(id);
		return ResponseEntity.noContent().build();
	}
}
