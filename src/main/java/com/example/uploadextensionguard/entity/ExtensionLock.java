package com.example.uploadextensionguard.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 커스텀 확장자 추가 시 동시성 제어를 위한 락 테이블.
 * 단일 레코드를 비관적 락으로 잠가서 200개 제한을 보장함.
 */
@Entity
@Table(name = "extension_lock")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExtensionLock {

	@Id
	private String id = "LOCK";

	public ExtensionLock(String id) {
		this.id = id;
	}
}
