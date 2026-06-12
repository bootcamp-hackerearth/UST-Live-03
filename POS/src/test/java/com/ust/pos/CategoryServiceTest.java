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

import static org.junit.jupiter.api.Assertions.*;
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
    void saveTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");

        Category category = new Category();
        when(categoryRepository.findByIdentifier("Admin")).thenReturn(null);
        when(modelMapper.map(categoryDto, Category.class)).thenReturn(category);
        CategoryDto response = categoryService.save(categoryDto);

        assertEquals("Admin", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        assertTrue(response.isSuccess());

    }

    @Test
    void saveTestFailure() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");

        Category category = new Category();
        when(categoryRepository.findByIdentifier("Admin")).thenReturn(category);
        CategoryDto response = categoryService.save(categoryDto);

        assertEquals("Admin", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage());
        assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Category category = new Category();
        category.setIdentifier("Admin");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");

        when(categoryRepository.findByIdentifier("Admin")).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        CategoryDto response = categoryService.findByIdentifier("Admin");
        assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");

        Category existingCategory = new Category();
        existingCategory.setIdentifier("Admin");

        when(categoryRepository.findByIdentifier("Admin")).thenReturn(existingCategory);
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);

        CategoryDto response = categoryService.update(categoryDto);
        assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");

        when(categoryRepository.findByIdentifier("Admin")).thenReturn(null);
        CategoryDto response = categoryService.update(categoryDto);
        assertFalse(response.isSuccess());
    }

    @Test
    void testDelete_Success() {
        String identifier = "CAT-001";

        when(categoryRepository.existsBySuperCategory(identifier)).thenReturn(false);

        categoryService.delete(identifier);

        verify(categoryRepository).existsBySuperCategory(identifier);
        verify(categoryRepository).deleteByIdentifier(identifier);
    }
    @Test
    void testDelete_WhenUsedAsSuperCategory_ShouldThrowException() {
        String identifier = "CAT-001";
        when(categoryRepository.existsBySuperCategory(identifier)).thenReturn(true);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoryService.delete(identifier)
        );
        assertEquals(
                "Cannot delete category. It is used as a super category.",
                exception.getMessage()
        );
        verify(categoryRepository).existsBySuperCategory(identifier);
        verify(categoryRepository, never()).deleteByIdentifier(identifier);
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 5);
        Category category = new Category();
        category.setIdentifier("C1");
        List<Category> categoryList = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categoryList);

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("C1");
        List<CategoryDto> categoryDtoList = List.of(categoryDto);

        when(modelMapper.map(Mockito.eq(categoryPage.getContent()), Mockito.any(Type.class))).thenReturn(categoryDtoList);

        WsDto<CategoryDto> result = categoryService.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals("C1", result.getDtoList().get(0).getIdentifier());
        verify(categoryRepository).findAll(pageable);
    }

    @Test
    void toggleStatusTest_TrueToFalse() {
        Category category = new Category();
        category.setIdentifier("M1");
        category.setStatus(true);

        when(categoryRepository.findByIdentifier("M1")).thenReturn(category);

        categoryService.toggleStatus("M1");
        assertFalse(category.isStatus());
        verify(categoryRepository).save(category);
    }

    @Test
    void toggleStatusTest_FalseToTrue() {
        Category category = new Category();
        category.setIdentifier("M1");
        category.setStatus(false);

        when(categoryRepository.findByIdentifier("M1")).thenReturn(category);

        categoryService.toggleStatus("M1");
        assertTrue(category.isStatus());
        verify(categoryRepository).save(category);
    }

    @Test
    void findActiveCategoryTest() {
        Category model = new Category();
        model.setIdentifier("M1");
        model.setStatus(true);
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("M1");

        List<Category> categoryList = List.of(model);
        List<CategoryDto> dtoList = List.of(dto);

        when(categoryRepository.findByStatusTrue()).thenReturn(categoryList);
        when(modelMapper.map(Mockito.eq(categoryList), Mockito.any(java.lang.reflect.Type.class))).thenReturn(dtoList);

        List<CategoryDto> response = categoryService.findActiveCategories();
        assertEquals(1, response.size());
        assertEquals("M1", response.get(0).getIdentifier());
        verify(categoryRepository).findByStatusTrue();
    }

    @Test
    void findChildCategoriesTest() {
        Category category = new Category();
        category.setIdentifier("Electronics");
        category.setSuperCategory("Parent");
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("Electronics");

        List<Category> categoryList = List.of(category);
        List<CategoryDto> dtoList = List.of(dto);

        when(categoryRepository.findBySuperCategoryIsNot("")).thenReturn(categoryList);
        when(modelMapper.map(Mockito.eq(categoryList), Mockito.any(java.lang.reflect.Type.class))).thenReturn(dtoList);

        List<CategoryDto> response = categoryService.findChildCategories();
        assertEquals(1, response.size());
        assertEquals("Electronics", response.get(0).getIdentifier());

        verify(categoryRepository).findBySuperCategoryIsNot("");
    }

}