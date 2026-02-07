package com.example.uploadextensionguard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("파일 확장자 차단 API")
				.description("파일 업로드 시 특정 확장자를 차단하기 위한 API")
				.version("1.0.0"));
	}
}
