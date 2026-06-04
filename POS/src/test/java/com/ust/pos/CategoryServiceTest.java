package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
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

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierTest() {

        Category category = new Category();
        category.setIdentifier("CAT1");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);

        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(dto);

        CategoryDto response = categoryService.findByIdentifier("CAT1");

        Assertions.assertEquals("CAT1", response.getIdentifier());

        Mockito.verify(categoryRepository).findByIdentifier("CAT1");
    }

    @Test
    void findByIdentifierNullTest() {

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);

        CategoryDto response = categoryService.findByIdentifier("CAT1");

        Assertions.assertNull(response);
    }

    @Test
    void saveTest() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setSuperCategory("SUPER");

        CategoryDto mappedDto = new CategoryDto();
        mappedDto.setIdentifier("CAT1");
        mappedDto.setSuccess(true);

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);

        Mockito.when(modelMapper.map(Mockito.any(Category.class), Mockito.eq(CategoryDto.class))).thenReturn(mappedDto);

        CategoryDto response = categoryService.save(dto);

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertEquals("CAT1", response.getIdentifier());

        Mockito.verify(categoryRepository).save(Mockito.any(Category.class));
    }

    @Test
    void saveWithNullSuperCategoryTest() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setSuperCategory(null);

        CategoryDto mappedDto = new CategoryDto();
        mappedDto.setIdentifier("CAT1");

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);

        Mockito.when(modelMapper.map(Mockito.any(Category.class), Mockito.eq(CategoryDto.class))).thenReturn(mappedDto);

        CategoryDto response = categoryService.save(dto);

        Assertions.assertNotNull(response);

        Mockito.verify(categoryRepository).save(Mockito.argThat(category -> category.getSuperCategory() == null));
    }

    @Test
    void saveWithBlankSuperCategoryTest() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setSuperCategory("   ");

        CategoryDto mappedDto = new CategoryDto();
        mappedDto.setIdentifier("CAT1");

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);

        Mockito.when(modelMapper.map(Mockito.any(Category.class), Mockito.eq(CategoryDto.class))).thenReturn(mappedDto);

        CategoryDto response = categoryService.save(dto);

        Assertions.assertNotNull(response);

        Mockito.verify(categoryRepository).save(Mockito.argThat(category -> category.getSuperCategory() == null));
    }

    @Test
    void saveDuplicateTest() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        Category existing = new Category();

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(existing);

        CategoryDto response = categoryService.save(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Category with identifier - CAT1 already exists", response.getMessage());
    }

    @Test
    void updateTest() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setSuperCategory("SUPER");

        Category category = new Category();
        category.setIdentifier("CAT1");

        CategoryDto mappedDto = new CategoryDto();
        mappedDto.setIdentifier("CAT1");
        mappedDto.setSuccess(true);

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);

        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(mappedDto);

        CategoryDto response = categoryService.update(dto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(categoryRepository).save(category);
    }

    @Test
    void updateWithNullSuperCategoryTest() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setSuperCategory(null);

        Category category = new Category();
        category.setIdentifier("CAT1");
        category.setSuperCategory("OLD");

        CategoryDto mappedDto = new CategoryDto();

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);

        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(mappedDto);

        categoryService.update(dto);

        Assertions.assertNull(category.getSuperCategory());

        Mockito.verify(categoryRepository).save(category);
    }

    @Test
    void updateWithBlankSuperCategoryTest() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setSuperCategory("   ");

        Category category = new Category();
        category.setIdentifier("CAT1");
        category.setSuperCategory("OLD");

        CategoryDto mappedDto = new CategoryDto();

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);

        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(mappedDto);

        categoryService.update(dto);

        Assertions.assertNull(category.getSuperCategory());

        Mockito.verify(categoryRepository).save(category);
    }

    @Test
    void updateNotFoundTest() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);

        CategoryDto response = categoryService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Category with identifier - CAT1 not found", response.getMessage());
    }

    @Test
    void deleteTest() {

        Mockito.doNothing().when(categoryRepository).deleteByIdentifier("CAT1");

        boolean response = categoryService.delete("CAT1");

        Assertions.assertTrue(response);

        Mockito.verify(categoryRepository).deleteByIdentifier("CAT1");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Category category = new Category();
        category.setIdentifier("CAT1");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        List<Category> categories = List.of(category);
        List<CategoryDto> dtos = List.of(dto);

        Page<Category> categoryPage = new PageImpl<>(categories);

        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);

        Mockito.when(modelMapper.map(Mockito.eq(categories), Mockito.any(Type.class))).thenReturn(dtos);

        List<CategoryDto> response = categoryService.findAll(pageable);

        Assertions.assertEquals(1, response.size());

        Mockito.verify(categoryRepository).findAll(pageable);
    }

    @Test
    void findSuperCategoriesTest() {

        Category category = new Category();
        category.setIdentifier("CAT1");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        List<Category> categories = List.of(category);
        List<CategoryDto> dtos = List.of(dto);

        Mockito.when(categoryRepository.findBySuperCategoryIsNull()).thenReturn(categories);

        Mockito.when(modelMapper.map(Mockito.eq(categories), Mockito.any(Type.class))).thenReturn(dtos);

        List<CategoryDto> response = categoryService.findSuperCategories();

        Assertions.assertEquals(1, response.size());

        Mockito.verify(categoryRepository).findBySuperCategoryIsNull();
    }

    @Test
    void toggleStatusTrueToFalseTest() {

        Category category = new Category();
        category.setIdentifier("CAT1");
        category.setSuperCategory("SUPER");
        category.setStatus(true);

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);

        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(dto);

        CategoryDto response = categoryService.toggleStatus("CAT1");

        Assertions.assertEquals("CAT1", response.getIdentifier());

        Assertions.assertFalse(category.isStatus());

        Mockito.verify(categoryRepository).save(category);
    }

    @Test
    void toggleStatusFalseToTrueTest() {

        Category category = new Category();
        category.setIdentifier("CAT1");
        category.setSuperCategory("SUPER");
        category.setStatus(false);

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);

        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(dto);

        CategoryDto response = categoryService.toggleStatus("CAT1");

        Assertions.assertEquals("CAT1", response.getIdentifier());

        Assertions.assertTrue(category.isStatus());

        Mockito.verify(categoryRepository).save(category);
    }

    @Test
    void toggleStatusNoSuperCategoryTest() {

        Category category = new Category();
        category.setIdentifier("CAT1");
        category.setSuperCategory(null);

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);

        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(dto);

        CategoryDto response = categoryService.toggleStatus("CAT1");

        Assertions.assertEquals("CAT1", response.getIdentifier());

        Mockito.verify(categoryRepository, Mockito.never()).save(category);
    }

    @Test
    void toggleStatusNullTest() {

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);

        CategoryDto response = categoryService.toggleStatus("CAT1");

        Assertions.assertNull(response);
    }

    @Test
    void findIfTrueTest() {

        Category category1 = new Category();
        category1.setIdentifier("CAT1");
        category1.setStatus(true);
        category1.setSuperCategory("SUPER");

        Category category2 = new Category();
        category2.setIdentifier("CAT2");
        category2.setStatus(false);
        category2.setSuperCategory("SUPER");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        List<Category> categories = List.of(category1, category2);
        List<CategoryDto> dtos = List.of(dto);

        Mockito.when(categoryRepository.findBySuperCategoryIsNotNull()).thenReturn(categories);

        Mockito.when(modelMapper.map(Mockito.anyList(), Mockito.any(Type.class))).thenReturn(dtos);

        List<CategoryDto> response = categoryService.findIfTrue();

        Assertions.assertEquals(1, response.size());

        Mockito.verify(categoryRepository).findBySuperCategoryIsNotNull();
    }
}