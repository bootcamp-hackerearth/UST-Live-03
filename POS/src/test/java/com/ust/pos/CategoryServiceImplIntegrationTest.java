package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.modell.Category;
import com.ust.pos.modell.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CategoryServiceImplIntegrationTest {

    @Autowired
    private CategoryServiceImpl categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
    }

    private Category createCategory(
            String identifier,
            String superCategory,
            Boolean status,
            Boolean deleted) {

        Category category = new Category();
        category.setIdentifier(identifier);
        category.setSuperCategory(superCategory);
        category.setStatus(status);
        category.setDeleted(deleted);

        return categoryRepository.save(category);
    }

    @Test
    void save_ShouldCreateCategorySuccessfully() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");

        CategoryDto result = categoryService.save(dto);

        assertNotNull(result);

        Category saved =
                categoryRepository.findByIdentifier("CAT001");

        assertNotNull(saved);
        assertEquals("CAT001", saved.getIdentifier());
        assertNull(saved.getSuperCategory());
    }

    @Test
    void save_ShouldFail_WhenCategoryAlreadyExists() {

        createCategory(
                "CAT001",
                null,
                true,
                false
        );

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");

        CategoryDto result = categoryService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Category with identifier - CAT001 already exists",
                result.getMessage()
        );
    }

    @Test
    void save_ShouldFail_WhenSoftDeletedCategoryExists() {

        createCategory(
                "CAT001",
                null,
                true,
                true
        );

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");

        CategoryDto result = categoryService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Category with Identifier CAT001 already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void update_ShouldUpdateCategorySuccessfully() {

        Category saved = createCategory(
                "CAT001",
                null,
                true,
                false
        );

        CategoryDto dto = new CategoryDto();
        dto.setId(saved.getId());
        dto.setIdentifier("CAT002");
        dto.setSuperCategory("SUPER1");

        CategoryDto result = categoryService.update(dto);

        assertNotNull(result);

        Category updated =
                categoryRepository.findById(saved.getId()).orElseThrow();

        assertEquals("CAT002", updated.getIdentifier());
        assertEquals("SUPER1", updated.getSuperCategory());
    }

    @Test
    void update_ShouldFail_WhenCategoryNotFound() {

        CategoryDto dto = new CategoryDto();
        dto.setId(999L);
        dto.setIdentifier("CAT001");

        CategoryDto result = categoryService.update(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Category not found",
                result.getMessage()
        );
    }

    @Test
    void update_ShouldFail_WhenIdentifierAlreadyExists() {

        Category first = createCategory(
                "CAT001",
                null,
                true,
                false
        );

        createCategory(
                "CAT002",
                null,
                true,
                false
        );

        CategoryDto dto = new CategoryDto();
        dto.setId(first.getId());
        dto.setIdentifier("CAT002");

        CategoryDto result = categoryService.update(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Category already exists",
                result.getMessage()
        );
    }

    @Test
    void delete_ShouldSoftDeleteCategory() {

        createCategory(
                "CAT001",
                null,
                true,
                false
        );

        categoryService.delete("CAT001");

        Category deleted =
                categoryRepository.findByIdentifier("CAT001");

        assertTrue(deleted.getDeleted());
    }

    @Test
    void delete_ShouldThrowException_WhenUsedAsSuperCategory() {

        createCategory(
                "PARENT",
                null,
                true,
                false
        );

        createCategory(
                "CHILD",
                "PARENT",
                true,
                false
        );

        IllegalStateException ex =
                assertThrows(
                        IllegalStateException.class,
                        () -> categoryService.delete("PARENT")
                );

        assertEquals(
                "Cannot delete category. It is used as a super category.",
                ex.getMessage()
        );
    }

    @Test
    void findByIdentifier_ShouldReturnCategory() {

        createCategory(
                "CAT001",
                null,
                true,
                false
        );

        CategoryDto result =
                categoryService.findByIdentifier("CAT001");

        assertNotNull(result);
        assertEquals("CAT001", result.getIdentifier());
    }

    @Test
    void findByIdentifier_ShouldThrowException_WhenNotFound() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.findByIdentifier("INVALID")
        );
    }

    @Test
    void findAll_ShouldReturnNonDeletedCategories() {

        createCategory(
                "CAT001",
                null,
                true,
                false
        );

        createCategory(
                "CAT002",
                null,
                true,
                false
        );

        WsDto<CategoryDto> result =
                categoryService.findAll(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(2, result.getTotalRecords());
        assertEquals(2, result.getDtoList().size());
    }

    @Test
    void findChildCategories_ShouldReturnCategoriesHavingSuperCategory() {

        createCategory(
                "CAT001",
                null,
                true,
                false
        );

        createCategory(
                "CAT002",
                "CAT001",
                true,
                false
        );

        List<CategoryDto> result =
                categoryService.findChildCategories();

        assertEquals(1, result.size());
        assertEquals("CAT002", result.get(0).getIdentifier());
    }

    @Test
    void toggleStatus_ShouldDisableCategory() {

        createCategory(
                "CAT001",
                null,
                true,
                false
        );

        CategoryDto result =
                categoryService.toggleStatus("CAT001");

        assertFalse(result.getStatus());

        Category updated =
                categoryRepository.findByIdentifier("CAT001");

        assertFalse(updated.getStatus());
    }

    @Test
    void toggleStatus_ShouldEnableCategory() {

        createCategory(
                "CAT001",
                null,
                false,
                false
        );

        CategoryDto result =
                categoryService.toggleStatus("CAT001");

        assertTrue(result.getStatus());

        Category updated =
                categoryRepository.findByIdentifier("CAT001");

        assertTrue(updated.getStatus());
    }

    @Test
    void toggleStatus_ShouldThrowException_WhenCategoryNotFound() {

        NoSuchElementException ex =
                assertThrows(
                        NoSuchElementException.class,
                        () -> categoryService.toggleStatus("INVALID")
                );

        assertEquals(
                "category not found with identifier: INVALID",
                ex.getMessage()
        );
    }

    @Test
    void findAllActive_ShouldReturnOnlyActiveCategories() {

        createCategory(
                "CAT001",
                null,
                true,
                false
        );

        createCategory(
                "CAT002",
                null,
                false,
                false
        );

        List<CategoryDto> result =
                categoryService.findAllActive();

        assertEquals(1, result.size());
        assertEquals("CAT001", result.get(0).getIdentifier());
    }
}