package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        category.setIdentifier("FOOD");
        category.setSupercategory("");

        categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setIdentifier("FOOD");
        categoryDto.setSupercategory("");
    }

    @Test
    void save_shouldSaveCategory() {
        when(categoryRepository.findByIdentifier("FOOD")).thenReturn(null);

        when(modelMapper.map(categoryDto, Category.class)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);

        CategoryDto result = categoryService.save(categoryDto);

        assertNotNull(result);
        assertEquals("FOOD", result.getIdentifier());

        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void findById_shouldReturnCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        CategoryDto result = categoryService.findById(1L);

        assertEquals(1L, result.getId());
        assertEquals("FOOD", result.getIdentifier());
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> categoryService.findById(1L)
        );

        assertTrue(exception.getMessage().contains("Category not found"));
    }

    @Test
    void update_shouldUpdateCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        CategoryDto result = categoryService.update(categoryDto);

        assertEquals("FOOD", result.getIdentifier());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void update_shouldThrowException_whenCategoryNotFound() {
        CategoryDto dto = new CategoryDto();
        dto.setId(99L);
        dto.setIdentifier("FOOD");

        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> categoryService.update(dto)
        );

        assertEquals("Category not found", exception.getMessage());
    }

    @Test
    void findAll_shouldReturnList() {
        Page<Category> page = new PageImpl<>(List.of(category));

        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(page);

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        when(modelMapper.map(page.getContent(), listType))
                .thenReturn(List.of(categoryDto));

        List<CategoryDto> result =
                categoryService.findAll(PageRequest.of(0, 50, Sort.unsorted()));

        assertEquals(1, result.size());
        assertEquals("FOOD", result.get(0).getIdentifier());
    }

    @Test
    void deleteById_shouldDeleteCategory() {
        doNothing().when(categoryRepository).deleteById(1L);

        categoryService.deleteById(1L);

        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void findSubCategories_shouldReturnSubCategories() {
        category.setSupercategory("MAIN");
        categoryDto.setSupercategory("MAIN");

        when(categoryRepository.findBySupercategoryIsNot(""))
                .thenReturn(List.of(category));
        when(modelMapper.map(category, CategoryDto.class))
                .thenReturn(categoryDto);

        List<CategoryDto> result = categoryService.findSubCategories();

        assertFalse(result.isEmpty());
        assertEquals("MAIN", result.get(0).getSupercategory());
    }
}