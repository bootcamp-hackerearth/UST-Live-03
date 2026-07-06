package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.PaginationResponseDto;
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
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

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
        categoryDto.setIdentifier("CAT001");
        Category category = new Category();
        Mockito.when(categoryRepository.findByIdentifier("CAT001")).thenReturn(null);
        Mockito.when(modelMapper.map(categoryDto, Category.class)).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        CategoryDto response = categoryService.save(categoryDto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Successfully added the category", response.getMessage());
        Mockito.verify(categoryRepository).save(category);
    }

    @Test
    void saveTestFailure() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT001");
        Category category = new Category();
        Mockito.when(categoryRepository.findByIdentifier("CAT001")).thenReturn(category);
        CategoryDto response = categoryService.save(categoryDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("already exists"));
    }

    @Test
    void saveSoftDeletedCategoryTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT001");
        Category category = new Category();
        category.setDeleted(true);
        Mockito.when(categoryRepository.findByIdentifier("CAT001")).thenReturn(category);
        CategoryDto response = categoryService.save(categoryDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void saveWithEmptySuperCategoryTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT001");
        categoryDto.setSuperCategory("");
        Category category = new Category();
        Mockito.when(categoryRepository.findByIdentifier("CAT001")).thenReturn(null);
        Mockito.when(modelMapper.map(categoryDto, Category.class)).thenReturn(category);
        categoryService.save(categoryDto);
        Assertions.assertNull(categoryDto.getSuperCategory());
        Mockito.verify(categoryRepository).save(category);
    }

    @Test
    void findByIdentifierTest() {
        Category category = new Category();
        category.setIdentifier("CAT001");
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");
        Mockito.when(categoryRepository.findByIdentifier("CAT001")).thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(dto);
        CategoryDto response = categoryService.findByIdentifier("CAT001");
        Assertions.assertEquals("CAT001", response.getIdentifier());
    }

    @Test
    void updateTest() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");
        Category category = new Category();
        category.setIdentifier("CAT001");
        Mockito.when(categoryRepository.findByIdentifier("CAT001")).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        CategoryDto response = categoryService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(categoryRepository).save(category);
    }

    @Test
    void updateTestFailure() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");
        Mockito.when(categoryRepository.findByIdentifier("CAT001")).thenReturn(null);
        CategoryDto response = categoryService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("not found"));
    }

    @Test
    void deleteTest() {
        Category category = new Category();
        category.setIdentifier("CAT001");
        Mockito.when(categoryRepository.findByIdentifier("CAT001")).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        categoryService.delete("CAT001");
        Assertions.assertTrue(category.isDeleted());
        Mockito.verify(categoryRepository).findByIdentifier("CAT001");
        Mockito.verify(categoryRepository).save(category);
    }

    @Test
    void deleteNotFoundTest() {
        Mockito.when(categoryRepository.findByIdentifier("CAT001")).thenReturn(null);
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                        () -> categoryService.delete("CAT001"));
        Assertions.assertEquals("Category not found", exception.getMessage());
    }

    @Test
    void findAllWithPageableTest() {
        Category category = new Category();
        category.setIdentifier("CAT001");
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");
        Pageable pageable = PageRequest.of(0, 5);
        Page<Category> page = new PageImpl<>(List.of(category));
        Mockito.when(categoryRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(List.of(category)), Mockito.any(Type.class))).thenReturn(List.of(dto));
        PaginationResponseDto<CategoryDto> response = categoryService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("CAT001", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Category category = new Category();
        category.setIdentifier("CAT001");
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");
        List<Category> categories = List.of(category);
        List<CategoryDto> dtos = List.of(dto);
        Mockito.when(categoryRepository.findAll()).thenReturn(categories);
        Mockito.when(modelMapper.map(Mockito.eq(categories), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<CategoryDto> response = categoryService.findAll(null);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("CAT001", response.getDtoList().get(0).getIdentifier());
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
        Mockito.verify(categoryRepository).save(category);
    }

    @Test
    void toggleStatusFailureTest() {

        Mockito.when(categoryRepository.findByIdentifier("CAT001")).thenReturn(null);
        Mockito.when(modelMapper.map(null, CategoryDto.class)).thenReturn(null);
        CategoryDto response = categoryService.toggleStatus("CAT001", true);
        Assertions.assertNull(response);
        Mockito.verify(categoryRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findSubCategoriesTest() {
        Category category = new Category();
        category.setIdentifier("SUB001");
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("SUB001");
        List<Category> categories = List.of(category);
        List<CategoryDto> dtos = List.of(dto);
        Mockito.when(categoryRepository.findBySuperCategoryIsNotNull()).thenReturn(categories);
        Mockito.when(modelMapper.map(Mockito.eq(categories), Mockito.any(Type.class))).thenReturn(dtos);
        List<CategoryDto> response = categoryService.findSubCategories();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("SUB001", response.get(0).getIdentifier());
        Mockito.verify(categoryRepository).findBySuperCategoryIsNotNull();
    }

    @Test
    void findAllWithSpecificationTest() {
        Category category = new Category();
        category.setIdentifier("CAT001");
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");
        List<Category> categories = List.of(category);
        List<CategoryDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Category> page = new PageImpl<>(categories, pageable, 1);
        Specification<Category> specification = Mockito.mock(Specification.class);
        Mockito.when(categoryRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(categories), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<CategoryDto> response = categoryService.findAll(specification, pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("CAT001", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(5, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
        Mockito.verify(categoryRepository).findAll(specification, pageable);
    }

    @Test
    void findAllWithSpecificationNoDataTest() {
        Pageable pageable = PageRequest.of(0, 5);
        Specification<Category> specification = Mockito.mock(Specification.class);
        Page<Category> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        Mockito.when(categoryRepository.findAll(specification, pageable)).thenReturn(emptyPage);
        Mockito.when(modelMapper.map(Mockito.eq(List.of()), Mockito.any(Type.class))).thenReturn(List.of());
        PaginationResponseDto<CategoryDto> response = categoryService.findAll(specification, pageable);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.getDtoList().isEmpty());
        Assertions.assertEquals(0, response.getTotalRecords());
        Assertions.assertEquals(0, response.getTotalPages());
        Assertions.assertEquals(5, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
        Mockito.verify(categoryRepository).findAll(specification, pageable);
    }
}