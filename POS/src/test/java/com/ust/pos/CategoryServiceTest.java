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

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Electronics");

        Category category = new Category();

        Mockito.when(categoryRepository.findByIdentifier("Electronics")).thenReturn(null);
        Mockito.when(modelMapper.map(categoryDto, Category.class)).thenReturn(category);

        CategoryDto response = categoryService.save(categoryDto);

        Assertions.assertEquals("Electronics", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(categoryRepository).save(category);
    }

    @Test
    void saveFailureTest() {

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Electronics");

        Category category = new Category();
        category.setIdentifier("Electronics");

        Mockito.when(categoryRepository.findByIdentifier("Electronics")).thenReturn(category);

        CategoryDto response = categoryService.save(categoryDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateSuccessTest() {

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Electronics");

        Category category = new Category();
        category.setIdentifier("Electronics");

        Mockito.when(categoryRepository.findByIdentifier("Electronics")).thenReturn(category);

        CategoryDto response = categoryService.update(categoryDto);

        Assertions.assertEquals("Electronics", response.getIdentifier());
        verify(categoryRepository).save(category);
    }

    @Test
    void updateFailureTest() {

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Electronics");

        Mockito.when(categoryRepository.findByIdentifier("Electronics")).thenReturn(null);

        CategoryDto response = categoryService.update(categoryDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteSuccessTest() {

        categoryService.delete("Electronics");

        verify(categoryRepository).deleteByIdentifier("Electronics");
    }

    @Test
    void findByIdentifierSuccessTest() {

        Category category = new Category();
        category.setIdentifier("Electronics");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Electronics");

        Mockito.when(categoryRepository.findByIdentifier("Electronics")).thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        CategoryDto response = categoryService.findByIdentifier("Electronics");

        Assertions.assertEquals("Electronics", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {

        Mockito.when(categoryRepository.findByIdentifier("Electronics")).thenReturn(null);

        CategoryDto response = categoryService.findByIdentifier("Electronics");

        Assertions.assertNull(response);
    }

    @Test
    void findAllSuccessTest() {

        Category c1 = new Category();
        c1.setIdentifier("Electronics");

        Category c2 = new Category();
        c2.setIdentifier("Fashion");

        List<Category> categories = List.of(c1, c2);

        CategoryDto d1 = new CategoryDto();
        d1.setIdentifier("Electronics");

        CategoryDto d2 = new CategoryDto();
        d2.setIdentifier("Fashion");

        List<CategoryDto> categoryDtos = List.of(d1, d2);

        Page<Category> page = new PageImpl<>(categories);
        Pageable pageable = PageRequest.of(0, 20);

        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(categories), Mockito.any(Type.class))).thenReturn(categoryDtos);

        WsDto<CategoryDto> result = categoryService.findAll(pageable);

        Assertions.assertEquals(2, result.getDtoList().size());
    }

    @Test
    void findAllWithSuperCategoryEmptyTest() {

        Category cat1 = new Category();
        Category cat2 = new Category();

        List<Category> categories = List.of(cat1, cat2);

        CategoryDto d1 = new CategoryDto();
        d1.setIdentifier("Electronics");
        d1.setSuperCategory(List.of("Cloths", "Footwear"));

        CategoryDto d2 = new CategoryDto();
        d2.setIdentifier("Root");
        d2.setSuperCategory(List.of());

        List<CategoryDto> mappedDtos = List.of(d1, d2);

        Mockito.when(categoryRepository.findAll()).thenReturn(categories);

        Mockito.when(modelMapper.map(Mockito.eq(categories), Mockito.any(Type.class))).thenReturn(mappedDtos);

        List<CategoryDto> result = categoryService.findAllWithSuperCategoryEmpty();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Electronics", result.get(0).getIdentifier());
    }

}