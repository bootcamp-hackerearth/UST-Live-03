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
import org.springframework.test.context.ActiveProfiles;

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

        CategoryDto response = categoryService.save(dto);

        Category saved = categoryRepository.findByIdentifier("CAT001");

        assertNotNull(saved);
        assertEquals("CAT001", saved.getIdentifier());
        assertEquals("CAT001", response.getIdentifier());
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
    void save_shouldFailWhenExistingCategoryIsSoftDeleted() {

        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setDeleted(true);

        categoryRepository.save(category);

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");

        CategoryDto response = categoryService.save(dto);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void findById_shouldReturnCategory() {

        Category category = new Category();
        category.setIdentifier("CAT001");

        Category saved = categoryRepository.save(category);

        CategoryDto result = categoryService.findById(saved.getId());

        assertEquals("CAT001", result.getIdentifier());
    }

    @Test
    void findById_shouldThrowWhenNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.findById(9999L));
    }

    @Test
    void update_shouldUpdateSupercategory() {

        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setDeleted(false);

        Category saved = categoryRepository.save(category);

        CategoryDto dto = new CategoryDto();
        dto.setId(saved.getId());
        dto.setIdentifier("CAT001");
        dto.setSupercategory("PARENT");

        categoryService.update(dto);

        Category updated = categoryRepository.findByIdentifier("CAT001");

        assertEquals("PARENT", updated.getSupercategory());
    }

    @Test
    void findAll_shouldReturnOnlyNonDeletedCategories() {

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
    void delete_shouldSoftDeleteCategory() {

        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setDeleted(false);

        categoryRepository.save(category);

        categoryService.delete("CAT001");

        Category deleted = categoryRepository.findByIdentifier("CAT001");

        assertTrue(deleted.isDeleted());
    }

    @Test
    void findSubCategories_shouldReturnActiveSubcategoriesOnly() {

        Category sub = new Category();
        sub.setIdentifier("CAT001");
        sub.setStatus(true);
        sub.setSupercategory("PARENT");
        categoryRepository.save(sub);

        Category topLevel = new Category();
        topLevel.setIdentifier("CAT002");
        topLevel.setStatus(true);
        topLevel.setSupercategory("");
        categoryRepository.save(topLevel);

        var result = categoryService.findSubCategories();

        assertEquals(1, result.size());
        assertEquals("CAT001", result.get(0).getIdentifier());
    }

    @Test
    void changeCategoryStatus_shouldUpdateStatus() {

        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setStatus(true);

        categoryRepository.save(category);

        CategoryDto result = categoryService.changeCategoryStatus("CAT001", false);

        assertNotNull(result);
        assertFalse(result.isStatus());

        Category updated = categoryRepository.findByIdentifier("CAT001");
        assertFalse(updated.isStatus());
    }

    @Test
    void changeCategoryStatus_shouldReturnNullWhenCategoryNotFound() {

        CategoryDto result = categoryService.changeCategoryStatus("NON_EXISTENT", true);

        assertNull(result);
    }
}