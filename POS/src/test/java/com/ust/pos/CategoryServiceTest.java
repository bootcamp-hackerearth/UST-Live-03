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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void saveTest() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("Admin");
        Mockito.when(categoryRepository.findByIdentifier("Admin")).thenReturn(null);
        Mockito.when(categoryRepository.save(Mockito.any(Category.class))).thenReturn(new Category());
        CategoryDto response = categoryService.save(dto);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());

    }

    @Test
    void saveTestFailure() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("Admin");
        Mockito.when(categoryRepository.findByIdentifier("Admin"))
                .thenReturn(new Category());
        CategoryDto response = categoryService.save(dto);
        Assertions.assertFalse(response.isSuccess());

    }

    @Test
    void findByIdentifierTest() {

        Category category = new Category();
        category.setIdentifier("Admin");
        Mockito.when(categoryRepository.findByIdentifier("Admin"))
                .thenReturn(category);
        CategoryDto response = categoryService.findByIdentifier("Admin");
        Assertions.assertEquals("Admin", response.getIdentifier());

    }

    @Test
    void updateTest() {
        Category category = new Category();
        category.setIdentifier("Admin");
        Mockito.when(categoryRepository.findByIdentifier("Admin")).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("Admin");
        CategoryDto response = categoryService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {

        Mockito.when(categoryRepository.findByIdentifier("Admin")).thenReturn(null);
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("Admin");
        CategoryDto response = categoryService.update(dto);
        Assertions.assertFalse(response.isSuccess());

    }

    @Test
    void deleteTest() {

        Mockito.doNothing().when(categoryRepository).deleteByIdentifier("Admin");
        boolean response = categoryService.delete("Admin");
        Assertions.assertTrue(response);

    }

    @Test
    void findAllTest() {

        Category category = new Category();
        category.setIdentifier("Admin");
        List<Category> categories = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categories, PageRequest.of(0, 2), categories.size());
        Pageable pageable = PageRequest.of(0, 50);
        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        List<CategoryDto> response = categoryService.findAll(pageable);
        Assertions.assertEquals(1, response.size());

    }

    @Test
    void findByStatusTest() {

        Category category = new Category();
        category.setIdentifier("Admin");
        Mockito.when(categoryRepository.findByStatusIsTrue()).thenReturn(List.of(category));
        List<CategoryDto> response = categoryService.findIfTrue();
        Assertions.assertEquals(1, response.size());

    }

    @Test
    void toggleTestActive() {

        Category category = new Category();
        category.setStatus(false);
        Mockito.when(categoryRepository.findByIdentifier("Admin")).thenReturn(category);
        CategoryDto response = categoryService.toggleStatus("Admin");
        Assertions.assertTrue(response.isStatus());

    }

    @Test
    void toggleTestInactive() {

        Category category = new Category();
        category.setStatus(true);
        Mockito.when(categoryRepository.findByIdentifier("Admin")).thenReturn(category);
        CategoryDto response = categoryService.toggleStatus("Admin");
        Assertions.assertFalse(response.isStatus());

    }

    @Test
    void findBySuperCategoryNotNullTest() {

        Category category = new Category();
        category.setIdentifier("Admin");
        Mockito.when(categoryRepository.findByStatusTrueAndSuperCategoryIsNot(""))
                .thenReturn(List.of(category));
        List<CategoryDto> response = categoryService.findBySuperCategoryNotNull();
        Assertions.assertEquals(1, response.size());

    }

    @Test
    void findAllActiveTest() {

        Category valid = new Category();
        valid.setIdentifier("Valid");
        valid.setSuperCategory("Parent");
        valid.setStatus(true);
        Category nullSuper = new Category();
        nullSuper.setSuperCategory(null);
        nullSuper.setStatus(true);
        Category emptySuper = new Category();
        emptySuper.setSuperCategory(" ");
        emptySuper.setStatus(true);
        Category inactive = new Category();
        inactive.setSuperCategory("Parent");
        inactive.setStatus(false);
        Mockito.when(categoryRepository.findAll()).thenReturn(List.of(valid, nullSuper, emptySuper, inactive));
        List<CategoryDto> response = categoryService.findAllActive();
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Valid", response.get(0).getIdentifier());

    }

    @Test
    void findAllActiveEmptyTest() {

        Mockito.when(categoryRepository.findAll()).thenReturn(List.of());
        List<CategoryDto> response = categoryService.findAllActive();
        Assertions.assertTrue(response.isEmpty());

    }
}