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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

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
    void findAllWithKeywordTest() {
        Category category = new Category();
        category.setIdentifier("Admin");
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");
        List<Category> categories = List.of(category);
        List<CategoryDto> categoryDtos = List.of(categoryDto);
        Page<Category> categoryPage = new PageImpl<>(categories, PageRequest.of(0, 2), categories.size());
        Pageable pageable = PageRequest.of(0, 50);
        Specification<Category> spec = Mockito.mock(Specification.class);
        Mockito.when(categoryRepository.findAll(spec, pageable)).thenReturn(categoryPage);
        Mockito.doReturn(categoryDtos).when(modelMapper).map(
                Mockito.eq(categories),
                Mockito.any(java.lang.reflect.Type.class)
        );
        WsDto<CategoryDto> response = categoryService.findAll(spec, pageable, "Admin");
        Assertions.assertEquals(categoryDtos, response.getDtoList());
        Assertions.assertEquals(1L, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
        Assertions.assertEquals("Admin", response.getKeyword());
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
        Category category = new Category();
        category.setIdentifier("Admin");
        Mockito.when(categoryRepository.findByIdentifier("Admin")).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        boolean response = categoryService.delete("Admin");
        Assertions.assertTrue(response);
    }

    @Test
    void findAllTest() {
        Category category = new Category();
        category.setIdentifier("Admin");
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");
        List<Category> categories = List.of(category);
        List<CategoryDto> categoryDtos = List.of(categoryDto);
        Page<Category> categoryPage = new PageImpl<>(categories, PageRequest.of(0, 2), categories.size());
        Pageable pageable = PageRequest.of(0, 50);
        Mockito.when(categoryRepository.findByDeletedFalse(pageable)).thenReturn(categoryPage);
        Mockito.doReturn(categoryDtos).when(modelMapper).map(
                Mockito.eq(categories),
                Mockito.any(java.lang.reflect.Type.class)
        );
        WsDto<CategoryDto> response = categoryService.findAll(pageable);
        Assertions.assertEquals(categoryDtos, response.getDtoList());
        Assertions.assertEquals(1L, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
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
        Mockito.when(categoryRepository.findByStatusTrueAndDeletedFalseAndSuperCategoryIsNot(""))
                .thenReturn(List.of(category));
        List<CategoryDto> response = categoryService.findBySuperCategoryNotNull();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllActiveCategoriesTest() {
        Category category1 = new Category();
        category1.setIdentifier("Admin");
        Category category2 = new Category();
        category2.setIdentifier("User");
        Mockito.when(categoryRepository.findByStatusTrueAndDeletedFalse())
                .thenReturn(List.of(category1, category2));
        List<CategoryDto> response = categoryService.findAllActiveCategories();
        Assertions.assertEquals(2, response.size());
    }
}