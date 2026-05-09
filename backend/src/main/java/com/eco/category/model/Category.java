package com.eco.category.model;


import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="categories")
public class Category {
    @Id
    private UUID id;
    @Column(nullable=false, length = 80,unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoryKind kind;

    @Column(length = 20)
    private String color;

    @Column(length = 50)
    private String icon;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name="created_at",nullable = false)
    private Instant createdAt;

    @Column(name="updated_at",nullable = false)
    private Instant updatedAt;

    protected Category() {}

    public Category(String name, CategoryKind kind, String color, String icon) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.kind = kind;
        this.color = color;
        this.icon = icon;
        this.active = true;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryKind getKind() {
        return kind;
    }

    public void setKind(CategoryKind kind) {
        this.kind = kind;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
