package com.eco.category.service;

import com.eco.category.dto.CategoryResponse;
import com.eco.category.dto.CreateCategoryRequest;
import com.eco.category.model.Category;
import com.eco.category.model.CategoryKind;
import com.eco.category.repository.CategoryRepository;
import com.eco.common.exception.BusinessException;
import com.eco.common.exception.NotFoundException;
import com.eco.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private final User user = new User("Usuario Dev", "dev@eco.com", "hash");

    @Test
    void findAllShouldReturnCategories() {
        Category category = new Category("Alimentacao", CategoryKind.EXPENSE, "#E86F51", "utensils", user);

        when(categoryRepository.findAllByUserId(user.getId())).thenReturn(List.of(category));

        List<CategoryResponse> response = categoryService.findAll(user);

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().getName()).isEqualTo("Alimentacao");
        assertThat(response.getFirst().getKind()).isEqualTo(CategoryKind.EXPENSE);
    }

    @Test
    void findByIdShouldReturnCategoryWhenExists() {
        UUID id = UUID.randomUUID();
        Category category = new Category("Alimentacao", CategoryKind.EXPENSE, "#E86F51", "utensils", user);
        category.setId(id);

        when(categoryRepository.findByIdAndUserId(id, user.getId())).thenReturn(Optional.of(category));

        CategoryResponse response = categoryService.findById(id, user);

        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getName()).isEqualTo("Alimentacao");
    }

    @Test
    void findByIdShouldThrowNotFoundWhenCategoryDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(categoryRepository.findByIdAndUserId(id, user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(id, user))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Categoria nao encontrada");
    }

    @Test
    void createShouldSaveCategoryWhenNameDoesNotExist() {
        CreateCategoryRequest request = createCategoryRequest();

        when(categoryRepository.existsByNameIgnoreCaseAndUserId("Alimentacao", user.getId())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryResponse response = categoryService.create(request, user);

        assertThat(response.getName()).isEqualTo("Alimentacao");
        assertThat(response.getKind()).isEqualTo(CategoryKind.EXPENSE);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createShouldThrowBusinessExceptionWhenNameAlreadyExists() {
        CreateCategoryRequest request = createCategoryRequest();

        when(categoryRepository.existsByNameIgnoreCaseAndUserId("Alimentacao", user.getId())).thenReturn(true);

        assertThatThrownBy(() -> categoryService.create(request, user))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Categoria ja existe");

        verify(categoryRepository, never()).save(any(Category.class));
    }

    private CreateCategoryRequest createCategoryRequest() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        ReflectionTestUtils.setField(request, "name", "Alimentacao");
        ReflectionTestUtils.setField(request, "kind", CategoryKind.EXPENSE);
        ReflectionTestUtils.setField(request, "color", "#E86F51");
        ReflectionTestUtils.setField(request, "icon", "utensils");
        return request;
    }
}
