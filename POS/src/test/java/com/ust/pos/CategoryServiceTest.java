package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        category.setStatus(true);

        categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setIdentifier("CAT001");
        categoryDto.setStatus(true);
    }


    @Test
    void save_shouldFail_whenCategoryAlreadyExists() {
        when(categoryRepository.findByIdentifier("CAT001")).thenReturn(category);

        CategoryDto result = categoryService.save(categoryDto);

        assertFalse(result.isSuccess());
        assertEquals("Category already exists", result.getMessage());
        verify(categoryRepository, never()).save(any());
    }


    @Test
    void save_shouldSave_whenSuperCategoryIsNull() {
        categoryDto.setSuperCategory(null);
        when(categoryRepository.findByIdentifier("CAT001")).thenReturn(null);
        CategoryDto result = categoryService.save(categoryDto);
        org.mockito.ArgumentCaptor<Category> captor =
                org.mockito.ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        assertNull(captor.getValue().getSuperCategory());
        assertNotNull(result);
    }

    @Test
    void save_shouldSave_whenSuperCategoryIsBlank() {
        categoryDto.setSuperCategory("   ");
        when(categoryRepository.findByIdentifier("CAT001")).thenReturn(null);

        CategoryDto result = categoryService.save(categoryDto);
        org.mockito.ArgumentCaptor<Category> captor =
                org.mockito.ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        assertNull(captor.getValue().getSuperCategory());
        assertNotNull(result);
    }

    @Test
    void save_shouldSave_whenSuperCategoryIsPresent() {
        categoryDto.setSuperCategory("PARENT001");
        when(categoryRepository.findByIdentifier("CAT001")).thenReturn(null);

        CategoryDto result = categoryService.save(categoryDto);
        org.mockito.ArgumentCaptor<Category> captor =
                org.mockito.ArgumentCaptor.forClass(Category.class);

        verify(categoryRepository).save(captor.capture());
        assertEquals("PARENT001", captor.getValue().getSuperCategory());
        assertNotNull(result);
    }

    @Test
    void update_shouldFail_whenCategoryNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        CategoryDto result = categoryService.update(categoryDto);

        assertFalse(result.isSuccess());

        assertEquals("Category not found", result.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void update_shouldSucceed_whenIdentifierUnchanged() {
        category.setIdentifier("CAT001");
        categoryDto.setSuperCategory("PARENT001");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryDto result = categoryService.update(categoryDto);

        verify(categoryRepository).save(category);
        assertNotNull(result);
    }

    @Test
    void update_shouldFail_whenNewIdentifierAlreadyExists() {
        category.setIdentifier("CAT001");
        categoryDto.setIdentifier("CAT_NEW");
        Category conflict = new Category();
        conflict.setIdentifier("CAT_NEW");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.findByIdentifier("CAT_NEW")).thenReturn(conflict);
        CategoryDto result = categoryService.update(categoryDto);
        assertFalse(result.isSuccess());
        assertEquals("Category already exists", result.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void update_shouldSucceed_whenIdentifierChangedAndNoDuplicate() {
        category.setIdentifier("CAT001");
        categoryDto.setIdentifier("CAT_NEW");
        categoryDto.setSuperCategory(null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.findByIdentifier("CAT_NEW")).thenReturn(null);
        CategoryDto result = categoryService.update(categoryDto);
        verify(categoryRepository).save(category);
        assertNull(category.getSuperCategory());
        assertNotNull(result);
    }


    @Test
    void update_shouldSetSuperCategoryNull_whenBlank() {
        category.setIdentifier("CAT001");
        categoryDto.setSuperCategory("   ");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.update(categoryDto);

        assertNull(category.getSuperCategory());
        verify(categoryRepository).save(category);
    }

    @Test
    void update_shouldSetSuperCategory_whenPresent() {
        category.setIdentifier("CAT001");
        categoryDto.setSuperCategory("PARENT001");  // non-blank

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.update(categoryDto);

        assertEquals("PARENT001", category.getSuperCategory());
        verify(categoryRepository).save(category);
    }

    /* ===================== DELETE ===================== */

    // Branch: existsBySuperCategory false → delete proceeds
    @Test
    void deleteByIdentifier_shouldDelete_whenNotUsedAsSuperCategory() {
        when(categoryRepository.existsBySuperCategory("CAT001")).thenReturn(false);
        doNothing().when(categoryRepository).deleteByIdentifier("CAT001");

        categoryService.deleteByIdentifier("CAT001");

        verify(categoryRepository).deleteByIdentifier("CAT001");
    }

    @Test
    void deleteByIdentifier_shouldThrow_whenUsedAsSuperCategory() {
        when(categoryRepository.existsBySuperCategory("CAT001")).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> categoryService.deleteByIdentifier("CAT001"));

        verify(categoryRepository, never()).deleteByIdentifier(any());
    }

    @Test
    void findByIdentifier_shouldReturnCategoryDto() {
        when(categoryRepository.findByIdentifier("CAT001")).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        CategoryDto result = categoryService.findByIdentifier("CAT001");

        assertNotNull(result);
        assertEquals("CAT001", result.getIdentifier());
    }

    @Test
    void findAllTest() {
        List<Category> categories = List.of(category);
        List<CategoryDto> categoryDtos = List.of(categoryDto);

        Page<Category> categoryPage =
                new PageImpl<>(categories, PageRequest.of(0, 2), categories.size());
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));

        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(categories),
                Mockito.any(java.lang.reflect.Type.class))
        ).thenReturn(categoryDtos);

        List<CategoryDto> response = categoryService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findChildCategories_shouldReturnChildCategoryDtos() {
        Category child = new Category();
        child.setIdentifier("CHILD001");
        child.setSuperCategory("CAT001");

        CategoryDto childDto = new CategoryDto();
        childDto.setIdentifier("CHILD001");

        List<Category> children = List.of(child);
        List<CategoryDto> childDtos = List.of(childDto);

        when(categoryRepository.findBySuperCategoryIsNotNull()).thenReturn(children);
        when(modelMapper.map(Mockito.eq(children), Mockito.any(Type.class)))
                .thenReturn(childDtos);

        List<CategoryDto> result = categoryService.findChildCategories();

        assertEquals(1, result.size());
        assertEquals("CHILD001", result.get(0).getIdentifier());
    }

    @Test
    void toggleStatus_shouldFlipTrueToFalse() {
        category.setStatus(true);
        CategoryDto toggledDto = new CategoryDto();
        toggledDto.setStatus(false);

        when(categoryRepository.findByIdentifier("CAT001")).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(toggledDto);

        CategoryDto result = categoryService.toggleStatus("CAT001");

        assertFalse(category.isStatus());
        verify(categoryRepository).save(category);
        assertFalse(result.isStatus());
    }

    @Test
    void toggleStatus_shouldFlipFalseToTrue() {
        category.setStatus(false);
        CategoryDto toggledDto = new CategoryDto();
        toggledDto.setStatus(true);

        when(categoryRepository.findByIdentifier("CAT001")).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(toggledDto);

        CategoryDto result = categoryService.toggleStatus("CAT001");

        assertTrue(category.isStatus());
        verify(categoryRepository).save(category);
        assertTrue(result.isStatus());
    }

    @Test
    void findIfTrue_shouldReturnActiveCategories() {
        Category activeCategory = new Category();
        activeCategory.setIdentifier("CAT001");
        activeCategory.setStatus(true);

        when(categoryRepository.findByStatusTrue()).thenReturn(List.of(activeCategory));
        when(modelMapper.map(activeCategory, CategoryDto.class)).thenReturn(categoryDto);

        List<CategoryDto> result = categoryService.findIfTrue();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isStatus());
    }

    @Test
    void findIfTrue_shouldReturnEmptyList_whenNoActiveCategories() {
        when(categoryRepository.findByStatusTrue()).thenReturn(List.of());

        List<CategoryDto> result = categoryService.findIfTrue();

        assertTrue(result.isEmpty());
    }
}