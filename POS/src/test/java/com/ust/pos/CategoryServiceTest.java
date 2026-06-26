package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    void save_success() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setName("Electronics");

        when(categoryRepository.existsByIdentifier("CAT1")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));
        CategoryDto result = categoryService.save(dto);

        assertTrue(result.isSuccess());
        assertEquals("Category created successfully", result.getMessage());

        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void save_duplicate() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        when(categoryRepository.existsByIdentifier("CAT1")).thenReturn(true);
        CategoryDto result = categoryService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals("Identifier already exists", result.getMessage());

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void find_success() {

        Category category = new Category();
        category.setDeleted(false);

        CategoryDto mapped = new CategoryDto();
        mapped.setIdentifier("CAT1");

        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(Optional.of(category));
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(mapped);

        CategoryDto result = categoryService.findByIdentifier("CAT1");

        assertTrue(result.isSuccess());
        assertEquals("CAT1", result.getIdentifier());
    }

    @Test
    void find_not_found() {

        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(Optional.empty());
        CategoryDto result = categoryService.findByIdentifier("CAT1");

        assertFalse(result.isSuccess());
        assertEquals("Category not found", result.getMessage());
    }

    @Test
    void update_success() {

        Category category = new Category();
        category.setIdentifier("CAT1");
        category.setDeleted(false);

        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setName("Updated");

        CategoryDto result = categoryService.update(dto);

        assertTrue(result.isSuccess());
        assertEquals("Category updated successfully", result.getMessage());

        verify(categoryRepository).save(category);
    }

    @Test
    void update_not_found() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(Optional.empty());
        CategoryDto result = categoryService.update(dto);

        assertFalse(result.isSuccess());
        assertEquals("Category not found", result.getMessage());
    }

    @Test
    void delete_found() {

        Category category = new Category();
        category.setIdentifier("CAT1");

        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

        categoryService.delete("CAT1");

        assertTrue(category.getDeleted());
        verify(categoryRepository).save(category);
    }

    @Test
    void delete_not_found() {

        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(Optional.empty());
        categoryService.delete("CAT1");

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void find_all() {

        Category category = new Category();
        Page<Category> page = new PageImpl<>(List.of(category));
        Pageable pageable = PageRequest.of(0, 5);

        when(categoryRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Type type = new TypeToken<List<CategoryDto>>() {}.getType();
        when(modelMapper.map(anyList(), eq(type))).thenReturn(List.of(new CategoryDto()));
        WsDto<CategoryDto> result =categoryService.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
    }

    @Test
    void super_categories() {

        Category parent = new Category();
        parent.setIdentifier("A");
        parent.setSuperCategoryIdentifier(null);

        Category child = new Category();
        child.setIdentifier("B");
        child.setSuperCategoryIdentifier("A");

        when(categoryRepository.findByDeletedFalse()).thenReturn(List.of(parent, child));
        when(modelMapper.map(parent, CategoryDto.class)).thenReturn(new CategoryDto());
        List<CategoryDto> result =categoryService.findSuperCategories();
        assertEquals(1, result.size());
    }

    @Test
    void leaf_categories() {

        Category parent = new Category();
        parent.setIdentifier("A");

        Category child = new Category();
        child.setIdentifier("B");
        child.setSuperCategoryIdentifier("A");

        when(categoryRepository.findByDeletedFalse()).thenReturn(List.of(parent, child));
        when(modelMapper.map(child, CategoryDto.class)).thenReturn(new CategoryDto());
        List<CategoryDto> result =categoryService.findLeafCategories();

        assertEquals(1, result.size());
    }

    @Test
    void child_categories() {

        Category child = new Category();
        child.setIdentifier("B");
        child.setSuperCategoryIdentifier("A");

        when(categoryRepository.findByDeletedFalse()).thenReturn(List.of(child));
        when(modelMapper.map(child, CategoryDto.class)).thenReturn(new CategoryDto());
        List<CategoryDto> result = categoryService.findChildCategories();

        assertEquals(1, result.size());
    }
}