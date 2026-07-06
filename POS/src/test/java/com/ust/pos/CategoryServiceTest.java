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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    void findByIdentifier_Found() {

        Category category = new Category();
        category.setIdentifier("C1");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("C1");

        when(categoryRepository.findByIdentifier("C1")).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(dto);

        CategoryDto result = categoryService.findByIdentifier("C1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("C1", result.getIdentifier());
    }

    @Test
    void save_NewCategory() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("C1");

        Category category = new Category();

        when(categoryRepository.findByIdentifier("C1")).thenReturn(null);
        when(modelMapper.map(dto, Category.class)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);

        CategoryDto result = categoryService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("C1", result.getIdentifier());

        verify(categoryRepository).save(category);
    }

    @Test
    void save_CategoryExists() {

        Category existing = new Category();
        existing.setDeleted(false);

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("C1");

        when(categoryRepository.findByIdentifier("C1")).thenReturn(existing);

        CategoryDto result = categoryService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void save_CategoryDeleted() {

        Category existing = new Category();
        existing.setDeleted(true);

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("C1");

        when(categoryRepository.findByIdentifier("C1")).thenReturn(existing);

        CategoryDto result = categoryService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("deleted"));

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void update_CategoryExists() {

        Category existing = new Category();

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("C1");

        when(categoryRepository.findByIdentifier("C1")).thenReturn(existing);
        when(categoryRepository.save(existing)).thenReturn(existing);

        CategoryDto result = categoryService.update(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("C1", result.getIdentifier());

        verify(modelMapper).map(dto, existing);
        verify(categoryRepository).save(existing);
    }

    @Test
    void update_CategoryNotFound() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("C1");

        when(categoryRepository.findByIdentifier("C1")).thenReturn(null);

        CategoryDto result = categoryService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteTest() {

        Category category = new Category();

        when(categoryRepository.findByIdentifier("C1")).thenReturn(category);

        categoryService.delete("C1");

        verify(categoryRepository).findByIdentifier("C1");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Category category1 = new Category();
        Category category2 = new Category();

        Page<Category> page = new PageImpl<>(List.of(category1, category2), pageable, 2);

        List<CategoryDto> dtoList = List.of(new CategoryDto(), new CategoryDto());

        when(categoryRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(dtoList);

        WsDto<CategoryDto> result = categoryService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getContent().size());
        Assertions.assertEquals(0, result.getPage());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(2, result.getTotalRecords());

        verify(categoryRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Category> page = new PageImpl<>(List.of(), pageable, 0);

        when(categoryRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of());

        WsDto<CategoryDto> result = categoryService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getContent().isEmpty());
        Assertions.assertEquals(0, result.getTotalRecords());

        verify(categoryRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void findAllCategoriesWithNoSuper_Test() {

        Category c1 = new Category();
        c1.setSuperCategory(new ArrayList<>());

        Category c2 = new Category();
        c2.setSuperCategory(List.of("Parent"));

        when(categoryRepository.findAll()).thenReturn(List.of(c1, c2));

        List<CategoryDto> dtoList = List.of(new CategoryDto());

        when(modelMapper.map(eq(List.of(c2)), any(Type.class))).thenReturn(dtoList);

        List<CategoryDto> result = categoryService.findAllCategoriesWithNoSuper();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());

        verify(categoryRepository).findAll();
    }

    @Test
    void findAllSpecification_Test() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Category> specification = mock(Specification.class);

        Category category = new Category();

        Page<Category> page = new PageImpl<>(List.of(category), pageable, 1);

        List<CategoryDto> dtoList = List.of(new CategoryDto());

        when(categoryRepository.findAll(specification, pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(dtoList);

        WsDto<CategoryDto> result =
                categoryService.findAll(specification, pageable, "category");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getContent().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(0, result.getPage());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals("category", result.getKeyword());

        verify(categoryRepository).findAll(specification, pageable);
    }
}