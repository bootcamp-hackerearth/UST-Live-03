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
    void saveTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT1");

        Mockito.when(categoryRepository.findByIdentifier("CAT1"))
                .thenReturn(null);

        Category category = new Category();
        Mockito.when(modelMapper.map(categoryDto, Category.class))
                .thenReturn(category);
        Mockito.when(categoryRepository.save(category))
                .thenReturn(category);

        CategoryDto response = categoryService.save(categoryDto);

        Assertions.assertEquals("CAT1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT1");

        Mockito.when(categoryRepository.findByIdentifier("CAT1"))
                .thenReturn(new Category());

        CategoryDto response = categoryService.save(categoryDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT1");

        Category existingCategory = new Category();

        Mockito.when(categoryRepository.findByIdentifier("CAT1"))
                .thenReturn(existingCategory);
        Mockito.doNothing()
                .when(modelMapper).map(categoryDto, existingCategory);
        Mockito.when(categoryRepository.save(existingCategory))
                .thenReturn(existingCategory);

        CategoryDto response = categoryService.update(categoryDto);

        Assertions.assertNotNull(response);
        Mockito.verify(categoryRepository).save(existingCategory);
    }

    @Test
    void updateTestFailure() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT1");

        Mockito.when(categoryRepository.findByIdentifier("CAT1"))
                .thenReturn(null);

        CategoryDto response = categoryService.update(categoryDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(categoryRepository)
                .deleteByIdentifier("CAT1");

        boolean result = categoryService.delete("CAT1");

        Assertions.assertTrue(result);
        Mockito.verify(categoryRepository)
                .deleteByIdentifier("CAT1");
    }

    @Test
    void findByIdentifierTest() {
        Category category = new Category();
        category.setIdentifier("CAT1");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT1");

        Mockito.when(categoryRepository.findByIdentifier("CAT1"))
                .thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class))
                .thenReturn(categoryDto);

        CategoryDto response = categoryService.findByIdentifier("CAT1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("CAT1", response.getIdentifier());
    }

    // ✅ PAGINATION FIND ALL (CHANGED)
    @Test
    void findAllPaginationTest() {

        Category category = new Category();
        CategoryDto categoryDto = new CategoryDto();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage =
                new PageImpl<>(List.of(category), pageable, 1);

        Mockito.when(categoryRepository.findAll(pageable))
                .thenReturn(categoryPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(categoryPage.getContent()),
                Mockito.any(Type.class)
        )).thenReturn(List.of(categoryDto));

        List<CategoryDto> response = categoryService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findBySubCategoryTest() {
        Category subCategory = new Category();
        subCategory.setSupercategory("PARENT");

        CategoryDto categoryDto = new CategoryDto();

        Mockito.when(categoryRepository.findBySupercategoryIsNot(""))
                .thenReturn(List.of(subCategory));
        Mockito.when(modelMapper.map(subCategory, CategoryDto.class))
                .thenReturn(categoryDto);

        List<CategoryDto> response = categoryService.findBySubCategory();

        Assertions.assertEquals(1, response.size());
    }
}