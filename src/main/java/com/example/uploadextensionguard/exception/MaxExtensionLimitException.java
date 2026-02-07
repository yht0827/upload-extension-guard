package com.example.uploadextensionguard.exception;

public class MaxExtensionLimitException extends RuntimeException {

	public MaxExtensionLimitException() {
		super("커스텀 확장자는 최대 200개까지 등록 가능합니다");
	}
}
