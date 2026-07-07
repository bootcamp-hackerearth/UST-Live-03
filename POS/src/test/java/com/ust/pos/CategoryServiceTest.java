package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
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

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    @Spy
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierTest() {

        Category category = new Category();
        category.setIdentifier("CAT1");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);

        when(modelMapper.map(category, CategoryDto.class)).thenReturn(dto);

        CategoryDto result = categoryService.findByIdentifier("CAT1");

        assertNotNull(result);
        assertEquals("CAT1", result.getIdentifier());

        verify(categoryRepository).findByIdentifier("CAT1");

    }

    @Test
    void findByIdentifierNotFoundTest() {

        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> categoryService.findByIdentifier("CAT1"));

    }

    @Test
    void saveTest() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setSuperCategory("SUPER");

        CategoryDto response = new CategoryDto();
        response.setSuccess(true);

        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);

        when(modelMapper.map(any(Category.class), eq(CategoryDto.class))).thenReturn(response);

        CategoryDto result = categoryService.save(dto);

        assertTrue(result.isSuccess());

        verify(categoryRepository).save(any(Category.class));

    }

    @Test
    void saveNullSuperCategoryTest() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setSuperCategory(null);

        CategoryDto response = new CategoryDto();

        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);

        when(modelMapper.map(any(Category.class), eq(CategoryDto.class))).thenReturn(response);

        categoryService.save(dto);

        verify(categoryRepository).save(any(Category.class));

    }

    @Test
    void saveEmptySuperCategoryTest() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setSuperCategory(" ");

        CategoryDto response = new CategoryDto();

        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);

        when(modelMapper.map(any(Category.class), eq(CategoryDto.class))).thenReturn(response);

        categoryService.save(dto);

        verify(categoryRepository).save(any(Category.class));

    }

    @Test
    void saveAlreadyExistsTest() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        Category existing = new Category();

        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(existing);

        CategoryDto result = categoryService.save(dto);

        assertFalse(result.isSuccess());

        assertEquals("Category with identifier - CAT1 already exists", result.getMessage());

    }

    @Test
    void saveSoftDeletedTest() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        Category existing = new Category();
        existing.setDeleted(true);

        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(existing);

        CategoryDto result = categoryService.save(dto);

        assertFalse(result.isSuccess());

        assertEquals("Category with identifier - CAT1 has been soft deleted.(Rollback by changing status", result.getMessage());

    }

    @Test
    void updateTest() {

        Category category = new Category();
        category.setIdentifier("CAT1");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setSuperCategory("SUPER");

        CategoryDto response = new CategoryDto();

        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);

        when(modelMapper.map(category, CategoryDto.class)).thenReturn(response);

        CategoryDto result = categoryService.update(dto);

        assertTrue(result.isSuccess());

        verify(categoryRepository).save(category);

    }

    @Test
    void updateNullSuperCategoryTest() {

        Category category = new Category();

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        CategoryDto response = new CategoryDto();

        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);

        when(modelMapper.map(category, CategoryDto.class)).thenReturn(response);

        categoryService.update(dto);

        verify(categoryRepository).save(category);

    }

    @Test
    void updateBlankSuperCategoryTest() {

        Category category = new Category();

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setSuperCategory(" ");

        CategoryDto response = new CategoryDto();

        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);

        when(modelMapper.map(category, CategoryDto.class)).thenReturn(response);

        categoryService.update(dto);

        verify(categoryRepository).save(category);

    }

    @Test
    void updateNotFoundTest() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> categoryService.update(dto));

    }

    @Test
    void deleteTest() {

        Category category = new Category();

        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);

        boolean result = categoryService.delete("CAT1");

        assertTrue(result);

        verify(categoryRepository).save(category);

    }

    @Test
    void deleteNotFoundTest() {

        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> categoryService.delete("CAT1"));

    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Category category = new Category();

        CategoryDto dto = new CategoryDto();

        Page<Category> page = new PageImpl<>(List.of(category));

        when(categoryRepository.findByDeletedFalse(pageable)).thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(dto));

        WsDto<CategoryDto> result = categoryService.findAll(pageable);

        assertEquals(1, result.getDtoList().size());

        assertEquals(0, result.getPage());

    }

    @Test
    void findAllSpecificationTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Category> spec = (root, query, cb) -> null;

        Category category = new Category();

        CategoryDto dto = new CategoryDto();

        Page<Category> page = new PageImpl<>(List.of(category));

        when(categoryRepository.findAll(spec, pageable)).thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(dto));

        WsDto<CategoryDto> result = categoryService.findAll(spec, pageable, "abc");

        assertEquals("abc", result.getKeyword());

        assertEquals(1, result.getDtoList().size());

    }

    @Test
    void findSuperCategoriesTest() {

        List<Category> categories = List.of(new Category());

        List<CategoryDto> dtos = List.of(new CategoryDto());

        when(categoryRepository.findBySuperCategoryIsNull()).thenReturn(categories);

        when(modelMapper.map(eq(categories), any(Type.class))).thenReturn(dtos);

        List<CategoryDto> result = categoryService.findSuperCategories();

        assertEquals(1, result.size());

    }

    @Test
    void toggleStatusTest() {

        Category category = new Category();

        category.setStatus(true);
        category.setSuperCategory("SUPER");

        CategoryDto dto = new CategoryDto();

        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);

        when(modelMapper.map(category, CategoryDto.class)).thenReturn(dto);

        categoryService.toggleStatus("CAT1");

        assertFalse(category.isStatus());

        verify(categoryRepository).save(category);

    }

    @Test
    void toggleStatusNullSuperCategoryTest() {

        Category category = new Category();

        category.setSuperCategory(null);

        CategoryDto dto = new CategoryDto();

        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);

        when(modelMapper.map(category, CategoryDto.class)).thenReturn(dto);

        categoryService.toggleStatus("CAT1");

        verify(categoryRepository, never()).save(any());

    }

    @Test
    void toggleStatusNotFoundTest() {

        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> categoryService.toggleStatus("CAT1"));

    }

    @Test
    void findIfTrueTest() {

        Category c1 = new Category();
        c1.setStatus(true);

        Category c2 = new Category();
        c2.setStatus(false);

        List<CategoryDto> dtos = List.of(new CategoryDto());

        when(categoryRepository.findBySuperCategoryIsNotNull()).thenReturn(List.of(c1, c2));

        when(modelMapper.map(any(List.class), any(Type.class))).thenReturn(dtos);

        List<CategoryDto> result = categoryService.findIfTrue();

        assertEquals(1, result.size());

    }

}