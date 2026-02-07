package com.example.uploadextensionguard.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.uploadextensionguard.entity.CustomExtension;

public interface CustomExtensionRepository extends JpaRepository<CustomExtension, Long> {

	Optional<CustomExtension> findByExtension(String extension);

	boolean existsByExtension(String extension);
}
