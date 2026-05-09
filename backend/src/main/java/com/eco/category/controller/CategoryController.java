package com.eco.category.controller;


import com.eco.category.dto.CategoryResponse;
import com.eco.category.dto.CreateCategoryRequest;
import com.eco.category.dto.UpdateCategoryRequest;
import com.eco.category.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    // CategoryController precisa de CategoryService para acessar os serviços.\mvnw.cmd test
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponse> findAll() {
        return categoryService.findAll();
    }

    @GetMapping("/{id}")
    public CategoryResponse findById(@PathVariable UUID id) {
        return categoryService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@RequestBody @Valid CreateCategoryRequest request) {
        return categoryService.create(request);
    }

    @PutMapping("/{id}")
    public CategoryResponse update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateCategoryRequest request
    ) {
        return categoryService.update(id, request);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable UUID id) {
        categoryService.deactivate(id);
    }

}
