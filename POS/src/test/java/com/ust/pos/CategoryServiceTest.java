package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock(lenient = true)
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT01");

        Category category = new Category();
        category.setIdentifier("CAT01");
        category.setSuperCategory(List.of("PARENT"));

        when(categoryRepository.findByIdentifier("CAT01")).thenReturn(null);
        when(modelMapper.map(dto, Category.class)).thenReturn(category);

        CategoryDto response = categoryService.save(dto);

        assertEquals("CAT01", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        verify(categoryRepository).save(category);
    }

    @Test
    void saveFailureTest() {
        Category category = new Category();
        category.setIdentifier("CAT01");
        category.setSuperCategory(List.of("PARENT"));

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT01");

        when(categoryRepository.findByIdentifier("CAT01")).thenReturn(category);

        CategoryDto response = categoryService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        Category category = new Category();
        category.setIdentifier("CAT01");
        category.setSuperCategory(List.of("PARENT"));

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT01");

        when(categoryRepository.findByIdentifier("CAT01")).thenReturn(category);

        CategoryDto response = categoryService.update(dto);

        assertEquals("CAT01", response.getIdentifier());
        verify(modelMapper).map(dto, category);
        verify(categoryRepository).save(category);
    }

    @Test
    void updateFailureTest() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT01");

        when(categoryRepository.findByIdentifier("CAT01")).thenReturn(null);

        CategoryDto response = categoryService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        categoryService.delete("CAT01");
        verify(categoryRepository).deleteByIdentifier("CAT01");
    }

    @Test
    void findByIdentifierSuccessTest() {
        Category category = new Category();
        category.setIdentifier("CAT01");
        category.setSuperCategory(List.of("PARENT"));

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT01");

        when(categoryRepository.findByIdentifier("CAT01")).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(dto);

        CategoryDto response = categoryService.findByIdentifier("CAT01");

        assertNotNull(response);
        assertEquals("CAT01", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(categoryRepository.findByIdentifier("CAT01")).thenReturn(null);

        CategoryDto response = categoryService.findByIdentifier("CAT01");

        Assertions.assertNull(response);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Category> categories = List.of(new Category(), new Category());
        Page<Category> page = new PageImpl<>(categories);

        List<CategoryDto> dtoList = List.of(new CategoryDto(), new CategoryDto());

        when(categoryRepository.findAll(pageable)).thenReturn(page);

        when(modelMapper.map(
                eq(categories),
                any(java.lang.reflect.Type.class)
        )).thenReturn(dtoList);

        List<CategoryDto> result = categoryService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(categoryRepository).findAll(pageable);
    }

    @Test
    void findAllCategoriesWithNoSuperTest() {
        Category c1 = new Category();
        c1.setSuperCategory(List.of("PARENT"));

        Category c2 = new Category();
        c2.setSuperCategory(List.of("CHILD"));

        List<Category> categories = List.of(c1, c2);
        List<CategoryDto> dtoList = List.of(new CategoryDto(), new CategoryDto());

        when(categoryRepository.findAll()).thenReturn(categories);
        when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(dtoList);

        List<CategoryDto> result = categoryService.findAllCategoriesWithNoSuper();

        assertNotNull(result);
        assertEquals(2, result.size());
    }
}