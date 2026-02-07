package com.example.uploadextensionguard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.example.uploadextensionguard.entity.ExtensionLock;

import jakarta.persistence.LockModeType;

public interface ExtensionLockRepository extends JpaRepository<ExtensionLock, String> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT e FROM ExtensionLock e WHERE e.id = 'LOCK'")
	ExtensionLock acquireLock();
}
