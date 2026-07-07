package com.ust.pos;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CategoryServiceIntegration {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
    }

    @Test
    void save_ShouldCreateCategory() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");
        dto.setSuperCategory("");

        CategoryDto result = categoryService.save(dto);

        assertTrue(result.isSuccess());

        Category savedCategory =
                categoryRepository.findByIdentifierAndDeletedFalse("CAT001");

        assertNotNull(savedCategory);
        assertEquals("CAT001", savedCategory.getIdentifier());
        assertTrue(savedCategory.isStatus());
        assertFalse(savedCategory.getDeleted());
    }

    @Test
    void save_ShouldNotCreateDuplicateCategory() {
        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setSuperCategory("");
        category.setStatus(true);
        category.setDeleted(false);
        categoryRepository.save(category);

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
    void save_ShouldNotCreateDeletedCategoryAgain() {
        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setSuperCategory("");
        category.setStatus(true);
        category.setDeleted(true);
        categoryRepository.save(category);

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");

        CategoryDto result = categoryService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Category with identifier - CAT001 was deleted and cannot be created again.",
                result.getMessage()
        );
    }

    @Test
    void update_ShouldUpdateExistingCategory() {
        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setSuperCategory("");
        category.setStatus(true);
        category.setDeleted(false);
        categoryRepository.save(category);

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");
        dto.setSuperCategory("");

        CategoryDto result = categoryService.update(dto);

        assertTrue(result.isSuccess());

        Category updatedCategory =
                categoryRepository.findByIdentifierAndDeletedFalse("CAT001");

        assertNotNull(updatedCategory);
    }

    @Test
    void update_ShouldReturnFailure_WhenCategoryNotFound() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT999");

        CategoryDto result = categoryService.update(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Category with identifier - CAT999 not found",
                result.getMessage()
        );
    }

    @Test
    void delete_ShouldSoftDeleteCategory() {
        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setSuperCategory("");
        category.setStatus(true);
        category.setDeleted(false);
        categoryRepository.save(category);

        categoryService.delete("CAT001");

        Category deletedCategory = categoryRepository.findByIdentifier("CAT001");

        assertNotNull(deletedCategory);
        assertTrue(deletedCategory.getDeleted());
    }

    @Test
    void findByIdentifier_ShouldReturnCategoryDto() {
        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setSuperCategory("");
        category.setStatus(true);
        category.setDeleted(false);
        categoryRepository.save(category);

        CategoryDto result = categoryService.findByIdentifier("CAT001");

        assertNotNull(result);
        assertEquals("CAT001", result.getIdentifier());
    }

    @Test
    void findAll_ShouldReturnOnlyNonDeletedCategories() {
        Category category1 = new Category();
        category1.setIdentifier("CAT001");
        category1.setSuperCategory("");
        category1.setStatus(true);
        category1.setDeleted(false);
        categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setIdentifier("CAT002");
        category2.setSuperCategory("");
        category2.setStatus(true);
        category2.setDeleted(true);
        categoryRepository.save(category2);

        WsDto<CategoryDto> result =
                categoryService.findAll(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getDtoList().size());
        assertEquals("CAT001", result.getDtoList().get(0).getIdentifier());
    }

    @Test
    void toggleStatus_ShouldChangeCategoryStatus() {
        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setSuperCategory("");
        category.setStatus(true);
        category.setDeleted(false);
        categoryRepository.save(category);

        CategoryDto result = categoryService.toggleStatus("CAT001");

        assertNotNull(result);

        Category updatedCategory =
                categoryRepository.findByIdentifierAndDeletedFalse("CAT001");

        assertFalse(updatedCategory.isStatus());
    }

    @Test
    void toggleStatus_ShouldReturnFailure_WhenCategoryNotFound() {
        CategoryDto result = categoryService.toggleStatus("CAT999");

        assertFalse(result.isSuccess());
        assertEquals("Category not found", result.getMessage());
    }

    @Test
    void findActiveCategories_ShouldReturnOnlyActiveCategories() {
        Category activeCategory = new Category();
        activeCategory.setIdentifier("CAT001");
        activeCategory.setSuperCategory("");
        activeCategory.setStatus(true);
        activeCategory.setDeleted(false);
        categoryRepository.save(activeCategory);

        Category inactiveCategory = new Category();
        inactiveCategory.setIdentifier("CAT002");
        inactiveCategory.setSuperCategory("");
        inactiveCategory.setStatus(false);
        inactiveCategory.setDeleted(false);
        categoryRepository.save(inactiveCategory);

        List<CategoryDto> result = categoryService.findActiveCategories();

        assertEquals(1, result.size());
        assertEquals("CAT001", result.get(0).getIdentifier());
    }

    @Test
    void findChildCategories_ShouldReturnCategoriesWithSuperCategory() {
        Category parentCategory = new Category();
        parentCategory.setIdentifier("CAT001");
        parentCategory.setSuperCategory("");
        parentCategory.setStatus(true);
        parentCategory.setDeleted(false);
        categoryRepository.save(parentCategory);

        Category childCategory = new Category();
        childCategory.setIdentifier("CAT002");
        childCategory.setSuperCategory("CAT001");
        childCategory.setStatus(true);
        childCategory.setDeleted(false);
        categoryRepository.save(childCategory);

        List<CategoryDto> result = categoryService.findChildCategories();

        assertEquals(1, result.size());
        assertEquals("CAT002", result.get(0).getIdentifier());
    }
}