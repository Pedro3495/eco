package com.eco.category.dto;

import com.eco.category.model.CategoryKind;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateCategoryRequest {

    @NotBlank
    @Size(max = 80)
    private String name;

    @NotNull
    private CategoryKind kind;

    @Size(max = 20)
    private String color;

    @Size(max = 50)
    private String icon;

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
}