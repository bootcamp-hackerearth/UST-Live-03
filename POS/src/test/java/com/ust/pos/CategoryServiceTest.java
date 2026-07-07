package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PageDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
    void saveTestSuccess() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");

        Mockito.when(categoryRepository.findByIdentifier("CAT001")).thenReturn(null);

        Category category = new Category();

        Mockito.when(modelMapper.map(dto, Category.class)).thenReturn(category);

        Mockito.when(categoryRepository.save(category)).thenReturn(category);

        CategoryDto response = categoryService.save(dto);

        Assertions.assertNotNull(response);

        Mockito.verify(categoryRepository).save(category);
    }

    @Test
    void saveTestFailure_CategoryAlreadyExists() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");

        Category existing = new Category();
        existing.setDeleted(false);

        Mockito.when(categoryRepository.findByIdentifier("CAT001")).thenReturn(existing);

        CategoryDto response = categoryService.save(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Category with identifier - CAT001 already exists", response.getMessage());

        Mockito.verify(categoryRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveTestFailure_SoftDeletedCategory() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");

        Category existing = new Category();
        existing.setDeleted(true);

        Mockito.when(categoryRepository.findByIdentifier("CAT001")).thenReturn(existing);

        CategoryDto response = categoryService.save(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Category with identifier - CAT001 has been soft deleted. Restore it by changing status.", response.getMessage());

        Mockito.verify(categoryRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateTestSuccess() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");
        dto.setSuccess(true);

        Category category = new Category();

        Mockito.when(categoryRepository.findByIdentifier("CAT001")).thenReturn(category);

        Mockito.when(categoryRepository.save(category)).thenReturn(category);

        CategoryDto response = categoryService.update(dto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(modelMapper).map(dto, category);

        Mockito.verify(categoryRepository).save(category);
    }

    @Test
    void updateTestFailure() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");

        Mockito.when(categoryRepository.findByIdentifier("CAT001")).thenReturn(null);

        CategoryDto response = categoryService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Category with identifier - CAT001 not found", response.getMessage());

        Mockito.verify(categoryRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteTestSuccess() {

        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setDeleted(false);

        Mockito.when(categoryRepository.findByIdentifier("CAT001")).thenReturn(category);

        Mockito.when(categoryRepository.save(category)).thenReturn(category);

        boolean result = categoryService.delete("CAT001");

        Assertions.assertTrue(result);

        Assertions.assertTrue(category.getDeleted());

        Mockito.verify(categoryRepository).save(category);
    }

    @Test
    void deleteTestFailure() {

        Mockito.when(categoryRepository.findByIdentifier("CAT001")).thenReturn(null);

        boolean result = categoryService.delete("CAT001");

        Assertions.assertFalse(result);

        Mockito.verify(categoryRepository, Mockito.never()).save(Mockito.any());
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

        Assertions.assertNotNull(response);

        Assertions.assertEquals("CAT001", response.getIdentifier());
    }

    @Test
    void findByIdentifierTest_notFound_throwsException() {

        Mockito.when(categoryRepository.findByIdentifier("CAT404")).thenReturn(null);

        Assertions.assertThrows(com.ust.pos.exception.ResourceNotFoundException.class, () -> categoryService.findByIdentifier("CAT404"));

        Mockito.verify(modelMapper, Mockito.never()).map(Mockito.any(), Mockito.eq(CategoryDto.class));
    }

    @Test
    void findAllPaginationTest() {

        Category category = new Category();
        category.setIdentifier("CAT001");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Category> page = new PageImpl<>(List.of(category), pageable, 1);

        Mockito.when(categoryRepository.findByDeletedFalse(pageable)).thenReturn(page);

        Type listType = new TypeToken<List<CategoryDto>>() {
                }.getType();

        Mockito.when(modelMapper.map(page.getContent(), listType)).thenReturn(List.of(dto));

        PageDto<CategoryDto> response = categoryService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());

        Assertions.assertEquals(1, response.getTotalRecords());

        Assertions.assertEquals(1, response.getTotalPages());

        Assertions.assertEquals(10, response.getSizePerPage());

        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findAll_withSpecificationAndKeyword() {

        Category category = new Category();
        category.setIdentifier("CAT001");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Category> spec = Mockito.mock(Specification.class);

        Page<Category> page = new PageImpl<>(List.of(category), pageable, 1);

        Mockito.when(categoryRepository.findAll(spec, pageable)).thenReturn(page);

        Type listType = new TypeToken<List<ModelDto>>() {
                }.getType();

        Mockito.when(modelMapper.map(page.getContent(), listType)).thenReturn(List.of(dto));

        PageDto<CategoryDto> response = categoryService.findAll(spec, pageable, "shoes");

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
        Assertions.assertEquals("shoes", response.getKeyword());

        Mockito.verify(categoryRepository).findAll(spec, pageable);
    }

    @Test
    void findAll_withSpecificationAndKeyword_noResults() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Category> spec = Mockito.mock(Specification.class);

        Page<Category> page = new PageImpl<>(List.of(), pageable, 0);

        Mockito.when(categoryRepository.findAll(spec, pageable)).thenReturn(page);

        Type listType = new TypeToken<List<ModelDto>>() {
                }.getType();

        Mockito.when(modelMapper.map(page.getContent(), listType)).thenReturn(List.of());

        PageDto<CategoryDto> response = categoryService.findAll(spec, pageable, "nomatch");

        Assertions.assertTrue(response.getDtoList().isEmpty());
        Assertions.assertEquals(0, response.getTotalRecords());
        Assertions.assertEquals("nomatch", response.getKeyword());
    }

    @Test
    void findBySubCategoryTest() {

        Category category1 = new Category();
        Category category2 = new Category();

        Mockito.when(categoryRepository.findBySupercategoryIsNot("")).thenReturn(List.of(category1, category2));

        Mockito.when(modelMapper.map(Mockito.any(Category.class), Mockito.eq(CategoryDto.class))).thenReturn(new CategoryDto());

        List<CategoryDto> response = categoryService.findBySubCategory();

        Assertions.assertEquals(2, response.size());
    }

    @Test
    void findBySubCategoryTest_empty() {

        Mockito.when(categoryRepository.findBySupercategoryIsNot("")).thenReturn(List.of());

        List<CategoryDto> response = categoryService.findBySubCategory();

        Assertions.assertTrue(response.isEmpty());

        Mockito.verify(modelMapper, Mockito.never()).map(Mockito.any(Category.class), Mockito.eq(CategoryDto.class));
    }

    @Test
    void toggleStatusTest() {

        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setStatus(true);

        Mockito.when(categoryRepository.findByIdentifier("CAT001")).thenReturn(category);

        Mockito.when(categoryRepository.save(category)).thenReturn(category);

        categoryService.toggleStatus("CAT001");

        Assertions.assertFalse(category.getStatus());

        Mockito.verify(categoryRepository).save(category);
    }

    @Test
    void toggleStatusTestFailure() {

        Mockito.when(categoryRepository.findByIdentifier("CAT001")).thenReturn(null);

        categoryService.toggleStatus("CAT001");

        Mockito.verify(categoryRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findActiveCategoriesTest() {

        Category category = new Category();
        category.setIdentifier("CAT001");
        category.setStatus(true);

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT001");

        List<Category> categoryList = List.of(category);

        Type listType = new TypeToken<List<CategoryDto>>() {
                }.getType();

        Mockito.when(categoryRepository.findByStatusTrue()).thenReturn(categoryList);

        Mockito.when(modelMapper.map(categoryList, listType)).thenReturn(List.of(dto));

        List<CategoryDto> response = categoryService.findActiveCategories();

        Assertions.assertNotNull(response);

        Assertions.assertEquals(1, response.size());

        Assertions.assertEquals("CAT001", response.get(0).getIdentifier());
    }

    @Test
    void findActiveCategoriesTest_empty() {

        Type listType = new TypeToken<List<CategoryDto>>() {
                }.getType();

        Mockito.when(categoryRepository.findByStatusTrue()).thenReturn(List.of());

        Mockito.when(modelMapper.map(List.of(), listType)).thenReturn(List.of());

        List<CategoryDto> response = categoryService.findActiveCategories();

        Assertions.assertTrue(response.isEmpty());
    }
}