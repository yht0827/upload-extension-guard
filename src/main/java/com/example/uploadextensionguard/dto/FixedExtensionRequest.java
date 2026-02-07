package com.example.uploadextensionguard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FixedExtensionRequest {

	@NotBlank(message = "확장자를 입력해주세요")
	private String extension;

	@NotNull(message = "blocked 값은 필수입니다")
	private Boolean blocked;
}
