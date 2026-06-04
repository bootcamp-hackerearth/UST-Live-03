package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
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

    @Mock()
    private ModelMapper modelMapper;

    private Category category;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {

        categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT01");
        categoryDto.setSuccess(true);

        category = new Category();
        category.setIdentifier("CAT01");
        category.setSuperCategory("PARENT");
        category.setStatus(true);
    }

    @Test
    void testSave_Success() {

        when(categoryRepository.findByIdentifier("CAT01"))
                .thenReturn(null);

        when(modelMapper.map(categoryDto, Category.class))
                .thenReturn(category);

        CategoryDto response =
                categoryService.save(categoryDto);

        assertNotNull(response);
        assertEquals("CAT01", response.getIdentifier());
        assertTrue(response.isSuccess());
        assertNull(response.getMessage());

        verify(categoryRepository).findByIdentifier("CAT01");
        verify(modelMapper).map(categoryDto, Category.class);
        verify(categoryRepository).save(category);
    }

    @Test
    void testSave_Failure_AlreadyExists() {

        when(categoryRepository.findByIdentifier("CAT01"))
                .thenReturn(category);

        CategoryDto response =
                categoryService.save(categoryDto);

        assertNotNull(response);
        assertFalse(response.isSuccess());

        assertEquals(
                "Category with identifier - CAT01 already exists",
                response.getMessage()
        );

        verify(categoryRepository).findByIdentifier("CAT01");
        verify(categoryRepository, never()).save(any());
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void testUpdate_Success() {

        when(categoryRepository.findByIdentifier("CAT01"))
                .thenReturn(category);

        CategoryDto response =
                categoryService.update(categoryDto);

        assertNotNull(response);
        assertEquals("CAT01", response.getIdentifier());

        verify(categoryRepository).findByIdentifier("CAT01");
        verify(modelMapper).map(categoryDto, category);
        verify(categoryRepository).save(category);
    }

    @Test
    void testUpdate_Failure_NotFound() {

        when(categoryRepository.findByIdentifier("CAT01"))
                .thenReturn(null);

        CategoryDto response =
                categoryService.update(categoryDto);

        assertNotNull(response);
        assertFalse(response.isSuccess());

        assertEquals(
                "category with identifier - CAT01 not found",
                response.getMessage()
        );

        verify(categoryRepository).findByIdentifier("CAT01");
        verify(categoryRepository, never()).save(any());
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void testDelete() {

        doNothing().when(categoryRepository)
                .deleteByIdentifier("CAT01");

        categoryService.delete("CAT01");

        verify(categoryRepository).deleteByIdentifier("CAT01");
    }

    @Test
    void testFindByIdentifier_Success() {

        when(categoryRepository.findByIdentifier("CAT01"))
                .thenReturn(category);

        when(modelMapper.map(category, CategoryDto.class))
                .thenReturn(categoryDto);

        CategoryDto response =
                categoryService.findByIdentifier("CAT01");

        assertNotNull(response);
        assertEquals("CAT01", response.getIdentifier());

        verify(categoryRepository).findByIdentifier("CAT01");
        verify(modelMapper).map(category, CategoryDto.class);
    }

    @Test
    void testFindByIdentifier_NotFound() {

        when(categoryRepository.findByIdentifier("CAT01"))
                .thenReturn(null);

        when(modelMapper.map(null, CategoryDto.class))
                .thenReturn(null);

        CategoryDto response =
                categoryService.findByIdentifier("CAT01");

        assertNull(response);

        verify(categoryRepository).findByIdentifier("CAT01");
        verify(modelMapper).map(null, CategoryDto.class);
    }

    @Test
    void testFindAll_WithData() {

        List<Category> categories =
                List.of(category);

        List<CategoryDto> dtoList =
                List.of(categoryDto);

        Pageable pageable =
                PageRequest.of(0, 50, Sort.by(new ArrayList<>()));

        Page<Category> categoryPage =
                new PageImpl<>(categories, pageable, categories.size());

        Type listType =
                new TypeToken<List<CategoryDto>>() {
                }.getType();

        when(categoryRepository.findAll(pageable))
                .thenReturn(categoryPage);

        when(modelMapper.map(categoryPage.getContent(), listType))
                .thenReturn(dtoList);

        List<CategoryDto> result =
                categoryService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(categoryRepository).findAll(pageable);
        verify(modelMapper).map(categoryPage.getContent(), listType);
    }

    @Test
    void testFindAll_EmptyList() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Category> emptyPage =
                new PageImpl<>(Collections.emptyList());

        Type listType =
                new TypeToken<List<CategoryDto>>() {
                }.getType();

        when(categoryRepository.findAll(pageable))
                .thenReturn(emptyPage);

        when(modelMapper.map(emptyPage.getContent(), listType))
                .thenReturn(Collections.emptyList());

        List<CategoryDto> result =
                categoryService.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(categoryRepository).findAll(pageable);
        verify(modelMapper).map(emptyPage.getContent(), listType);
    }

    @Test
    void testFindAllWithSuperCategory_WithFilteredData() {

        Category category1 = new Category();
        category1.setIdentifier("CAT01");
        category1.setSuperCategory("PARENT");

        Category category2 = new Category();
        category2.setIdentifier("CAT02");
        category2.setSuperCategory("");

        List<Category> allCategories =
                List.of(category1, category2);

        List<Category> filteredCategories =
                List.of(category1);

        List<CategoryDto> dtoList =
                List.of(categoryDto);

        Type listType =
                new TypeToken<List<CategoryDto>>() {
                }.getType();

        when(categoryRepository.findAll())
                .thenReturn(allCategories);

        when(modelMapper.map(filteredCategories, listType))
                .thenReturn(dtoList);

        List<CategoryDto> result =
                categoryService.findAllWithSuperCategory();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(categoryRepository).findAll();
        verify(modelMapper).map(filteredCategories, listType);
    }

    @Test
    void testFindAllWithSuperCategory_EmptyResult() {

        Category category1 = new Category();
        category1.setIdentifier("CAT01");
        category1.setSuperCategory("");

        Category category2 = new Category();
        category2.setIdentifier("CAT02");
        category2.setSuperCategory("");

        List<Category> allCategories =
                List.of(category1, category2);

        Type listType =
                new TypeToken<List<CategoryDto>>() {
                }.getType();

        when(categoryRepository.findAll())
                .thenReturn(allCategories);

        when(modelMapper.map(Collections.emptyList(), listType))
                .thenReturn(Collections.emptyList());

        List<CategoryDto> result =
                categoryService.findAllWithSuperCategory();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(categoryRepository).findAll();
        verify(modelMapper).map(Collections.emptyList(), listType);
    }
}