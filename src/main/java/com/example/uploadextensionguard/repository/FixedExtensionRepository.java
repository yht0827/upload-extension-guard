package com.example.uploadextensionguard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.uploadextensionguard.entity.FixedExtension;

public interface FixedExtensionRepository extends JpaRepository<FixedExtension, String> {

	List<FixedExtension> findAllByOrderByExtensionAsc();
}
