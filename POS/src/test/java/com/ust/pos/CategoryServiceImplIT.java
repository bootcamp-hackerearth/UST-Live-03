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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

        Category saved = categoryRepository.findByIdentifier("CAT001");
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
        assertEquals("Category with identifier - CAT001 already exists", response.getMessage());
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
        assertEquals("Category with identifier CAT001 was previously deleted. Please contact backend team to restore.", response.getMessage());
    }

    @Test
    void update_shouldUpdateCategoryDetails() {
        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setStatus(true);
        category.setDeleted(false);
        categoryRepository.save(category);

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");
        dto.setStatus(false);

        CategoryDto response = categoryService.update(dto);
        assertTrue(response.isSuccess());

        Category updated = categoryRepository.findByIdentifier("CAT001");
        assertFalse(updated.isStatus());
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
    void findByIdentifier_shouldThrowExceptionWhenNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.findByIdentifier("NON-EXISTENT");
        });
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

        boolean isDeleted = categoryService.delete("CAT001");
        assertTrue(isDeleted);

        Category deleted = categoryRepository.findByIdentifier("CAT001");
        assertTrue(deleted.isDeleted());
    }

    @Test
    void findAll_shouldReturnPaginatedData() {
        Category category1 = new Category();
        category1.setIdentifier("CAT001");
        category1.setDeleted(false);
        categoryRepository.save(category1);

        Pageable pageable = PageRequest.of(0, 10);
        WsDto<CategoryDto> response = categoryService.findAll(pageable);

        assertNotNull(response);
    }

    @Test
    void findIfTrue_shouldReturnActiveAndNonDeletedRecords() {
        Category activeCategory = new Category();
        activeCategory.setIdentifier("CAT001");
        activeCategory.setStatus(true);
        activeCategory.setDeleted(false);
        categoryRepository.save(activeCategory);

        List<CategoryDto> activeList = categoryService.findIfTrue();
        assertFalse(activeList.isEmpty());
    }

    @Test
    void findBySuperCategoryNotNull_shouldReturnMatchingCategories() {
        Category category = new Category();
        category.setIdentifier("CAT002");
        category.setStatus(true);
        category.setDeleted(false);
        category.setSuperCategory("Electronics");
        categoryRepository.save(category);

        List<CategoryDto> result = categoryService.findBySuperCategoryNotNull();
        assertFalse(result.isEmpty());
        assertEquals("CAT002", result.get(0).getIdentifier());
    }
}