package com.eco.category.service;

import com.eco.category.dto.CategoryResponse;
import com.eco.category.dto.CreateCategoryRequest;
import com.eco.category.dto.UpdateCategoryRequest;
import com.eco.category.model.Category;
import com.eco.category.repository.CategoryRepository;
import com.eco.user.model.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import com.eco.common.exception.BusinessException;
import com.eco.common.exception.NotFoundException;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Retorna todas as categorias do banco,
    // transforma a lista em fluxo de dados,
    // para cada categoria do banco, monta um DTO de resposta
    // transforma o resultado final de volta em uma lista
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll(User user) {
        return  categoryRepository.findAllByUserId(user.getId())
                .stream()
                .map(CategoryResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse findById(UUID id, User user) {
        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Categoria nao encontrada"));

        return CategoryResponse.fromEntity(category);
    }
    @Transactional
    public CategoryResponse create(CreateCategoryRequest request, User user){
        if (categoryRepository.existsByNameIgnoreCaseAndUserId(request.getName(), user.getId())) {
            throw new BusinessException("Categoria ja existe");
        }
        Category category = new Category(request.getName(), request.getKind(), request.getColor(), request.getIcon(), user);

        Category savedCategory = categoryRepository.save(category);

        return CategoryResponse.fromEntity(savedCategory);
    }

    @Transactional
    public CategoryResponse update(UUID id, UpdateCategoryRequest request, User user) {
        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Categoria nao encontrada"));

        categoryRepository.findByNameIgnoreCaseAndUserId(request.getName(), user.getId())
                .filter(existingCategory -> !existingCategory.getId().equals(id))
                .ifPresent(existingCategory -> {
                    throw new BusinessException("Categoria ja existe");
                });

        category.setName(request.getName());
        category.setKind(request.getKind());
        category.setColor(request.getColor());
        category.setIcon(request.getIcon());
        category.setActive(request.getActive());

        Category savedCategory = categoryRepository.save(category);

        return CategoryResponse.fromEntity(savedCategory);
    }

    @Transactional
    public void deactivate(UUID id, User user) {
        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Categoria nao encontrada"));

        category.setActive(false);

        categoryRepository.save(category);
    }

    
}
