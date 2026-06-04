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
        categoryDto.setIdentifier("Admin");

        Mockito.when(categoryRepository.findByIdentifier("Admin")).thenReturn(null);
        Category category = new Category();
        Mockito.when(modelMapper.map(categoryDto, Category.class)).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        CategoryDto response = categoryService.save(categoryDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess());

    }

    @Test
    void saveTestFailure() {

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");
        Category category = new Category();

        Mockito.when(categoryRepository.findByIdentifier("Admin")).thenReturn(category);
        CategoryDto response = categoryService.save(categoryDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");

        Assertions.assertEquals(false, response.isSuccess());

    }

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
    void updateTest() {

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");

        Category existingCategory = new Category();
        existingCategory.setIdentifier("Admin");

        Mockito.when(categoryRepository.findByIdentifier("Admin"))
                .thenReturn(existingCategory);
        Mockito.when(categoryRepository.save(existingCategory))
                .thenReturn(existingCategory);

        CategoryDto response = categoryService.update(categoryDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");

        Mockito.when(categoryRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        CategoryDto response = categoryService.update(categoryDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {

        Mockito.doNothing().when(categoryRepository)
                .deleteByIdentifier("Admin");

        categoryService.delete("Admin");

        Mockito.verify(categoryRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findAllTest() {

        Category category = new Category();
        category.setIdentifier("Admin");
        category.setStatus(true);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");

        List<Category> categories = List.of(category);
        List<CategoryDto> categoryDtos = List.of(categoryDto);

        Page<Category> categoryPage = new PageImpl<>(categories);

        Mockito.when(categoryRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(categoryPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(categories),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(categoryDtos);

        List<CategoryDto> response = categoryService.findAll(PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllActiveTest() {

        Category category = new Category();
        category.setIdentifier("Admin");
        category.setStatus(true);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");

        List<Category> categorys = List.of(category);
        List<CategoryDto> categoryDtos = List.of(categoryDto);

        Mockito.when(categoryRepository.findByStatus(true)).thenReturn(categorys);
        Mockito.when(modelMapper.map(
                Mockito.eq(categorys),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(categoryDtos);

        List<CategoryDto> response = categoryService.findAllActive();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void changeStatusTest() {

        Category category = new Category();
        category.setIdentifier("Admin");
        category.setStatus(false);

        Mockito.when(categoryRepository.findByIdentifier("Admin"))
                .thenReturn(category);

        Mockito.when(categoryRepository.save(category))
                .thenReturn(category);

        categoryService.changeStatus("Admin", true);

        Assertions.assertTrue(category.getStatus());

        Mockito.verify(categoryRepository).save(category);
    }
}