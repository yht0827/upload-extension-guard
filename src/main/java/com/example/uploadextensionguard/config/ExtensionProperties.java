package com.example.uploadextensionguard.config;

import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "extension")
public record ExtensionProperties(
	int maxCustom,
	Set<String> dangerous,
	String dangerousWarning
) {
	public boolean isDangerous(String extension) {
		return dangerous.contains(extension);
	}
}
