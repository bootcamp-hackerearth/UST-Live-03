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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTestSuccess() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("Admin");

        Category category = new Category();

        when(categoryRepository.findByIdentifier("Admin")).thenReturn(null);
        when(modelMapper.map(dto, Category.class)).thenReturn(category);

        CategoryDto response = categoryService.save(dto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        verify(categoryRepository).save(category);
    }

    @Test
    void saveTestFailure() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("Admin");

        Category category = new Category();
        category.setIdentifier("Admin");

        when(categoryRepository.findByIdentifier("Admin")).thenReturn(category);

        CategoryDto response = categoryService.save(dto);

        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateTestSuccess() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("Admin");

        Category category = new Category();

        when(categoryRepository.findByIdentifier("Admin")).thenReturn(category);

        CategoryDto response = categoryService.update(dto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        verify(modelMapper).map(dto, category);
        verify(categoryRepository).save(category);
    }

    @Test
    void updateTestFailure() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("Admin");

        when(categoryRepository.findByIdentifier("Admin")).thenReturn(null);

        CategoryDto response = categoryService.update(dto);

        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        categoryService.delete("Admin");
        verify(categoryRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findByIdentifierSuccessTest() {
        Category category = new Category();
        category.setIdentifier("Admin");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("Admin");

        when(categoryRepository.findByIdentifier("Admin")).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(dto);

        CategoryDto result = categoryService.findByIdentifier("Admin");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Admin", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(categoryRepository.findByIdentifier("Admin")).thenReturn(null);

        CategoryDto result = categoryService.findByIdentifier("Admin");

        Assertions.assertNull(result);
    }

    @Test
    void findAllTest() {
        Pageable pageable = mock(Pageable.class);
        Page<Category> page = mock(Page.class);

        List<Category> categories = List.of(new Category(), new Category());
        List<CategoryDto> dtoList = List.of(new CategoryDto(), new CategoryDto());

        when(categoryRepository.findAll(pageable)).thenReturn(page);
        when(page.getContent()).thenReturn(categories);
        when(modelMapper.map(eq(categories), any(Type.class))).thenReturn(dtoList);

        List<CategoryDto> result = categoryService.findAll(pageable);

        Assertions.assertEquals(2, result.size());

        verify(categoryRepository).findAll(pageable);
        verify(page).getContent();
        verify(modelMapper).map(eq(categories), any(Type.class));
    }

    @Test
    void findAllCategoriesWithNoSuperTest() {
        Category c1 = new Category();
        c1.setSuperCategory(new ArrayList<>());

        Category c2 = new Category();
        List<String> superList = new ArrayList<>();
        superList.add("parent");
        c2.setSuperCategory(superList);

        List<Category> categories = List.of(c1, c2);
        List<CategoryDto> dtoList = List.of(new CategoryDto());

        when(categoryRepository.findAll()).thenReturn(categories);
        when(modelMapper.map(eq(List.of(c2)), any(Type.class))).thenReturn(dtoList);

        List<CategoryDto> result = categoryService.findAllCategoriesWithNoSuper();

        Assertions.assertEquals(1, result.size());
        verify(categoryRepository).findAll();
    }
}