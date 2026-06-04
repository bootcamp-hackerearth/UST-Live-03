package com.ust.pos;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import com.ust.pos.category.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

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
    void saveTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("C0020");

        Mockito.when(categoryRepository.findByIdentifier("C0020")).thenReturn(null);
        Category category = new Category();

        Mockito.when(modelMapper.map(categoryDto, Category.class)).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        CategoryDto response = categoryService.save(categoryDto);
        Assertions.assertEquals("C0020", response.getIdentifier());
        Assertions.assertEquals(true, response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("C0020");
        Category category = new Category();

        Mockito.when(categoryRepository.findByIdentifier("C0020")).thenReturn(category);
        CategoryDto response = categoryService.save(categoryDto);
        Assertions.assertEquals("C0020", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");
        Assertions.assertEquals(false, response.isSuccess());

    }

    @Test
    void findByIdentifierTest() {
        Category category = new Category();
        category.setIdentifier("C0020");
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("C0020");

        Mockito.when(categoryRepository.findByIdentifier("C0020")).thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);
        CategoryDto response = categoryService.findByIdentifier("C0020");
        Assertions.assertEquals("C0020", response.getIdentifier());
    }

    @Test
    void updateTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("C0020");
        Category existingCategory = new Category();
        existingCategory.setIdentifier("C0020");

        Mockito.when(categoryRepository.findByIdentifier("C0020")).thenReturn(existingCategory);
        Mockito.when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);
        CategoryDto response = categoryService.update(categoryDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("C0020");

        Mockito.when(categoryRepository.findByIdentifier("C0020")).thenReturn(null);
        CategoryDto response = categoryService.update(categoryDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(categoryRepository).deleteByIdentifier("C0020");
        categoryService.delete("C0020");
        Mockito.verify(categoryRepository).deleteByIdentifier("C0020");
    }

    @Test
    void findAllTest() {
        Category category = new Category();
        category.setIdentifier("C0020");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("C0020");

        List<Category> categories = List.of(category);
        List<CategoryDto> categoryDtos = List.of(categoryDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(categories),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(categoryDtos);
        List<CategoryDto> response = categoryService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("C0020", response.get(0).getIdentifier());
    }

    @Test
    void findChildCategoriesTest() {
        Category category = new Category();
        category.setIdentifier("C0021");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("C0021");

        List<Category> categories = List.of(category);
        List<CategoryDto> categoryDtos = List.of(categoryDto);

        Mockito.when(categoryRepository.findBySuperCategoryIsNotNull()).thenReturn(categories);
        Mockito.when(modelMapper.map(Mockito.eq(categories),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(categoryDtos);
        List<CategoryDto> response = categoryService.findChildCategories();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("C0021", response.get(0).getIdentifier());
    }
}