package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {

        category = new Category();
        category.setId(1L);
        category.setIdentifier("CAT001");
        category.setDescription("Test Category");

        categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT001");
        categoryDto.setDescription("Test Category");
        categoryDto.setSuperCategory("SUPER001");
    }

    @Test
    void save_ShouldReturnFailure_WhenCategoryAlreadyExists() {

        when(categoryRepository.findByIdentifierAndIsDeleteFalse("CAT001"))
                .thenReturn(category);

        CategoryDto result = categoryService.save(categoryDto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Category with identifier - CAT001 already exists",
                result.getMessage());

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void save_ShouldSaveCategory_WhenCategoryDoesNotExist() {

        when(categoryRepository.findByIdentifierAndIsDeleteFalse("CAT001"))
                .thenReturn(null);

        when(modelMapper.map(categoryDto, Category.class))
                .thenReturn(category);

        CategoryDto result = categoryService.save(categoryDto);

        assertNotNull(result);

        verify(modelMapper).map(categoryDto, Category.class);
        verify(categoryRepository).save(category);
    }

    @Test
    void save_ShouldSetSuperCategoryNull_WhenEmpty() {

        categoryDto.setSuperCategory("");

        when(categoryRepository.findByIdentifierAndIsDeleteFalse("CAT001"))
                .thenReturn(null);

        when(modelMapper.map(categoryDto, Category.class))
                .thenReturn(category);

        categoryService.save(categoryDto);

        assertNull(categoryDto.getSuperCategory());

        verify(categoryRepository).save(category);
    }

    @Test
    void update_ShouldReturnFailure_WhenCategoryNotFound() {

        when(categoryRepository.findByIdentifierAndIsDeleteFalse("CAT001"))
                .thenReturn(null);

        CategoryDto result = categoryService.update(categoryDto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Category with identifier - CAT001 is not found",
                result.getMessage());

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void update_ShouldUpdateCategory_WhenCategoryExists() {

        when(categoryRepository.findByIdentifierAndIsDeleteFalse("CAT001"))
                .thenReturn(category);

        CategoryDto result = categoryService.update(categoryDto);

        assertNotNull(result);

        verify(modelMapper).map(categoryDto, category);
        verify(categoryRepository).save(category);
    }

    @Test
    void update_ShouldSetSuperCategoryNull_WhenEmpty() {

        categoryDto.setSuperCategory("");

        when(categoryRepository.findByIdentifierAndIsDeleteFalse("CAT001"))
                .thenReturn(category);

        categoryService.update(categoryDto);

        assertNull(categoryDto.getSuperCategory());

        verify(modelMapper).map(categoryDto, category);
        verify(categoryRepository).save(category);
    }

    @Test
    void delete_ShouldSoftDelete_WhenCategoryExists() {

        when(categoryRepository.findByIdentifierAndIsDeleteFalse("CAT001"))
                .thenReturn(category);

        categoryService.delete("CAT001");

        assertTrue(category.isDelete());

        verify(categoryRepository).save(category);
    }

    @Test
    void delete_ShouldDoNothing_WhenCategoryNotFound() {

        when(categoryRepository.findByIdentifierAndIsDeleteFalse("CAT001"))
                .thenReturn(null);

        categoryService.delete("CAT001");

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void findAll_ShouldReturnMappedList() {

        List<Category> categories = List.of(category);
        List<CategoryDto> expected = List.of(categoryDto);

        when(categoryRepository.findByIsDeleteFalse())
                .thenReturn(categories);

        when(modelMapper.map(
                eq(categories),
                any(Type.class)))
                .thenReturn(expected);

        List<CategoryDto> result = categoryService.findAll();

        assertEquals(1, result.size());

        verify(categoryRepository).findByIsDeleteFalse();
    }

    @Test
    void findAllWithoutNull_ShouldFilterNullAndSameIdentifier() {

        CategoryDto dto1 = new CategoryDto();
        dto1.setIdentifier("CAT001");
        dto1.setSuperCategory("SUPER");

        CategoryDto dto2 = new CategoryDto();
        dto2.setIdentifier("CAT002");
        dto2.setSuperCategory("SUPER");

        CategoryDto dto3 = new CategoryDto();
        dto3.setIdentifier("CAT003");
        dto3.setSuperCategory(null);

        List<CategoryDto> mappedList =
                List.of(dto1, dto2, dto3);

        when(categoryRepository.findByIsDeleteFalse())
                .thenReturn(List.of(category));

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(mappedList);

        List<CategoryDto> result =
                categoryService.findAllWithoutNull("CAT001");

        assertEquals(1, result.size());
        assertEquals("CAT002", result.get(0).getIdentifier());
    }

    @Test
    void findAllWithNull_ShouldReturnOnlyNullSuperCategories() {

        CategoryDto dto1 = new CategoryDto();
        dto1.setIdentifier("CAT001");
        dto1.setSuperCategory("SUPER");

        CategoryDto dto2 = new CategoryDto();
        dto2.setIdentifier("CAT002");
        dto2.setSuperCategory(null);

        List<CategoryDto> mappedList =
                List.of(dto1, dto2);

        when(categoryRepository.findByIsDeleteFalse())
                .thenReturn(List.of(category));

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(mappedList);

        List<CategoryDto> result =
                categoryService.findAllWithNull();

        assertEquals(1, result.size());
        assertEquals("CAT002", result.get(0).getIdentifier());
    }

    @Test
    void findByIdentifier_ShouldReturnMappedDto() {

        when(categoryRepository.findByIdentifierAndIsDeleteFalse("CAT001"))
                .thenReturn(category);

        when(modelMapper.map(category, CategoryDto.class))
                .thenReturn(categoryDto);

        CategoryDto result =
                categoryService.findByIdentifier("CAT001");

        assertNotNull(result);
        assertEquals("CAT001", result.getIdentifier());
    }

    @Test
    void findAll_WithSearch_ShouldReturnMappedPage() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Category> page =
                new PageImpl<>(List.of(category));

        when(categoryRepository
                .findByIdentifierContainingIgnoreCaseAndIsDeleteFalse(
                        "CAT",
                        pageable))
                .thenReturn(page);

        when(modelMapper.map(any(Category.class),
                eq(CategoryDto.class)))
                .thenReturn(categoryDto);

        Page<CategoryDto> result =
                categoryService.findAll(pageable, "CAT");

        assertEquals(1, result.getTotalElements());

        verify(categoryRepository)
                .findByIdentifierContainingIgnoreCaseAndIsDeleteFalse(
                        "CAT",
                        pageable);
    }

    @Test
    void findAll_WithoutSearch_ShouldReturnMappedPage() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Category> page =
                new PageImpl<>(List.of(category));

        when(categoryRepository.findByIsDeleteFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(any(Category.class),
                eq(CategoryDto.class)))
                .thenReturn(categoryDto);

        Page<CategoryDto> result =
                categoryService.findAll(pageable, "");

        assertEquals(1, result.getTotalElements());

        verify(categoryRepository)
                .findByIsDeleteFalse(pageable);
    }
}