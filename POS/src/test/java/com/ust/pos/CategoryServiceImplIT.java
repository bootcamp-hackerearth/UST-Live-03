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
public class CategoryServiceImplIT {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    private CategoryDto sampleCategoryDto;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();

        sampleCategoryDto = new CategoryDto();
        sampleCategoryDto.setIdentifier("CAT-ELEC");
        sampleCategoryDto.setSuperCategory("CAT-PARENT");
        sampleCategoryDto.setStatus(true);
    }

    @Test
    void testSave_Success() {
        CategoryDto result = categoryService.save(sampleCategoryDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Category Added Successfully", result.getMessage());

        Category savedCategory = categoryRepository.findByIdentifier("CAT-ELEC");
        assertNotNull(savedCategory);
        assertEquals("CAT-PARENT", savedCategory.getSuperCategory());
        assertFalse(savedCategory.isDeleted());
    }

    @Test
    void testSave_Success_WithBlankSuperCategory() {
        sampleCategoryDto.setSuperCategory("   ");
        CategoryDto result = categoryService.save(sampleCategoryDto);

        assertTrue(result.isSuccess());
        Category savedCategory = categoryRepository.findByIdentifier("CAT-ELEC");
        assertNull(savedCategory.getSuperCategory());
    }

    @Test
    void testSave_AlreadyExists_Active() {
        categoryService.save(sampleCategoryDto);

        CategoryDto duplicateDto = new CategoryDto();
        duplicateDto.setIdentifier("CAT-ELEC");

        CategoryDto result = categoryService.save(duplicateDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_AlreadyExists_PreviouslyDeleted() {
        Category deletedCategory = new Category();
        deletedCategory.setIdentifier("CAT-OLD");
        deletedCategory.setDeleted(true);
        categoryRepository.save(deletedCategory);

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT-OLD");

        CategoryDto result = categoryService.save(dto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testSave_CannotBeItsOwnSuperCategory() {
        sampleCategoryDto.setIdentifier("CAT-SAME");
        sampleCategoryDto.setSuperCategory("CAT-SAME");

        CategoryDto result = categoryService.save(sampleCategoryDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Category cannot be its own super category", result.getMessage());
    }

    @Test
    void testFindByIdentifier_Success() {
        categoryService.save(sampleCategoryDto);

        CategoryDto foundDto = categoryService.findByIdentifier("CAT-ELEC");

        assertNotNull(foundDto);
        assertEquals("CAT-ELEC", foundDto.getIdentifier());
    }

    @Test
    void testFindByIdentifier_ThrowsResourceNotFoundException() {
        assertThrows(ResourceNotFoundException.class, () -> categoryService.findByIdentifier("NON-EXISTENT"));
    }

    @Test
    void testFindAll_Paged_Success() {
        categoryService.save(sampleCategoryDto);

        Pageable pageable = PageRequest.of(0, 5);
        WsDto<CategoryDto> wsResult = categoryService.findAll(pageable);

        assertNotNull(wsResult);
        assertEquals(1, wsResult.getTotalRecords());
        assertEquals(1, wsResult.getDtoList().size());
    }

    @Test
    void testFindAll_WithSpecification_Success() {
        categoryService.save(sampleCategoryDto);

        Specification<Category> searchSpec = (root, query, cb) -> cb.equal(root.get("identifier"), "CAT-ELEC");
        Pageable pageable = PageRequest.of(0, 5);

        WsDto<CategoryDto> wsResult = categoryService.findAll(searchSpec, pageable);

        assertNotNull(wsResult);
        assertEquals(1, wsResult.getTotalRecords());
        assertEquals("CAT-ELEC", wsResult.getDtoList().getFirst().getIdentifier());
    }

    @Test
    void testFindAllActive() {
        categoryService.save(sampleCategoryDto);

        List<CategoryDto> activeList = categoryService.findAllActive();

        assertNotNull(activeList);
        assertEquals(1, activeList.size());
        assertTrue(activeList.getFirst().isStatus());
    }

    @Test
    void testUpdate_Success() {
        categoryService.save(sampleCategoryDto);

        CategoryDto updateDto = new CategoryDto();
        updateDto.setIdentifier("CAT-ELEC");
        updateDto.setSuperCategory("CAT-NEW-PARENT");

        CategoryDto result = categoryService.update(updateDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Updated"));

        Category updatedEntity = categoryRepository.findByIdentifier("CAT-ELEC");
        assertEquals("CAT-NEW-PARENT", updatedEntity.getSuperCategory());
    }

    @Test
    void testUpdate_NotFound() {
        CategoryDto nonExistentDto = new CategoryDto();
        nonExistentDto.setIdentifier("MISSING-ID");

        CategoryDto result = categoryService.update(nonExistentDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testToggleStatus() {
        categoryService.save(sampleCategoryDto);

        CategoryDto toggledOff = categoryService.toggleStatus("CAT-ELEC");
        assertFalse(toggledOff.isStatus());

        CategoryDto toggledOn = categoryService.toggleStatus("CAT-ELEC");
        assertTrue(toggledOn.isStatus());
    }

    @Test
    void testDelete_Success() {
        categoryService.save(sampleCategoryDto);

        boolean deletionFlag = categoryService.delete("CAT-ELEC");
        assertTrue(deletionFlag);

        Category deletedEntity = categoryRepository.findByIdentifier("CAT-ELEC");
        assertNotNull(deletedEntity);
        assertTrue(deletedEntity.isDeleted());
    }

    @Test
    void testDelete_NotFound() {
        boolean deletionFlag = categoryService.delete("UNKNOWN-ID");
        assertFalse(deletionFlag);
    }
}