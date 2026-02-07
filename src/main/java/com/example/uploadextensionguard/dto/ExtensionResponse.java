package com.example.uploadextensionguard.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExtensionResponse {

	private List<FixedExtensionDto> fixed;
	private List<CustomExtensionDto> custom;

	@Getter
	@AllArgsConstructor
	public static class FixedExtensionDto {
		private String extension;
		private boolean blocked;
	}

	@Getter
	public static class CustomExtensionDto {
		private Long id;
		private String extension;
		private String warning;

		public CustomExtensionDto(Long id, String extension) {
			this.id = id;
			this.extension = extension;
		}

		public CustomExtensionDto(Long id, String extension, String warning) {
			this.id = id;
			this.extension = extension;
			this.warning = warning;
		}
	}
}
