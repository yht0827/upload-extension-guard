package com.example.uploadextensionguard.exception;

public class ExtensionNotFoundException extends RuntimeException {

	public ExtensionNotFoundException(String extension) {
		super("존재하지 않는 확장자입니다: " + extension);
	}

	public ExtensionNotFoundException(Long id) {
		super("존재하지 않는 확장자입니다. ID: " + id);
	}
}
