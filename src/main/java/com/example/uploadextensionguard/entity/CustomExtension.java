package com.example.uploadextensionguard.entity;

import java.util.regex.Pattern;

import com.example.uploadextensionguard.exception.InvalidExtensionException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "custom_extension")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomExtension extends BaseTimeEntity {

	private static final int MAX_LENGTH = 20;
	private static final Pattern EXTENSION_PATTERN = Pattern.compile("^[a-z0-9]+$");

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 20, unique = true, nullable = false)
	private String extension;

	private CustomExtension(String extension) {
		this.extension = normalizeExtension(extension);
		validate(this.extension);
	}

	public static CustomExtension create(String extension) {
		return new CustomExtension(extension);
	}

	public static String normalizeExtension(String extension) {
		if (extension == null) {
			return "";
		}
		String normalized = extension.trim().toLowerCase();
		if (normalized.startsWith(".")) {
			normalized = normalized.substring(1);
		}
		return normalized;
	}

	private void validate(String extension) {
		if (extension.isEmpty()) {
			throw new InvalidExtensionException("확장자를 입력해주세요");
		}
		if (extension.length() > MAX_LENGTH) {
			throw new InvalidExtensionException("확장자는 20자 이하로 입력해주세요");
		}
		if (!EXTENSION_PATTERN.matcher(extension).matches()) {
			throw new InvalidExtensionException("영문 소문자와 숫자만 입력 가능합니다");
		}
	}
}
