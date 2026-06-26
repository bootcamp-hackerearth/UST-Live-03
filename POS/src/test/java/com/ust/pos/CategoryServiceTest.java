package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Category;
import com.ust.pos.modell.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    void save_Success() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);
        CategoryDto result = categoryService.save(dto);
        assertNotNull(result);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void save_AlreadyExists() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(new Category());
        CategoryDto result = categoryService.save(dto);
        assertFalse(result.isSuccess());
        assertEquals("Category with identifier - CAT1 already exists", result.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void save_SuperCategoryNull() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setSuperCategory(null);
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);
        categoryService.save(dto);
        verify(categoryRepository).save(argThat(cat -> cat.getSuperCategory() == null));
    }

    @Test
    void save_SuperCategoryBlank() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setSuperCategory(" ");
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);
        categoryService.save(dto);
        verify(categoryRepository).save(argThat(cat -> cat.getSuperCategory() == null));
    }

    @Test
    void save_SuperCategoryPresent() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setSuperCategory("PARENT");
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);
        categoryService.save(dto);
        verify(categoryRepository).save(argThat(cat -> "PARENT".equals(cat.getSuperCategory())));
    }

    @Test
    void update_Success() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setIdentifier("OLD");
        CategoryDto dto = new CategoryDto();
        dto.setId(1L);
        dto.setIdentifier("NEW");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findByIdentifierAndDeletedFalse("NEW")).thenReturn(null);
        CategoryDto result = categoryService.update(dto);
        assertNotNull(result);
        verify(categoryRepository).save(existing);
        assertEquals("NEW", existing.getIdentifier());
    }

    @Test
    void update_NotFound() {
        CategoryDto dto = new CategoryDto();
        dto.setId(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        CategoryDto result = categoryService.update(dto);
        assertFalse(result.isSuccess());
        assertEquals("Category not found", result.getMessage()
        );
    }

    @Test
    void update_DuplicateIdentifier() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setIdentifier("OLD");
        CategoryDto dto = new CategoryDto();
        dto.setId(1L);
        dto.setIdentifier("NEW");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findByIdentifierAndDeletedFalse("NEW")).thenReturn(new Category());
        CategoryDto result = categoryService.update(dto);
        assertFalse(result.isSuccess());
        assertEquals("Category already exists", result.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void update_SuperCategoryNull() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setIdentifier("OLD");
        CategoryDto dto = new CategoryDto();
        dto.setId(1L);
        dto.setIdentifier("OLD");
        dto.setSuperCategory(null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        categoryService.update(dto);
        assertNull(existing.getSuperCategory());
    }

    @Test
    void update_SuperCategoryPresent() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setIdentifier("OLD");
        CategoryDto dto = new CategoryDto();
        dto.setId(1L);
        dto.setIdentifier("OLD");
        dto.setSuperCategory("PARENT");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        categoryService.update(dto);
        assertEquals("PARENT", existing.getSuperCategory()
        );
    }

    @Test
    void delete_Success() {
        Category category = new Category();
        category.setIdentifier("CAT1");
        category.setDeleted(false);
        when(categoryRepository.findByIdentifierAndDeletedFalse("CAT1")).thenReturn(category);
        categoryService.delete("CAT1");
        verify(categoryRepository).save(category);
        assertTrue(category.getDeleted());
    }

    @Test
    void findByIdentifier_Success() {
        Category category = new Category();
        CategoryDto dto = new CategoryDto();
        when(categoryRepository.findByIdentifierAndDeletedFalse("CAT1")).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(dto);
        CategoryDto result = categoryService.findByIdentifier("CAT1");
        assertNotNull(result);
    }

    @Test
    void findAll_Success() {
        Category category = new Category();
        CategoryDto dto = new CategoryDto();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> page = new PageImpl<>(List.of(category), pageable, 1);
        when(categoryRepository.findALlByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(List.of(category)), any(Type.class))).thenReturn(List.of(dto));
        WsDto<CategoryDto> result = categoryService.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
    }

    @Test
    void findChildCategories_Success() {
        List<Category> categories = List.of(new Category());
        List<CategoryDto> dtos = List.of(new CategoryDto());
        when(categoryRepository.findBySuperCategoryIsNotNull()).thenReturn(categories);
        when(modelMapper.map(eq(categories), any(Type.class))).thenReturn(dtos);
        List<CategoryDto> result = categoryService.findChildCategories();
        assertEquals(1, result.size());
    }

    @Test
    void toggleStatus_TrueToFalse() {
        Category category = new Category();
        category.setIdentifier("CAT1");
        category.setStatus(true);
        when(categoryRepository.findByIdentifierAndDeletedFalse("CAT1")).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(new CategoryDto());
        categoryService.toggleStatus("CAT1");
        assertFalse(category.getStatus());
    }

    @Test
    void toggleStatus_FalseToTrue() {
        Category category = new Category();
        category.setIdentifier("CAT1");
        category.setStatus(false);
        when(categoryRepository.findByIdentifierAndDeletedFalse("CAT1")).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(new CategoryDto());
        categoryService.toggleStatus("CAT1");
        assertTrue(category.getStatus());
    }

    @Test
    void toggleStatus_NullToTrue() {
        Category category = new Category();
        category.setIdentifier("CAT1");
        category.setStatus(null);
        when(categoryRepository.findByIdentifierAndDeletedFalse("CAT1")).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(new CategoryDto());
        categoryService.toggleStatus("CAT1");
        assertTrue(category.getStatus());
    }

    @Test
    void toggleStatus_NotFound() {
        when(categoryRepository.findByIdentifierAndDeletedFalse("CAT1")).thenReturn(null);
        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> categoryService.toggleStatus("CAT1")
                );
        assertEquals(
                "Product not found with identifier: CAT1",
                ex.getMessage()
        );
    }

    @Test
    void findAllActive_Success() {
        Category category = new Category();
        CategoryDto dto = new CategoryDto();
        when(categoryRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of(category));
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(dto);
        List<CategoryDto> result = categoryService.findAllActive();
        assertEquals(1, result.size());
    }

    @Test
    void findAllActive_Empty() {
        when(categoryRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of());
        List<CategoryDto> result = categoryService.findAllActive();
        assertTrue(result.isEmpty());
    }
}