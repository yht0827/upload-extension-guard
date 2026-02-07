package com.example.uploadextensionguard.entity;

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

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 20, unique = true, nullable = false)
	private String extension;

	public CustomExtension(String extension) {
		this.extension = extension;
	}
}
