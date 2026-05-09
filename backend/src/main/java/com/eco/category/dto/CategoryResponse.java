package com.eco.category.dto;

import com.eco.category.model.Category;
import com.eco.category.model.CategoryKind;

import java.util.UUID;

public class CategoryResponse {

    private UUID id;
    private String name;
    private CategoryKind kind;
    private String color;
    private String icon;
    private boolean active;

    public CategoryResponse(UUID id, String name, CategoryKind kind, String color, String icon, boolean active) {
        this.id = id;
        this.name = name;
        this.kind = kind;
        this.color = color;
        this.icon = icon;
        this.active = active;
    }

    public static CategoryResponse fromEntity(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getKind(),
                category.getColor(),
                category.getIcon(),
                category.isActive()
        );
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CategoryKind getKind() {
        return kind;
    }

    public String getColor() {
        return color;
    }

    public String getIcon() {
        return icon;
    }

    public boolean isActive() {
        return active;
    }
}
