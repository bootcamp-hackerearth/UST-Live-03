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
        categoryDto.setIdentifier("CAT1");

        Category category = new Category();

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);
        Mockito.when(modelMapper.map(categoryDto, Category.class)).thenReturn(category);

        CategoryDto response = categoryService.save(categoryDto);

        Assertions.assertEquals("CAT1", response.getIdentifier());
        verify(categoryRepository).save(category);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT1");

        Category existingCategory = new Category();
        existingCategory.setDeleted(false);

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(existingCategory);

        CategoryDto response = categoryService.save(categoryDto);

        Assertions.assertEquals("CAT1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Category with identifier - CAT1 already exists", response.getMessage());
        Mockito.verify(categoryRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveFailureAlreadyDeletedTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT1");

        Category existingCategory = new Category();
        existingCategory.setDeleted(true);

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(existingCategory);

        CategoryDto response = categoryService.save(categoryDto);

        Assertions.assertEquals("CAT1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Category with identifier - CAT1 was deleted , Please Contact the Administrator to add.", response.getMessage());
        Mockito.verify(categoryRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateSuccessTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT1");

        Category existingCategory = new Category();
        existingCategory.setIdentifier("CAT1");

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(existingCategory);

        CategoryDto response = categoryService.update(categoryDto);

        Assertions.assertEquals("CAT1", response.getIdentifier());
        verify(modelMapper).map(categoryDto, existingCategory);
        verify(categoryRepository).save(existingCategory);
    }

    @Test
    void updateFailureTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT1");

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);

        CategoryDto response = categoryService.update(categoryDto);

        Assertions.assertEquals("CAT1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Category with identifier - CAT1 not found", response.getMessage());
        Mockito.verify(categoryRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteSuccessTest() {
        Category category = new Category();
        category.setIdentifier("CAT1");

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);

        categoryService.delete("CAT1");

        verify(categoryRepository).findByIdentifier("CAT1");
    }

    @Test
    void findAllSuccessTest() {
        Category c1 = new Category();
        c1.setIdentifier("CAT1");
        List<Category> categoryList = List.of(c1);

        CategoryDto d1 = new CategoryDto();
        d1.setIdentifier("CAT1");
        List<CategoryDto> categoryDtos = List.of(d1);

        Page<Category> page = new PageImpl<>(categoryList, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(categoryRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(categoryList), Mockito.any(Type.class))).thenReturn(categoryDtos);

        WsDto<CategoryDto> result = categoryService.findAll(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(0, result.getPage());
        Assertions.assertEquals(10, result.getSizePerPage());
    }

    @Test
    void findByIdentifierSuccessTest() {
        Category category = new Category();
        category.setIdentifier("CAT1");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT1");

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        CategoryDto response = categoryService.findByIdentifier("CAT1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("CAT1", response.getIdentifier());
    }

    @Test
    void findAllWithSuperCategoryEmptyFilterTest() {
        Category c1 = new Category();
        List<Category> repositoryList = List.of(c1);

        CategoryDto emptySuper = new CategoryDto();
        emptySuper.setIdentifier("CAT_EMP");
        emptySuper.setSuperCategory(List.of());

        CategoryDto nonEmptySuper = new CategoryDto();
        nonEmptySuper.setIdentifier("CAT_OK");
        nonEmptySuper.setSuperCategory(List.of("SUPER1"));

        List<CategoryDto> mappedList = List.of(emptySuper, nonEmptySuper);

        Mockito.when(categoryRepository.findByIsDeletedFalse()).thenReturn(repositoryList);
        Mockito.when(modelMapper.map(Mockito.eq(repositoryList), Mockito.any(Type.class))).thenReturn(mappedList);

        List<CategoryDto> result = categoryService.findAllWithSuperCategoryEmpty();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("CAT_OK", result.get(0).getIdentifier());
    }
}