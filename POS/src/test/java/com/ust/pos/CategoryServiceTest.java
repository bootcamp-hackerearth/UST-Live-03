package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void findByIdentifierTest() {
        Category category = new Category();
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT01");

        Mockito.when(categoryRepository.findByIdentifier("CAT01")).thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        CategoryDto response = categoryService.findByIdentifier("CAT01");

        Assertions.assertEquals("CAT01", response.getIdentifier());
    }

    @Test
    void findByIdentifierTestFailure() {
        Mockito.when(categoryRepository.findByIdentifier("CAT01")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.findByIdentifier("CAT01");
        });
    }

    @Test
    void saveTestSuccess() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT01");

        Mockito.when(categoryRepository.findByIdentifier("CAT01")).thenReturn(null);
        Category category = new Category();
        Mockito.when(modelMapper.map(categoryDto, Category.class)).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);

        CategoryDto response = categoryService.save(categoryDto);

        Assertions.assertEquals("CAT01", response.getIdentifier());
    }

    @Test
    void saveTestFailureAlreadyExists() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT01");

        Category existingCategory = new Category();
        existingCategory.setIdentifier("CAT01");
        existingCategory.setDeleted(false);

        Mockito.when(categoryRepository.findByIdentifier("CAT01")).thenReturn(existingCategory);

        CategoryDto response = categoryService.save(categoryDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Category with identifier - CAT01 already exists", response.getMessage());
    }

    @Test
    void saveTestFailurePreviouslyDeleted() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT01");

        Category existingCategory = new Category();
        existingCategory.setIdentifier("CAT01");
        existingCategory.setDeleted(true);

        Mockito.when(categoryRepository.findByIdentifier("CAT01")).thenReturn(existingCategory);

        CategoryDto response = categoryService.save(categoryDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Category with identifier CAT01 was previously deleted. Please contact backend team to restore.", response.getMessage());
    }

    @Test
    void updateTestSuccess() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT01");

        Category existingCategory = new Category();
        existingCategory.setIdentifier("CAT01");

        Mockito.when(categoryRepository.findByIdentifier("CAT01")).thenReturn(existingCategory);
        Mockito.when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);

        CategoryDto response = categoryService.update(categoryDto);

        Assertions.assertEquals("CAT01", response.getIdentifier());
    }

    @Test
    void updateTestFailure() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT01");

        Mockito.when(categoryRepository.findByIdentifier("CAT01")).thenReturn(null);

        CategoryDto response = categoryService.update(categoryDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Category with identifier - CAT01 not found", response.getMessage());
    }

    @Test
    void deleteTestSuccess() {
        Category category = new Category();

        Mockito.when(categoryRepository.findByIdentifier("CAT01")).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);

        boolean response = categoryService.delete("CAT01");

        Assertions.assertTrue(response);
    }

    @Test
    void deleteTestFailure() {
        Mockito.when(categoryRepository.findByIdentifier("CAT01")).thenReturn(null);

        boolean response = categoryService.delete("CAT01");

        Assertions.assertFalse(response);
    }

    @Test
    void findAllPageableTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Category category = new Category();
        List<Category> categories = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        CategoryDto categoryDto = new CategoryDto();
        List<CategoryDto> categoryDtos = List.of(categoryDto);

        Mockito.when(categoryRepository.findByDeletedFalse(pageable)).thenReturn(categoryPage);
        Mockito.when(modelMapper.map(Mockito.eq(categories), Mockito.any(java.lang.reflect.Type.class))).thenReturn(categoryDtos);

        WsDto<CategoryDto> response = categoryService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findBySuperCategoryNotNullTest() {
        Category category = new Category();
        List<Category> categories = List.of(category);
        CategoryDto categoryDto = new CategoryDto();
        List<CategoryDto> categoryDtos = List.of(categoryDto);

        Mockito.when(categoryRepository.findByStatusTrueAndDeletedFalseAndSuperCategoryIsNot("")).thenReturn(categories);
        Mockito.when(modelMapper.map(Mockito.eq(categories), Mockito.any(java.lang.reflect.Type.class))).thenReturn(categoryDtos);

        List<CategoryDto> response = categoryService.findBySuperCategoryNotNull();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleStatusTest() {
        Category category = new Category();
        category.setStatus(false);
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setStatus(true);

        Mockito.when(categoryRepository.findByIdentifier("CAT01")).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        CategoryDto response = categoryService.toggleStatus("CAT01");

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void findIfTrueTest() {
        Category category = new Category();
        List<Category> categories = List.of(category);
        CategoryDto categoryDto = new CategoryDto();
        List<CategoryDto> categoryDtos = List.of(categoryDto);

        Mockito.when(categoryRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(categories);
        Mockito.when(modelMapper.map(Mockito.eq(categories), Mockito.any(java.lang.reflect.Type.class))).thenReturn(categoryDtos);

        List<CategoryDto> response = categoryService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Specification<Category> specification = Mockito.mock(Specification.class);
        Category category = new Category();
        List<Category> categories = List.of(category);
        Page<Category> page = new PageImpl<>(categories, pageable, categories.size());

        CategoryDto categoryDto = new CategoryDto();
        List<CategoryDto> categoryDtos = List.of(categoryDto);

        Mockito.when(categoryRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(categories), Mockito.any(java.lang.reflect.Type.class))).thenReturn(categoryDtos);

        WsDto<CategoryDto> response = categoryService.findAll(specification, pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }
}