package com.ust.pos;

import com.ust.pos.category.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private CategoryDto sampleDto;
    private Category sampleEntity;

    @BeforeEach
    void setUp() {
        sampleDto = new CategoryDto();
        sampleDto.setIdentifier("CAT_TEST");
        sampleDto.setSuperCategory("  ");

        sampleEntity = new Category();
        sampleEntity.setIdentifier("CAT_TEST");
        sampleEntity.setDeleted(false);
        sampleEntity.setStatus(true);
    }

    @Test
    void save_Success_ReturnsSuccessDto() {
        when(categoryRepository.findByIdentifier("CAT_TEST")).thenReturn(null);
        when(categoryRepository.save(any(Category.class))).thenReturn(sampleEntity);

        CategoryDto result = categoryService.save(sampleDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Category created successfully", result.getMessage());
        assertNull(result.getSuperCategory());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void save_DuplicateActiveIdentifier_ReturnsFailureDto() {
        when(categoryRepository.findByIdentifier("CAT_TEST")).thenReturn(sampleEntity);

        CategoryDto result = categoryService.save(sampleDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void save_DuplicateSoftDeletedIdentifier_ReturnsFailureDto() {
        sampleEntity.setDeleted(true);
        when(categoryRepository.findByIdentifier("CAT_TEST")).thenReturn(sampleEntity);

        CategoryDto result = categoryService.save(sampleDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void save_NullIdentifier_ThrowsIllegalArgumentException() {
        sampleDto.setIdentifier(null);
        assertThrows(IllegalArgumentException.class, () -> categoryService.save(sampleDto));
    }

    @Test
    void update_Success_ReturnsUpdatedDto() {
        when(categoryRepository.findByIdentifier("CAT_TEST")).thenReturn(sampleEntity);
        when(categoryRepository.save(any(Category.class))).thenReturn(sampleEntity);

        sampleDto.setSuperCategory("PARENT_CAT");
        CategoryDto result = categoryService.update(sampleDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Category updated successfully", result.getMessage());
    }

    @Test
    void update_NotFound_ReturnsFailureDto() {
        when(categoryRepository.findByIdentifier("CAT_TEST")).thenReturn(null);

        CategoryDto result = categoryService.update(sampleDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void deleteByIdentifier_Success() {
        when(categoryRepository.existsBySuperCategory("CAT_TEST")).thenReturn(false);
        when(categoryRepository.findByIdentifier("CAT_TEST")).thenReturn(sampleEntity);

        assertDoesNotThrow(() -> categoryService.deleteByIdentifier("CAT_TEST"));
        verify(categoryRepository, times(1)).save(sampleEntity);
    }

    @Test
    void deleteByIdentifier_IsSuperCategory_ThrowsIllegalStateException() {
        when(categoryRepository.existsBySuperCategory("CAT_TEST")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> categoryService.deleteByIdentifier("CAT_TEST"));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void findByIdentifier_Success_ReturnsDto() {
        when(categoryRepository.findByIdentifier("CAT_TEST")).thenReturn(sampleEntity);

        CategoryDto result = categoryService.findByIdentifier("CAT_TEST");

        assertNotNull(result);
        assertEquals("CAT_TEST", result.getIdentifier());
    }

    @Test
    void findByIdentifier_NotFound_ThrowsResourceNotFoundException() {
        when(categoryRepository.findByIdentifier("CAT_TEST")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> categoryService.findByIdentifier("CAT_TEST"));
    }

    @Test
    void findAll_ReturnsPaginatedWsDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> page = new PageImpl<>(Collections.singletonList(sampleEntity));

        when(categoryRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<CategoryDto> result = categoryService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(0, result.getPage());
    }

    @Test
    void toggleStatus_Success_FlipsBoolean() {
        boolean initialStatus = sampleEntity.getStatus();
        when(categoryRepository.findByIdentifier("CAT_TEST")).thenReturn(sampleEntity);
        when(categoryRepository.save(any(Category.class))).thenReturn(sampleEntity);

        CategoryDto result = categoryService.toggleStatus("CAT_TEST");

        assertNotNull(result);
        assertNotEquals(initialStatus, sampleEntity.getStatus());
        verify(categoryRepository, times(1)).save(sampleEntity);
    }
}