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
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;

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

    private Category category;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setIdentifier("CAT-01");
        category.setSuperCategory("PARENT-CAT");
        category.setDeleted(false);
        category.setStatus(true);

        categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT-01");
        categoryDto.setSuperCategory("PARENT-CAT");
    }

    @Test
    void testSave_WhenDtoIsNull() {
        assertThrows(NullPointerException.class, () -> categoryService.save(null));
    }

    @Test
    void testSave_WhenIdentifierIsNull() {
        categoryDto.setIdentifier(null);
        when(categoryRepository.findByIdentifier(null)).thenReturn(null);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDto result = categoryService.save(categoryDto);
        assertNotNull(result);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testSave_WhenCategoryExistsAndNotDeleted() {
        category.setDeleted(false);
        when(categoryRepository.findByIdentifier("CAT-01")).thenReturn(category);

        CategoryDto result = categoryService.save(categoryDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_WhenCategoryExistsAndgetDeleted() {
        category.setDeleted(true);
        when(categoryRepository.findByIdentifier("CAT-01")).thenReturn(category);

        CategoryDto result = categoryService.save(categoryDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testSave_SuccessWithSuperCategory() {
        when(categoryRepository.findByIdentifier("CAT-01")).thenReturn(null);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDto result = categoryService.save(categoryDto);

        assertNotNull(result);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testSave_SuccessWithEmptySuperCategory() {
        categoryDto.setSuperCategory("   ");
        category.setSuperCategory(null);
        when(categoryRepository.findByIdentifier("CAT-01")).thenReturn(null);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDto result = categoryService.save(categoryDto);

        assertNotNull(result);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testUpdate_WhenCategoryNotFound() {
        when(categoryRepository.findByIdentifier("CAT-01")).thenReturn(null);

        CategoryDto result = categoryService.update(categoryDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testUpdate_WhenCategorygetDeleted() {
        category.setDeleted(true);
        when(categoryRepository.findByIdentifier("CAT-01")).thenReturn(category);

        CategoryDto result = categoryService.update(categoryDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testUpdate_SuccessWithSuperCategory() {
        when(categoryRepository.findByIdentifier("CAT-01")).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDto result = categoryService.update(categoryDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Category updated successfully", result.getMessage());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testUpdate_SuccessWithNullSuperCategory() {
        categoryDto.setSuperCategory(null);
        when(categoryRepository.findByIdentifier("CAT-01")).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDto result = categoryService.update(categoryDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testDeleteByIdentifier_WhenUsedAsSuperCategory() {
        when(categoryRepository.existsBySuperCategory("CAT-01")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> categoryService.deleteByIdentifier("CAT-01"));
        verify(categoryRepository, never()).findByIdentifier(anyString());
    }

    @Test
    void testDeleteByIdentifier_WhenCategoryNotFound() {
        when(categoryRepository.existsBySuperCategory("CAT-01")).thenReturn(false);
        when(categoryRepository.findByIdentifier("CAT-01")).thenReturn(null);

        categoryService.deleteByIdentifier("CAT-01");

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testDeleteByIdentifier_Success() {
        when(categoryRepository.existsBySuperCategory("CAT-01")).thenReturn(false);
        when(categoryRepository.findByIdentifier("CAT-01")).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        categoryService.deleteByIdentifier("CAT-01");

        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testFindByIdentifier_NotFound() {
        when(categoryRepository.findByIdentifier("CAT-01")).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> categoryService.findByIdentifier("CAT-01"));
    }

    @Test
    void testFindByIdentifier_Success() {
        when(categoryRepository.findByIdentifier("CAT-01")).thenReturn(category);

        CategoryDto result = categoryService.findByIdentifier("CAT-01");

        assertNotNull(result);
        assertEquals("CAT-01", result.getIdentifier());
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> list = Collections.singletonList(category);
        Page<Category> page = new PageImpl<>(list, pageable, 1);

        when(categoryRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<CategoryDto> result = categoryService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
        assertFalse(result.getDtoList().isEmpty());
    }

    @Test
    void testFindChildCategories() {
        List<Category> list = Collections.singletonList(category);
        when(categoryRepository.findBySuperCategoryIsNotNullAndDeletedFalse()).thenReturn(list);

        List<CategoryDto> result = categoryService.findChildCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testToggleStatus_WhenCategoryNotFound() {
        when(categoryRepository.findByIdentifier("CAT-01")).thenReturn(null);

        CategoryDto result = categoryService.toggleStatus("CAT-01");

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testToggleStatus_Success() {
        category.setStatus(true);
        when(categoryRepository.findByIdentifier("CAT-01")).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDto result = categoryService.toggleStatus("CAT-01");

        assertNotNull(result);
        assertFalse(result.isStatus());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testFindIfTrue() {
        List<Category> list = Collections.singletonList(category);
        when(categoryRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(list);

        List<CategoryDto> result = categoryService.findIfTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testFindBySuperCategoryNotNull() {
        List<Category> list = Collections.singletonList(category);
        when(categoryRepository.findByStatusIsTrueAndSuperCategoryIsNotNullAndDeletedFalse()).thenReturn(list);

        List<CategoryDto> result = categoryService.findBySuperCategoryNotNull();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testFindAllWithSpecification() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> list = Collections.singletonList(category);
        Page<Category> page = new PageImpl<>(list, pageable, 1);
        Specification<Category> spec = mock(Specification.class);

        when(categoryRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        WsDto<CategoryDto> result = categoryService.findAll(spec, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
        assertFalse(result.getDtoList().isEmpty());

        verify(categoryRepository, times(1)).findAll(spec, pageable);
    }
}