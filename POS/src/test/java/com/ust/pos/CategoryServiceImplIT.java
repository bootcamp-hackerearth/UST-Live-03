package com.ust.pos;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CategoryServiceImplIT {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void cleanUp() {
        categoryRepository.deleteAll();
    }

    @Test
    void save_shouldCreateCategory() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");
        dto.setStatus(true);
        CategoryDto response = categoryService.save(dto);
        Category saved = categoryRepository.findByIdentifier("CAT001");
        assertTrue(response.isSuccess());
        assertNotNull(saved);
        assertEquals("CAT001", saved.getIdentifier());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {
        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setDeleted(false);
        categoryRepository.save(category);
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");
        CategoryDto response = categoryService.save(dto);
        assertFalse(response.isSuccess());
        assertEquals(
                "Category with identifier - CAT001 already exists",
                response.getMessage()
        );
    }

    @Test
    void save_shouldFailWhenPreviouslyDeleted() {
        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setDeleted(true);
        categoryRepository.save(category);
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");
        CategoryDto response = categoryService.save(dto);
        assertFalse(response.isSuccess());
        assertEquals(
                "Category with identifier - CAT001 was previously deleted. " +
                        "Please contact backend team to restore.",
                response.getMessage()
        );
    }

    @Test
    void update_shouldUpdateSuperCategory() {
        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setDeleted(false);
        categoryRepository.save(category);
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");
        dto.setSuperCategory("PARENT");
        categoryService.update(dto);
        Category updated = categoryRepository.findByIdentifier("CAT001");
        assertEquals("PARENT", updated.getSuperCategory());
    }

    @Test
    void update_shouldFailWhenNotFound() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT_MISSING");
        CategoryDto response = categoryService.update(dto);
        assertFalse(response.isSuccess());
        assertEquals(
                "Category with identifier - CAT_MISSING not found",
                response.getMessage()
        );
    }

    @Test
    void findByIdentifier_shouldReturnCategory() {
        Category category = new Category();
        category.setIdentifier("CAT001");
        categoryRepository.save(category);
        CategoryDto result = categoryService.findByIdentifier("CAT001");
        assertEquals("CAT001", result.getIdentifier());
    }

    @Test
    void findByIdentifier_shouldThrowWhenNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.findByIdentifier("CAT_MISSING"));
    }

    @Test
    void toggleStatus_shouldToggleValue() {
        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setStatus(true);
        categoryRepository.save(category);
        categoryService.toggleStatus("CAT001");
        Category updated = categoryRepository.findByIdentifier("CAT001");
        assertFalse(updated.isStatus());
    }

    @Test
    void delete_shouldSoftDelete() {
        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setDeleted(false);
        categoryRepository.save(category);
        boolean result = categoryService.delete("CAT001");
        Category deleted = categoryRepository.findByIdentifier("CAT001");
        assertTrue(result);
        assertTrue(deleted.isDeleted());
    }

    @Test
    void delete_shouldReturnFalseWhenNotFound() {
        boolean result = categoryService.delete("CAT_MISSING");
        assertFalse(result);
    }

    @Test
    void findAll_pageable_shouldReturnOnlyNonDeletedCategories() {
        Category active = new Category();
        active.setIdentifier("CAT001");
        active.setDeleted(false);
        categoryRepository.save(active);
        Category deleted = new Category();
        deleted.setIdentifier("CAT002");
        deleted.setDeleted(true);
        categoryRepository.save(deleted);
        Pageable pageable = PageRequest.of(0, 10);
        WsDto<CategoryDto> result = categoryService.findAll(pageable);
        assertEquals(1, result.getTotalRecords());
        assertEquals("CAT001", result.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findAll_specification_shouldReturnFilteredResults() {
        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setDeleted(false);
        categoryRepository.save(category);
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Category> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        WsDto<CategoryDto> result = categoryService.findAll(spec, pageable, "CAT001");
        assertEquals(1, result.getTotalRecords());
        assertEquals("CAT001", result.getKeyword());
    }

    @Test
    void findBySuperCategoryNotNull_shouldReturnCategoriesWithSuperCategory() {
        Category withSuper = new Category();
        withSuper.setIdentifier("CAT001");
        withSuper.setStatus(true);
        withSuper.setDeleted(false);
        withSuper.setSuperCategory("PARENT");
        categoryRepository.save(withSuper);
        Category withoutSuper = new Category();
        withoutSuper.setIdentifier("CAT002");
        withoutSuper.setStatus(true);
        withoutSuper.setDeleted(false);
        withoutSuper.setSuperCategory("");
        categoryRepository.save(withoutSuper);
        List<CategoryDto> result = categoryService.findBySuperCategoryNotNull();
        assertEquals(1, result.size());
        assertEquals("CAT001", result.get(0).getIdentifier());
    }
}
