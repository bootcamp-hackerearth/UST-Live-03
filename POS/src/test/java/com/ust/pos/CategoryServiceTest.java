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

import java.lang.reflect.Type;
import java.util.List;


@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;


    @Test
    void saveTestSuccess() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Category1");

        Mockito.when(categoryRepository.findByIdentifier("Category1")).thenReturn(null);
        CategoryDto response = categoryService.save(categoryDto);

        Assertions.assertEquals("Category1", response.getIdentifier());
        Assertions.assertEquals(true, response.isSuccess());
    }

    @Test
    void saveTestWithEmptySuperCategory() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Category1");
        categoryDto.setSuperCategory("");

        Mockito.when(categoryRepository.findByIdentifier("Category1"))
                .thenReturn(null);

        CategoryDto response = categoryService.save(categoryDto);

        Assertions.assertNull(categoryDto.getSuperCategory());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Category1");
        Category category = new Category();

        Mockito.when(categoryRepository.findByIdentifier("Category1")).thenReturn(category);
        CategoryDto response = categoryService.save(categoryDto);

        Assertions.assertEquals("Category1", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");

        Assertions.assertEquals(false, response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Category category = new Category();
        category.setIdentifier("Category1");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Category1");

        Mockito.when(categoryRepository.findByIdentifier("Category1")).thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        CategoryDto response = categoryService.findByIdentifier("Category1");

        Assertions.assertEquals("Category1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Category1");

        Category existingCategory = new Category();
        existingCategory.setIdentifier("Category1");

        Mockito.when(categoryRepository.findByIdentifier("Category1"))
                .thenReturn(existingCategory);

        Mockito.when(categoryRepository.save(existingCategory))
                .thenReturn(existingCategory);

        CategoryDto response = categoryService.update(categoryDto);
        Mockito.verify(modelMapper).map(categoryDto, existingCategory);
        Mockito.verify(categoryRepository).save(existingCategory);

        Assertions.assertNotNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess());
    }


    @Test
    void updateTestFailure() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Category1");

        Mockito.when(categoryRepository.findByIdentifier("Category1"))
                .thenReturn(null);

        CategoryDto response = categoryService.update(categoryDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {

        Mockito.doNothing().when(categoryRepository)
                .deleteByIdentifier("Category1");

        categoryService.delete("Category1");

        Mockito.verify(categoryRepository).deleteByIdentifier("Category1");
    }

    @Test
    void findAllWithPageableTest() {
        Category category = new Category();
        category.setIdentifier("Category1");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Category1");

        List<Category> categories = List.of(category);
        List<CategoryDto> categoryDtos = List.of(categoryDto);

        Pageable pageable = PageRequest.of(0, 5);
        Page<Category> categoryPage = new PageImpl<>(categories);

        Mockito.when(categoryRepository.findAll(pageable))
                .thenReturn(categoryPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(categories),
                Mockito.any(Type.class)
        )).thenReturn(categoryDtos);

        List<CategoryDto> response = categoryService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Category1", response.get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Category category = new Category();
        category.setIdentifier("Category1");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Category1");

        List<Category> categories = List.of(category);
        List<CategoryDto> categoryDtos = List.of(categoryDto);

        Mockito.when(categoryRepository.findAll())
                .thenReturn(categories);

        Mockito.when(modelMapper.map(
                Mockito.eq(categories),
                Mockito.any(Type.class)
        )).thenReturn(categoryDtos);

        List<CategoryDto> response = categoryService.findAll(null);

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findBySuperCategoryIsNotNullAndStatusTrueTest() {
        Category category = new Category();
        category.setIdentifier("Category1");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Category1");

        List<Category> categories = List.of(category);
        List<CategoryDto> categoryDtos = List.of(categoryDto);

        Mockito.when(categoryRepository.findBySuperCategoryIsNotNullAndStatusTrue())
                .thenReturn(categories);

        Mockito.when(modelMapper.map(
                Mockito.eq(categories),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(categoryDtos);

        List<CategoryDto> response =
                categoryService.findBySuperCategoryIsNotNullAndStatusTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void updateStatusSuccessTest() {
        Category category = new Category();
        category.setIdentifier("Category1");
        category.setStatus(false);

        Mockito.when(categoryRepository.findByIdentifier("Category1"))
                .thenReturn(category);

        CategoryDto response = categoryService.updateStatus("Category1", true);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Status updated successfully", response.getMessage());
        Assertions.assertTrue(category.isStatus());
    }

    @Test
    void updateStatusFailureTest() {
        Mockito.when(categoryRepository.findByIdentifier("Category1"))
                .thenReturn(null);

        CategoryDto response = categoryService.updateStatus("Category1", true);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Category not found", response.getMessage());
    }
}