package com.eco.category.repository;

import com.eco.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findAllByUserId(UUID userId);
    Optional<Category> findByIdAndUserId(UUID id, UUID userId);
    Optional<Category> findByNameIgnoreCaseAndUserId(String name, UUID userId);
    boolean existsByNameIgnoreCaseAndUserId(String name, UUID userId);
}
