package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
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
        Assertions.assertEquals("CAT01", response.getIdentifier());
        assertNull(response.getMessage());
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
        Assertions.assertNotNull(response.getMessage());
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

        Assertions.assertEquals("CAT01", response.getIdentifier());
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
        Assertions.assertNotNull(response.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteTest() {

        Category category = new Category();
        category.setIdentifier("CAT01");

        when(categoryRepository.findByIdentifier("CAT01"))
                .thenReturn(category);

        categoryService.delete("CAT01");

        assertTrue(category.isDeleted());

        verify(categoryRepository)
                .findByIdentifier("CAT01");
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

        Assertions.assertNotNull(response);
        Assertions.assertEquals("CAT01", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {

        when(categoryRepository.findByIdentifier("CAT01")).thenReturn(null);
        CategoryDto response = categoryService.findByIdentifier("CAT01");
        assertNull(response);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Category category = new Category();
        category.setIdentifier("CAT01");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT01");

        Page<Category> page =
                new PageImpl<>(List.of(category), pageable, 1);

        when(categoryRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(category, CategoryDto.class))
                .thenReturn(dto);

        WsDto<CategoryDto> result =
                categoryService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("CAT01",
                result.getContent().getFirst().getIdentifier());

        assertEquals(0, result.getPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getTotalRecords());

        verify(categoryRepository)
                .findByIsDeletedFalse(pageable);

        verify(modelMapper)
                .map(category, CategoryDto.class);
    }

    @Test
    void saveFailureDeletedCategoryTest() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT01");

        Category category = new Category();
        category.setDeleted(true);

        when(categoryRepository.findByIdentifier("CAT01"))
                .thenReturn(category);

        CategoryDto response = categoryService.save(dto);

        assertFalse(response.isSuccess());

        assertTrue(response.getMessage()
                .contains("already exists but was deleted"));
    }

    @Test
    void testFindAllCategoriesWithNullSuperCategory() {

        Category category = new Category();
        category.setSuperCategory(null);

        when(categoryRepository.findByStatus(true))
                .thenReturn(List.of(category));

        when(modelMapper.map(category, CategoryDto.class))
                .thenReturn(new CategoryDto());

        List<CategoryDto> result =
                categoryService.findAllCategoriesWithNoSuper();

        assertEquals(1, result.size());

        verify(categoryRepository).findByStatus(true);
    }

    @Test
    void testFindAllCategoriesWithNoSuper() {

        Category category1 = new Category();
        category1.setSuperCategory(List.of("parent"));

        Category category2 = new Category();
        category2.setSuperCategory(List.of());

        List<Category> categories = List.of(category1, category2);

        when(categoryRepository.findByStatus(true))
                .thenReturn(categories);

        when(modelMapper.map(any(Category.class),
                eq(CategoryDto.class)))
                .thenReturn(new CategoryDto());

        List<CategoryDto> result =
                categoryService.findAllCategoriesWithNoSuper();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(categoryRepository).findByStatus(true);
    }
}