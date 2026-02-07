package com.example.uploadextensionguard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomExtensionRequest {

	@NotBlank(message = "확장자를 입력해주세요")
	@Size(max = 20, message = "확장자는 20자 이하로 입력해주세요")
	private String extension;
}
