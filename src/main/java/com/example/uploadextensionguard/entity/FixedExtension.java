package com.example.uploadextensionguard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fixed_extension")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FixedExtension extends BaseTimeEntity {

	@Id
	@Column(length = 20)
	private String extension;

	@Column(nullable = false)
	private boolean blocked = false;

	public FixedExtension(String extension) {
		this.extension = extension;
	}

	public void updateBlocked(boolean blocked) {
		this.blocked = blocked;
	}
}
