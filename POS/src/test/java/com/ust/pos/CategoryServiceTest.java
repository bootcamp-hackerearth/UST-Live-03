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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

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
    void findByIdentifierSuccessTest() {
        Category category = new Category();
        category.setIdentifier("CAT001");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");

        when(categoryRepository.findByIdentifier("CAT001"))
                .thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class))
                .thenReturn(dto);

        CategoryDto result = categoryService.findByIdentifier("CAT001");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("CAT001", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(categoryRepository.findByIdentifier("CAT001"))
                .thenReturn(null);

        CategoryDto result = categoryService.findByIdentifier("CAT001");

        Assertions.assertNull(result);
    }

    @Test
    void saveSuccessTest() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");

        Category category = new Category();

        when(categoryRepository.findByIdentifier("CAT001"))
                .thenReturn(null);
        when(modelMapper.map(dto, Category.class))
                .thenReturn(category);

        CategoryDto result = categoryService.save(dto);

        Assertions.assertEquals("CAT001", result.getIdentifier());

        verify(categoryRepository).save(category);
    }

    @Test
    void saveAlreadyExistsTest() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");

        Category existingCategory = new Category();
        existingCategory.setDeleted(false);

        when(categoryRepository.findByIdentifier("CAT001"))
                .thenReturn(existingCategory);

        CategoryDto result = categoryService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Category with identifier - CAT001 already exists",
                result.getMessage()
        );

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void saveDeletedCategoryTest() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");

        Category existingCategory = new Category();
        existingCategory.setDeleted(true);

        when(categoryRepository.findByIdentifier("CAT001"))
                .thenReturn(existingCategory);

        CategoryDto result = categoryService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Category with identifier - CAT001 was deleted , Please Contact the Administrator to add.",
                result.getMessage()
        );

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");

        Category existingCategory = new Category();
        existingCategory.setIdentifier("CAT001");

        when(categoryRepository.findByIdentifier("CAT001"))
                .thenReturn(existingCategory);

        CategoryDto result = categoryService.update(dto);

        Assertions.assertEquals("CAT001", result.getIdentifier());

        verify(modelMapper).map(dto, existingCategory);
        verify(categoryRepository).save(existingCategory);
    }

    @Test
    void updateFailureTest() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");

        when(categoryRepository.findByIdentifier("CAT001"))
                .thenReturn(null);

        CategoryDto result = categoryService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Category with identifier - CAT001 not found",
                result.getMessage()
        );

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Category category = new Category();

        when(categoryRepository.findByIdentifier("CAT001"))
                .thenReturn(category);

        categoryService.delete("CAT001");

        verify(categoryRepository).findByIdentifier("CAT001");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Category category1 = new Category();
        Category category2 = new Category();

        List<Category> categories = List.of(category1, category2);

        Page<Category> page = new PageImpl<>(categories, pageable, 2);

        List<CategoryDto> dtoList = List.of(
                new CategoryDto(),
                new CategoryDto()
        );

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();

        when(categoryRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(categories, listType))
                .thenReturn(dtoList);

        WsDto<CategoryDto> result = categoryService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getDtoList().size());
        Assertions.assertEquals(2, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    void findAllCategoriesWithNoSuperTest() {
        Category category1 = new Category();
        category1.setSuperCategory(List.of("SUPER1"));

        Category category2 = new Category();
        category2.setSuperCategory(List.of());

        Category category3 = new Category();
        category3.setSuperCategory(List.of("SUPER2"));

        List<Category> categories = List.of(
                category1,
                category2,
                category3
        );

        List<Category> filteredCategories = List.of(
                category1,
                category3
        );

        List<CategoryDto> dtoList = List.of(
                new CategoryDto(),
                new CategoryDto()
        );

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();

        when(categoryRepository.findAll())
                .thenReturn(categories);

        when(modelMapper.map(filteredCategories, listType))
                .thenReturn(dtoList);

        List<CategoryDto> result =
                categoryService.findAllCategoriesWithNoSuper();

        Assertions.assertEquals(2, result.size());

        verify(categoryRepository).findAll();
    }

    @Test
    void findAllCategoriesWithNoSuperEmptyTest() {
        Category category = new Category();
        category.setSuperCategory(List.of());

        List<Category> categories = List.of(category);

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();

        when(categoryRepository.findAll())
                .thenReturn(categories);

        when(modelMapper.map(List.of(), listType))
                .thenReturn(List.of());

        List<CategoryDto> result =
                categoryService.findAllCategoriesWithNoSuper();

        Assertions.assertTrue(result.isEmpty());
    }
}