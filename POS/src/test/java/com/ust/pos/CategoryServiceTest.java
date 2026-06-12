package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Category;
import com.ust.pos.modell.CategoryRepository;
import org.junit.jupiter.api.Assertions;
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
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);
        categoryService.save(dto);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void saveFailureTest() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(new Category());
        CategoryDto result = categoryService.save(dto);
        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Category already exists", result.getMessage());
    }

    @Test
    void saveTest_SuperCategoryNull() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setSuperCategory(null);
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);
        categoryService.save(dto);
        verify(categoryRepository).save(argThat(cat -> cat.getSuperCategory() == null));
    }

    @Test
    void saveTest_SuperCategoryBlank() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setSuperCategory("   ");
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);
        categoryService.save(dto);
        verify(categoryRepository).save(argThat(cat -> cat.getSuperCategory() == null));
    }

    @Test
    void saveTest_WithSuperCategory() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setSuperCategory("PARENT");
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);
        categoryService.save(dto);
        verify(categoryRepository).save(argThat(cat -> "PARENT".equals(cat.getSuperCategory())));
    }

    @Test
    void updateTest() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setIdentifier("OLD");
        CategoryDto dto = new CategoryDto();
        dto.setId(1L);
        dto.setIdentifier("NEW");
        when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));
        when(categoryRepository.findByIdentifier("NEW")).thenReturn(null);
        categoryService.update(dto);
        Assertions.assertEquals("NEW", existing.getIdentifier());
        verify(categoryRepository).save(existing);
    }

    @Test
    void updateTest_SuperCategoryNullOrBlank() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setIdentifier("OLD");
        CategoryDto dto = new CategoryDto();
        dto.setId(1L);
        dto.setIdentifier("NEW");
        dto.setSuperCategory("   ");
        when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));
        when(categoryRepository.findByIdentifier("NEW")).thenReturn(null);
        categoryService.update(dto);
        Assertions.assertNull(existing.getSuperCategory());
        verify(categoryRepository).save(existing);
    }

    @Test
    void updateTest_WithSuperCategoryValue() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setIdentifier("OLD");
        CategoryDto dto = new CategoryDto();
        dto.setId(1L);
        dto.setIdentifier("NEW");
        dto.setSuperCategory("PARENT");
        when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));
        when(categoryRepository.findByIdentifier("NEW")).thenReturn(null);
        categoryService.update(dto);
        Assertions.assertEquals("PARENT", existing.getSuperCategory());
    }

    @Test
    void updateFailureNotFoundTest() {
        CategoryDto dto = new CategoryDto();
        dto.setId(1L);
        when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        CategoryDto result = categoryService.update(dto);
        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Category not found", result.getMessage());
    }

    @Test
    void updateFailureDuplicateTest() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setIdentifier("OLD");
        CategoryDto dto = new CategoryDto();
        dto.setId(1L);
        dto.setIdentifier("NEW");
        when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));
        when(categoryRepository.findByIdentifier("NEW")).thenReturn(new Category());
        CategoryDto result = categoryService.update(dto);
        Assertions.assertFalse(result.isSuccess());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        when(categoryRepository.existsBySuperCategory("CAT1")).thenReturn(false);
        categoryService.delete("CAT1");
        verify(categoryRepository).deleteByIdentifier("CAT1");
    }

    @Test
    void delete_WithChildCategories_ShouldThrowException() {
        when(categoryRepository.existsBySuperCategory("CAT1")).thenReturn(true);
        Assertions.assertThrows(IllegalStateException.class, () -> categoryService.delete("CAT1"));
        verify(categoryRepository, never()).deleteByIdentifier(any());
    }

    @Test
    void findByIdentifierTest() {
        Category category = new Category();
        CategoryDto dto = new CategoryDto();
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(dto);
        Assertions.assertNotNull(categoryService.findByIdentifier("CAT1"));
    }

    @Test
    void findAllTest() {
        Category category = new Category();
        category.setIdentifier("Admin");
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");
        List<Category> categories = List.of(category);
        List<CategoryDto> categoryDtos = List.of(categoryDto);
        Pageable pageable = PageRequest.of(0, 50);
        Page<Category> page = new PageImpl<>(categories, pageable, 1);
        when(categoryRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(eq(categories), any(Type.class))).thenReturn(categoryDtos);
        WsDto<CategoryDto> response = categoryService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
    }

    @Test
    void findChildCategoriesTest() {
        List<Category> list = List.of(new Category());
        List<CategoryDto> dtoList = List.of(new CategoryDto());
        when(categoryRepository.findBySuperCategoryIsNotNull()).thenReturn(list);
        when(modelMapper.map(eq(list), any(Type.class))).thenReturn(dtoList);
        List<CategoryDto> result = categoryService.findChildCategories();
        Assertions.assertEquals(1, result.size());
    }

    @Test
    void toggleStatusTest() {
        Category category = new Category();
        category.setIdentifier("CAT1");
        category.setStatus(true);
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(new CategoryDto());
        categoryService.toggleStatus("CAT1");
        Assertions.assertFalse(category.getStatus());
    }

    @Test
    void toggleStatus_NullStatusTest() {
        Category category = new Category();
        category.setIdentifier("CAT1");
        category.setStatus(null);
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(new CategoryDto());
        categoryService.toggleStatus("CAT1");
        Assertions.assertTrue(category.getStatus());
    }

    @Test
    void toggleStatus_NotFoundTest() {
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> categoryService.toggleStatus("CAT1")
        );
    }
}