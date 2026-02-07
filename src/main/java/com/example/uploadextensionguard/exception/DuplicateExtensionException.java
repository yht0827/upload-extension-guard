package com.example.uploadextensionguard.exception;

public class DuplicateExtensionException extends RuntimeException {

	public DuplicateExtensionException(String extension) {
		super("이미 등록된 확장자입니다: " + extension);
	}
}
