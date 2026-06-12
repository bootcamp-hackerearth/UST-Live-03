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
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("S1");
        Mockito.when(categoryRepository.findByIdentifier("S1")).thenReturn(null);
        Category category = new Category();
        Mockito.when(modelMapper.map(categoryDto, Category.class)).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        CategoryDto response = categoryService.save(categoryDto);
        Assertions.assertEquals("S1", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("S1");
        Category existingCategory = new Category();
        Mockito.when(categoryRepository.findByIdentifier("S1")).thenReturn(existingCategory);
        CategoryDto response = categoryService.save(categoryDto);
        Assertions.assertEquals("S1", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Category category = new Category();
        category.setIdentifier("S1");
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("S1");
        Mockito.when(categoryRepository.findByIdentifier("S1")).thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);
        CategoryDto response = categoryService.findByIdentifier("S1");
        Assertions.assertEquals("S1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("S1");
        Category existingCategory = new Category();
        existingCategory.setIdentifier("S1");
        Mockito.when(categoryRepository.findByIdentifier("S1"))
                .thenReturn(existingCategory);
        Mockito.when(categoryRepository.save(existingCategory))
                .thenReturn(existingCategory);
        CategoryDto response = categoryService.update(categoryDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("S1");
        Mockito.when(categoryRepository.findByIdentifier("S1"))
                .thenReturn(null);
        CategoryDto response = categoryService.update(categoryDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(categoryRepository)
                .deleteByIdentifier("S1");
        categoryService.delete("S1");
        Mockito.verify(categoryRepository).deleteByIdentifier("S1");
    }

    @Test
    void findAllTest() {
        Category category = new Category();
        category.setIdentifier("C1");
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("C1");
        List<Category> categoryList = List.of(category);
        List<CategoryDto> categoryDtoList = List.of(categoryDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = new PageImpl<>(categoryList);
        Mockito.when(categoryRepository.findAll(pageable))
                .thenReturn(categoryPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(categoryList),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(categoryDtoList);
        WsDto<CategoryDto> response = categoryService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("C1", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findBySuperCategoryIsNotTest() {
        Category category = new Category();
        category.setIdentifier("C1");
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("C1");
        List<Category> categories = List.of(category);
        List<CategoryDto> categoryDtos = List.of(categoryDto);
        Mockito.when(categoryRepository.findBySuperCategoryIsNot(Mockito.anyString()))
                .thenReturn(categories);
        Mockito.when(modelMapper.map(
                Mockito.eq(categories),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(categoryDtos);
        List<CategoryDto> response =
                categoryService.findBySuperCategoryNotNull();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void updateStatusTest() {
        Category category = new Category();
        category.setIdentifier("S1");
        Mockito.when(categoryRepository.findByIdentifier("S1"))
                .thenReturn(category);
        Mockito.when(categoryRepository.save(category))
                .thenReturn(category);
        categoryService.updateStatus("S1", true);
        Mockito.verify(categoryRepository).save(category);
    }

    @Test
    void findAllActiveTest() {
        Category category = new Category();
        category.setIdentifier("S1");
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("S1");
        List<Category> shelves = List.of(category);
        List<CategoryDto> categoryDtos = List.of(categoryDto);
        Mockito.when(categoryRepository.findByStatus(true)).thenReturn(shelves);
        Mockito.when(modelMapper.map(
                Mockito.eq(shelves),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(categoryDtos);
        List<CategoryDto> response = categoryService.findAllActive();
        Assertions.assertEquals(1, response.size());
    }
}