package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.modell.Category;
import com.ust.pos.modell.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import java.util.ArrayList;
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
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);
        categoryService.save(dto);
        Mockito.verify(categoryRepository).save(Mockito.any(Category.class));
    }

    @Test
    void saveFailureTest() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(new Category());
        CategoryDto result = categoryService.save(dto);
        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Category already exists", result.getMessage());
    }

    @Test
    void saveTest_SuperCategoryNull() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setSuperCategory(null);
        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);
        categoryService.save(dto);
        Mockito.verify(categoryRepository).save(Mockito.argThat(cat -> cat.getSuperCategory() == null)
        );
    }

    @Test
    void saveTest_SuperCategoryBlank() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setSuperCategory("   ");
        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);
        categoryService.save(dto);
        Mockito.verify(categoryRepository).save(Mockito.argThat(cat -> cat.getSuperCategory() == null)
        );
    }

    @Test
    void saveTest_WithSuperCategory() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setSuperCategory("PARENT");
        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);
        categoryService.save(dto);
        Mockito.verify(categoryRepository).save(Mockito.argThat(cat -> "PARENT".equals(cat.getSuperCategory()))
        );
    }

    @Test
    void updateTest() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setIdentifier("OLD");
        CategoryDto dto = new CategoryDto();
        dto.setId(1L);
        dto.setIdentifier("NEW");
        Mockito.when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));
        Mockito.when(categoryRepository.findByIdentifier("NEW")).thenReturn(null);
        categoryService.update(dto);
        Assertions.assertEquals("NEW", existing.getIdentifier());
        Mockito.verify(categoryRepository).save(existing);
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
        Mockito.when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));
        Mockito.when(categoryRepository.findByIdentifier("NEW")).thenReturn(null);
        categoryService.update(dto);
        Assertions.assertEquals("NEW", existing.getIdentifier());
        Assertions.assertNull(existing.getSuperCategory());
        Mockito.verify(categoryRepository).save(existing);
    }

    @Test
    void updateTest_Identifier() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setIdentifier("OLD");
        CategoryDto dto = new CategoryDto();
        dto.setId(1L);
        dto.setIdentifier("NEW");
        Mockito.when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));
        Mockito.when(categoryRepository.findByIdentifier("NEW")).thenReturn(null); // no duplicate
        categoryService.update(dto);
        Assertions.assertEquals("NEW", existing.getIdentifier());
        Mockito.verify(categoryRepository).save(existing);
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
        Mockito.when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));
        Mockito.when(categoryRepository.findByIdentifier("NEW")).thenReturn(null);
        categoryService.update(dto);
        Assertions.assertEquals("NEW", existing.getIdentifier());
        Assertions.assertEquals("PARENT", existing.getSuperCategory());
        Mockito.verify(categoryRepository).save(existing);
    }

    @Test
    void updateFailureNotFoundTest() {
        CategoryDto dto = new CategoryDto();
        dto.setId(1L);
        Mockito.when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.empty());
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
        Mockito.when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));
        Mockito.when(categoryRepository.findByIdentifier("NEW")).thenReturn(new Category());
        CategoryDto result = categoryService.update(dto);
        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Category already exists", result.getMessage());
        Mockito.verify(categoryRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteTest() {
        Mockito.when(categoryRepository.existsBySuperCategory("CAT1")).thenReturn(false);
        categoryService.deleteByIdentifier("CAT1");
        Mockito.verify(categoryRepository).deleteByIdentifier("CAT1");
    }

    @Test
    void deleteFailureTest() {
        Mockito.when(categoryRepository.existsBySuperCategory("CAT1")).thenReturn(true);
        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> {
            categoryService.deleteByIdentifier("CAT1");
        });
        Assertions.assertTrue(ex.getMessage().contains("Cannot delete"));
    }

    @Test
    void findByIdentifierTest() {
        Category category = new Category();
        CategoryDto dto = new CategoryDto();
        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(dto);
        CategoryDto result = categoryService.findByIdentifier("CAT1");
        Assertions.assertNotNull(result);
    }

    @Test
    void findAllTest() {
        Category category = new Category();
        category.setIdentifier("Admin");
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("Admin");
        List<Category> categories = List.of(category);
        List<CategoryDto> categoryDtos = List.of(categoryDto);
        Page<Category> categoryPage = new PageImpl<>(categories, PageRequest.of(0, 2), categories.size());
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        Mockito.when(modelMapper.map(Mockito.eq(categories), Mockito.any(java.lang.reflect.Type.class))).thenReturn(categoryDtos);
        List<CategoryDto> response = categoryService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findChildCategoriesTest() {
        List<Category> list = List.of(new Category());
        List<CategoryDto> dtoList = List.of(new CategoryDto());
        Mockito.when(categoryRepository.findBySuperCategoryIsNotNull()).thenReturn(list);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(java.lang.reflect.Type.class))).thenReturn(dtoList);
        List<CategoryDto> result = categoryService.findChildCategories();
        Assertions.assertEquals(1, result.size());
    }
}