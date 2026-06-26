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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void findByIdentifierTest() {
        Category category = new Category();
        category.setIdentifier("Admin");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");

        Mockito.when(categoryRepository.findByIdentifier("Admin")).thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        CategoryDto response = categoryService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void saveTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");

        Mockito.when(categoryRepository.findByIdentifier("Admin")).thenReturn(null);
        Category category = new Category();
        Mockito.when(modelMapper.map(categoryDto, Category.class)).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);

        CategoryDto response = categoryService.save(categoryDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void saveTestFailure() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");

        Category existingCategory = new Category();
        existingCategory.setIdentifier("Admin");
        existingCategory.setDeleted(false);

        Mockito.when(categoryRepository.findByIdentifier("Admin")).thenReturn(existingCategory);

        CategoryDto response = categoryService.save(categoryDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void saveTestFailurePreviouslyDeleted() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");

        Category existingCategory = new Category();
        existingCategory.setIdentifier("Admin");
        existingCategory.setDeleted(true);

        Mockito.when(categoryRepository.findByIdentifier("Admin")).thenReturn(existingCategory);

        CategoryDto response = categoryService.save(categoryDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void updateTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");

        Category existingCategory = new Category();
        existingCategory.setIdentifier("Admin");

        Mockito.when(categoryRepository.findByIdentifier("Admin")).thenReturn(existingCategory);
        Mockito.when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);

        CategoryDto response = categoryService.update(categoryDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");

        Mockito.when(categoryRepository.findByIdentifier("Admin")).thenReturn(null);

        CategoryDto response = categoryService.update(categoryDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Category category = new Category();
        category.setIdentifier("Admin");

        Mockito.when(categoryRepository.findByIdentifier("Admin")).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);

        boolean response = categoryService.delete("Admin");

        Assertions.assertEquals(true, response);
    }

    @Test
    void deleteTestFailure() {
        Mockito.when(categoryRepository.findByIdentifier("Admin")).thenReturn(null);

        boolean response = categoryService.delete("Admin");

        Assertions.assertEquals(false, response);
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Category category = new Category();
        List<Category> categories = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        CategoryDto categoryDto = new CategoryDto();
        List<CategoryDto> categoryDtos = List.of(categoryDto);

        Mockito.when(categoryRepository.findByDeletedFalse(pageable)).thenReturn(categoryPage);
        Mockito.when(modelMapper.map(Mockito.eq(categories), Mockito.any(java.lang.reflect.Type.class))).thenReturn(categoryDtos);

        WsDto<CategoryDto> response = categoryService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
    }

    @Test
    void findBySuperCategoryNotNullTest() {
        Category category = new Category();
        List<Category> categories = List.of(category);
        CategoryDto categoryDto = new CategoryDto();
        List<CategoryDto> categoryDtos = List.of(categoryDto);

        Mockito.when(categoryRepository.findByStatusTrueAndDeletedFalseAndSuperCategoryIsNot("")).thenReturn(categories);
        Mockito.when(modelMapper.map(Mockito.eq(categories), Mockito.any(java.lang.reflect.Type.class))).thenReturn(categoryDtos);

        List<CategoryDto> response = categoryService.findBySuperCategoryNotNull();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleTestActive() {
        Category category = new Category();
        category.setStatus(false);
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setStatus(true);

        Mockito.when(categoryRepository.findByIdentifier("Admin")).thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        CategoryDto response = categoryService.toggleStatus("Admin");

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void toggleTestInactive() {
        Category category = new Category();
        category.setStatus(true);
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setStatus(false);

        Mockito.when(categoryRepository.findByIdentifier("Admin")).thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        CategoryDto response = categoryService.toggleStatus("Admin");

        Assertions.assertFalse(response.isStatus());
    }

    @Test
    void findByStatusTest() {
        Category category = new Category();
        List<Category> categories = List.of(category);
        CategoryDto categoryDto = new CategoryDto();
        List<CategoryDto> categoryDtos = List.of(categoryDto);

        Mockito.when(categoryRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(categories);
        Mockito.when(modelMapper.map(Mockito.eq(categories), Mockito.any(java.lang.reflect.Type.class))).thenReturn(categoryDtos);

        List<CategoryDto> response = categoryService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }
}