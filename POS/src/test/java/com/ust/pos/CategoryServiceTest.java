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

    // SAVE

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
        Assertions.assertNull(response.getSuperCategory());
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

    // UPDATE

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
        Assertions.assertNull(response.getSuperCategory());

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

    // FIND BY IDENTIFIER

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

    // FIND ALL

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

    // FIND ALL WITHOUT NULL

    @Test
    void findAllWithoutNullTest() {
        CategoryDto dto1 = new CategoryDto();
        dto1.setSuperCategory("Parent");

        CategoryDto dto2 = new CategoryDto();
        dto2.setSuperCategory(null);

        List<Category> entities = List.of(new Category(), new Category());
        List<CategoryDto> dtoList = List.of(dto1, dto2);

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();

        Mockito.when(categoryRepository.findAll())
                .thenReturn(entities);
        Mockito.when(modelMapper.map(entities, listType))
                .thenReturn(dtoList);

        List<CategoryDto> result = categoryService.findAllWithoutNull();

        Assertions.assertEquals(1, result.size());
        Assertions.assertNotNull(result.get(0).getSuperCategory());
    }

    // DELETE

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
    void findAll_ShouldReturnCategoryDtos() {

        // Arrange
        List<Category> categories = List.of(new Category());

        List<CategoryDto> categoryDtos = List.of(new CategoryDto());

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();

        Mockito.when(categoryRepository.findAll())
                .thenReturn(categories);

        Mockito.when(modelMapper.map(categories, listType))
                .thenReturn(categoryDtos);

        // Act
        List<CategoryDto> response = categoryService.findAll();

        // Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());

        Mockito.verify(categoryRepository).findAll();
        Mockito.verify(modelMapper).map(categories, listType);
    }
}