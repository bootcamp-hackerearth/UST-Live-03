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
    void saveTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("category");
        Mockito.when(categoryRepository.findByIdentifier("category")).thenReturn(null);
        Category category = new Category();
        Mockito.when(modelMapper.map(categoryDto, Category.class)).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        CategoryDto response = categoryService.save(categoryDto);
        Assertions.assertEquals("category", response.getIdentifier());
        Assertions.assertEquals(true, response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("category");
        Category category = new Category();
        Mockito.when(categoryRepository.findByIdentifier("category")).thenReturn(category);
        CategoryDto response = categoryService.save(categoryDto);
        Assertions.assertEquals("category", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");
        Assertions.assertEquals(false, response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Category category = new Category();
        category.setIdentifier("category");
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("category");
        Mockito.when(categoryRepository.findByIdentifier("category")).thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);
        CategoryDto response = categoryService.findByIdentifier("category");
        Assertions.assertEquals("category", response.getIdentifier());
    }

    @Test
    void updateTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("category");
        Category existingCategory = new Category();
        existingCategory.setIdentifier("category");
        Mockito.when(categoryRepository.findByIdentifier("category")).thenReturn(existingCategory);
        Mockito.when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);
        CategoryDto response = categoryService.update(categoryDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("category");
        Mockito.when(categoryRepository.findByIdentifier("category")).thenReturn(null);
        CategoryDto response = categoryService.update(categoryDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(categoryRepository).deleteByIdentifier("category");
        categoryService.delete("category");
        Mockito.verify(categoryRepository).deleteByIdentifier("category");
    }

    @Test
    void findAllWithPageableTest() {
        Category category = new Category();
        category.setIdentifier("Chips");
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("Chips");
        List<Category> categorys = List.of(category);
        List<CategoryDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Category> categoryPage = new PageImpl<>(categorys);
        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        Mockito.when(modelMapper.map(Mockito.eq(categorys), Mockito.any(Type.class))).thenReturn(dtos);
        List<CategoryDto> response = categoryService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Chips", response.get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Category category = new Category();
        category.setIdentifier("Chips");
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("Chips");
        List<Category> categorys = List.of(category);
        List<CategoryDto> dtos = List.of(dto);
        Mockito.when(categoryRepository.findAll()).thenReturn(categorys);
        Mockito.when(modelMapper.map(Mockito.eq(categorys), Mockito.any(Type.class))).thenReturn(dtos);
        List<CategoryDto> response = categoryService.findAll(null);
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleStatusSuccessTest() {
        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setStatus(false);
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");
        dto.setStatus(true);
        Mockito.when(categoryRepository.findByIdentifier("CAT001")).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(dto);
        CategoryDto response = categoryService.toggleStatus("CAT001", true);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("CAT001", response.getIdentifier());
        Assertions.assertTrue(category.isStatus());
        Assertions.assertTrue(response.isStatus());
        Mockito.verify(categoryRepository).findByIdentifier("CAT001");
        Mockito.verify(categoryRepository).save(category);
        Mockito.verify(modelMapper).map(category, CategoryDto.class);
    }

    @Test
    void toggleStatusFailureTest() {
        Mockito.when(categoryRepository.findByIdentifier("CAT001")).thenReturn(null);
        Mockito.when(modelMapper.map(null, CategoryDto.class)).thenReturn(null);
        CategoryDto response = categoryService.toggleStatus("CAT001", true);
        Assertions.assertNull(response);
        Mockito.verify(categoryRepository).findByIdentifier("CAT001");
        Mockito.verify(categoryRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findSubCategoriesTest() {
        Category category = new Category();
        category.setIdentifier("Chips");
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Chips");
        List<Category> categories = List.of(category);
        List<CategoryDto> categoryDtos = List.of(categoryDto);
        Mockito.when(categoryRepository.findBySuperCategoryIsNotNull()).thenReturn(categories);
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        Mockito.when(modelMapper.map(categories, listType)).thenReturn(categoryDtos);
        List<CategoryDto> response = categoryService.findSubCategories();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Chips", response.get(0).getIdentifier());
        Mockito.verify(categoryRepository).findBySuperCategoryIsNotNull();
        Mockito.verify(modelMapper).map(categories, listType);
    }
}