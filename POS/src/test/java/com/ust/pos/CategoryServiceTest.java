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
        categoryDto.setIdentifier("Chips");

        Mockito.when(categoryRepository.findByIdentifier("Chips")).thenReturn(null);

        Category category = new Category();

        Mockito.when(modelMapper.map(categoryDto, Category.class)).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);

        CategoryDto response = categoryService.save(categoryDto);

        Assertions.assertEquals("Chips", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getSuperCategory());
    }

    @Test
    void saveTestFailure() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Chips");

        Category category = new Category();

        Mockito.when(categoryRepository.findByIdentifier("Chips")).thenReturn(category);

        CategoryDto response = categoryService.save(categoryDto);

        Assertions.assertEquals("Chips", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Category category = new Category();
        category.setIdentifier("Chips");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Chips");

        Mockito.when(categoryRepository.findByIdentifier("Chips")).thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        CategoryDto response = categoryService.findByIdentifier("Chips");

        Assertions.assertEquals("Chips", response.getIdentifier());
    }

    @Test
    void updateTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Chips");

        Category existingCategory = new Category();
        existingCategory.setIdentifier("Chips");

        Mockito.when(categoryRepository.findByIdentifier("Chips")).thenReturn(existingCategory);
        Mockito.when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);

        CategoryDto response = categoryService.update(categoryDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Chips");

        Mockito.when(categoryRepository.findByIdentifier("Chips")).thenReturn(null);

        CategoryDto response = categoryService.update(categoryDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(categoryRepository).deleteByIdentifier("Chips");

        categoryService.deleteByIdentifier("Chips");

        Mockito.verify(categoryRepository).deleteByIdentifier("Chips");
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

        WsDto<CategoryDto> response = categoryService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Chips", response.getDtoList().get(0).getIdentifier());
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

        WsDto<CategoryDto> response = categoryService.findAll(null);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Chips", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findSubCategoriesTest() {
        Category category = new Category();
        category.setIdentifier("Chips");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Chips");

        List<Category> categories = List.of(category);

        Mockito.when(categoryRepository.findBySuperCategoryNotNull()).thenReturn(categories);
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        List<CategoryDto> response = categoryService.findSubCategories();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Chips", response.get(0).getIdentifier());

        Mockito.verify(categoryRepository).findBySuperCategoryNotNull();
        Mockito.verify(modelMapper).map(category, CategoryDto.class);
    }

}