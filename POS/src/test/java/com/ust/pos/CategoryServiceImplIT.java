package com.ust.pos;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
        dto.setStatus(true);

        categoryService.save(dto);

        Category saved =
                categoryRepository.findByIdentifier("CAT001");

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
    void update_shouldUpdateSuperCategory() {

        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setStatus(true);
        category.setDeleted(false);

        categoryRepository.save(category);

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");
        dto.setSuperCategory("PARENT");

        categoryService.update(dto);

        Category updated =
                categoryRepository.findByIdentifier("CAT001");

        assertEquals("PARENT",
                updated.getSuperCategory());
    }

    @Test
    void findByIdentifier_shouldReturnCategory() {

        Category category = new Category();
        category.setIdentifier("CAT001");

        categoryRepository.save(category);

        CategoryDto result =
                categoryService.findByIdentifier("CAT001");

        assertNotNull(result);
        assertEquals("CAT001", result.getIdentifier());
    }

    @Test
    void changeToggleStatus_shouldUpdateStatus() {

        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setStatus(true);

        categoryRepository.save(category);

        categoryService.changeToggleStatus(
                "CAT001",
                false
        );

        Category updated =
                categoryRepository.findByIdentifier("CAT001");

        assertFalse(updated.isStatus());
    }

    @Test
    void delete_shouldSoftDeleteCategory() {

        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setDeleted(false);

        categoryRepository.save(category);

        categoryService.delete("CAT001");

        Category deleted =
                categoryRepository.findByIdentifier("CAT001");

        assertTrue(deleted.isDeleted());
    }
}