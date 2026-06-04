package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImplementation;
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
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImplementation categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveCategorySuccess() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("ELEC");

        Category category = new Category();

        Mockito.when(categoryRepository.findByIdentifier("ELEC"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(categoryDto, Category.class))
                .thenReturn(category);

        CategoryDto response = categoryService.save(categoryDto);

        Assertions.assertEquals("ELEC", response.getIdentifier());
        Assertions.assertNull(response.getMessage());

        Mockito.verify(categoryRepository).save(category);
    }
    @Test
    void findAll_WithPagination_ShouldReturnCategoryDtos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        List<Category> categories = List.of(new Category());
        Page<Category> categoryPage = new PageImpl<>(categories);

        List<CategoryDto> categoryDtos = List.of(new CategoryDto());

        Type listType = new TypeToken<List<CategoryDto>>() {}.getType();

        Mockito.when(categoryRepository.findAll(pageable))
                .thenReturn(categoryPage);

        Mockito.when(modelMapper.map(categories, listType))
                .thenReturn(categoryDtos);

        // Act
        List<CategoryDto> response = categoryService.findAll(pageable);

        // Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());

        Mockito.verify(categoryRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(categories, listType);
    }

    @Test
    void saveCategoryAlreadyExists() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("ELEC");

        Category existingCategory = new Category();

        Mockito.when(categoryRepository.findByIdentifier("ELEC"))
                .thenReturn(existingCategory);

        CategoryDto response = categoryService.save(categoryDto);

        Assertions.assertEquals(
                "Category with identifier - ELEC already exists",
                response.getMessage()
        );

        Mockito.verify(categoryRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateCategorySuccess() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("ELEC");

        Category existingCategory = new Category();

        Mockito.when(categoryRepository.findByIdentifier("ELEC"))
                .thenReturn(existingCategory);

        CategoryDto response = categoryService.update(categoryDto);

        Assertions.assertNull(response.getMessage());

        Mockito.verify(modelMapper)
                .map(categoryDto, existingCategory);
        Mockito.verify(categoryRepository)
                .save(existingCategory);
    }

    @Test
    void updateCategoryNotFound() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("ELEC");

        Mockito.when(categoryRepository.findByIdentifier("ELEC"))
                .thenReturn(null);

        CategoryDto response = categoryService.update(categoryDto);

        Assertions.assertEquals(
                "Category with identifier - ELEC not found",
                response.getMessage()
        );

        Mockito.verify(categoryRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void deleteCategoryTest() {
        String identifier = "ELEC";

        Mockito.doNothing()
                .when(categoryRepository)
                .deleteByIdentifier(identifier);

        categoryService.delete(identifier);

        Mockito.verify(categoryRepository)
                .deleteByIdentifier(identifier);
    }

    @Test
    void findAllCategoriesTest() {
        Category category1 = new Category();
        Category category2 = new Category();

        List<Category> categoryList = new ArrayList<>();
        categoryList.add(category1);
        categoryList.add(category2);

        CategoryDto dto1 = new CategoryDto();
        CategoryDto dto2 = new CategoryDto();

        List<CategoryDto> dtoList = new ArrayList<>();
        dtoList.add(dto1);
        dtoList.add(dto2);

        Mockito.when(categoryRepository.findAll())
                .thenReturn(categoryList);
        Mockito.when(modelMapper.map(
                Mockito.eq(categoryList),
                Mockito.any(Type.class))
        ).thenReturn(dtoList);

        List<CategoryDto> response = categoryService.findAll();

        Assertions.assertEquals(2, response.size());

        Mockito.verify(categoryRepository).findAll();
    }

    @Test
    void findByIdentifierTest() {
        Category category = new Category();
        category.setIdentifier("ELEC");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("ELEC");

        Mockito.when(categoryRepository.findByIdentifier("ELEC"))
                .thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class))
                .thenReturn(dto);

        CategoryDto response = categoryService.findByIdentifier("ELEC");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("ELEC", response.getIdentifier());
    }
}
