package com.eco.category.controller;

import com.eco.category.dto.CategoryResponse;
import com.eco.category.dto.CreateCategoryRequest;
import com.eco.category.dto.UpdateCategoryRequest;
import com.eco.category.service.CategoryService;
import com.eco.user.model.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponse> findAll(@AuthenticationPrincipal User user) {
        return categoryService.findAll(user);
    }

    @GetMapping("/{id}")
    public CategoryResponse findById(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        return categoryService.findById(id, user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@RequestBody @Valid CreateCategoryRequest request, @AuthenticationPrincipal User user) {
        return categoryService.create(request, user);
    }

    @PutMapping("/{id}")
    public CategoryResponse update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateCategoryRequest request,
            @AuthenticationPrincipal User user
    ) {
        return categoryService.update(id, request, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        categoryService.deactivate(id, user);
    }
}
