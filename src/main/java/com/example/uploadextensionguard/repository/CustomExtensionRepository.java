package com.example.uploadextensionguard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.uploadextensionguard.entity.CustomExtension;

public interface CustomExtensionRepository extends JpaRepository<CustomExtension, Long> {

	boolean existsByExtension(String extension);

	List<CustomExtension> findAllByOrderByCreatedAtDesc();
}
