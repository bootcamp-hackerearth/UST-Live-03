package com.ust.pos;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import jakarta.persistence.EntityManager;
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

    @Autowired
    private EntityManager entityManager;

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
        category.setDeleted(false);

        categoryRepository.save(category);

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");
        dto.setSuperCategory("PARENT");
        categoryService.update(dto);

        Category updated =
                categoryRepository.findByIdentifier("CAT001");

        assertEquals("PARENT", updated.getSuperCategory());
    }

    @Test
    void update_shouldSetMessageWhenNotFound() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");
        dto.setSuperCategory("PARENT");

        CategoryDto response = categoryService.update(dto);

        assertEquals(
                "Category with identifier - CAT001 not found",
                response.getMessage()
        );
    }

    @Test
    void findByIdentifier_shouldReturnCategory() {

        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setDeleted(false);

        categoryRepository.save(category);

        CategoryDto result =
                categoryService.findByIdentifier("CAT001");

        assertEquals("CAT001", result.getIdentifier());
    }

    @Test
    void deleteByIdentifier_shouldSoftDelete() {

        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setDeleted(false);

        categoryRepository.save(category);

        categoryService.delete("CAT001");
        Boolean deletedFlag = (Boolean) entityManager
                .createNativeQuery("SELECT deleted FROM category WHERE identifier = :identifier")
                .setParameter("identifier", "CAT001")
                .getSingleResult();

        assertTrue(deletedFlag);
    }
}