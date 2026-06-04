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
import org.modelmapper.TypeToken;
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
    void saveTest_Success() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");
        categoryDto.setSuperCategory("");
        Category category = new Category();
        Mockito.when(categoryRepository.findByIdentifier("Admin"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(categoryDto, Category.class))
                .thenReturn(category);
        Mockito.when(categoryRepository.save(category))
                .thenReturn(category);
        CategoryDto response = categoryService.save(categoryDto);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(categoryRepository).save(category);
    }

    @Test
    void saveTest_Failure_WhenCategoryExists() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");
        Category existingCategory = new Category();
        Mockito.when(categoryRepository.findByIdentifier("Admin"))
                .thenReturn(existingCategory);
        CategoryDto response = categoryService.save(categoryDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(categoryRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateTest_Success() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");
        categoryDto.setSuperCategory("");
        Category existingCategory = new Category();
        existingCategory.setIdentifier("Admin");
        Category mappedCategory = new Category();
        Mockito.when(categoryRepository.findByIdentifier("Admin"))
                .thenReturn(existingCategory);
        Mockito.when(modelMapper.map(categoryDto, Category.class))
                .thenReturn(mappedCategory);
        Mockito.when(categoryRepository.save(mappedCategory))
                .thenReturn(mappedCategory);
        CategoryDto response = categoryService.update(categoryDto);
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(categoryRepository).save(mappedCategory);
    }

    @Test
    void updateTest_Failure_WhenCategoryNotFound() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");
        Mockito.when(categoryRepository.findByIdentifier("Admin"))
                .thenReturn(null);
        CategoryDto response = categoryService.update(categoryDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(categoryRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {
        Category category = new Category();
        category.setIdentifier("Admin");
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");
        Mockito.when(categoryRepository.findByIdentifier("Admin"))
                .thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class))
                .thenReturn(categoryDto);
        CategoryDto response = categoryService.findByIdentifier("Admin");
        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void findAllTest() {
        List<Category> categories = List.of(new Category());
        List<CategoryDto> categoryDtos = List.of(new CategoryDto());
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        Mockito.when(categoryRepository.findAll())
                .thenReturn(categories);
        Mockito.when(modelMapper.map(categories, listType))
                .thenReturn(categoryDtos);
        List<CategoryDto> response = categoryService.findAll();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findBySuperCategoryNotNull() {
        Category category1 = new Category();
        category1.setSuperCategory("Parent");
        List<Category> entities = List.of(category1);
        CategoryDto dto1 = new CategoryDto();
        dto1.setSuperCategory("Parent");
        List<CategoryDto> dtoList = List.of(dto1);
        Type listType = new TypeToken<List<CategoryDto>>() {}.getType();
        Mockito.when(categoryRepository.findBySuperCategoryIsNot(""))
                .thenReturn(entities);
        Mockito.when(modelMapper.map(entities, listType))
                .thenReturn(dtoList);
        List<CategoryDto> result = categoryService.findBySuperCategoryNotNull();
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Parent", result.get(0).getSuperCategory());

        Mockito.verify(categoryRepository)
                .findBySuperCategoryIsNot("");
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(categoryRepository)
                .deleteByIdentifier("Admin");
        categoryService.delete("Admin");
        Mockito.verify(categoryRepository)
                .deleteByIdentifier("Admin");
    }

    @Test
    void findAll_WithPagination_ShouldReturnCategoryDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(new Category());
        Page<Category> categoryPage = new PageImpl<>(categories);
        List<CategoryDto> categoryDtos = List.of(new CategoryDto());
        Type listType = new TypeToken<List<CategoryDto>>() {}.getType();
        Mockito.when(categoryRepository.findAll(pageable))
                .thenReturn(categoryPage);
        Mockito.when(modelMapper.map(categories, listType))
                .thenReturn(categoryDtos);
        List<CategoryDto> response = categoryService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Mockito.verify(categoryRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(categories, listType);
    }
}