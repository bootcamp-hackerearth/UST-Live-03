package com.ust.pos;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import com.ust.pos.category.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

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
        dto.setIdentifier("C0020");

        Category category = new Category();

        Mockito.when(categoryRepository.findByIdentifier("C0020")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Category.class)).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);

        CategoryDto response = categoryService.save(dto);
        Assertions.assertEquals("C0020", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveFailure_existingActive() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("C0020");

        Category existing = new Category();
        existing.setDeleted(false);

        Mockito.when(categoryRepository.findByIdentifier("C0020")).thenReturn(existing);
        CategoryDto response = categoryService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveFailure_softDeleted() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("C0020");

        Category existing = new Category();
        existing.setDeleted(true);

        Mockito.when(categoryRepository.findByIdentifier("C0020")).thenReturn(existing);
        CategoryDto response = categoryService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void findByIdentifierTest() {
        Category category = new Category();
        category.setIdentifier("C0020");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("C0020");

        Mockito.when(categoryRepository.findByIdentifier("C0020")).thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(dto);
        CategoryDto response = categoryService.findByIdentifier("C0020");
        Assertions.assertEquals("C0020", response.getIdentifier());
    }

    @Test
    void updateTest() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("C0020");

        Category existing = new Category();

        Mockito.when(categoryRepository.findByIdentifier("C0020")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(categoryRepository.save(existing)).thenReturn(existing);
        CategoryDto response = categoryService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(categoryRepository).save(existing);
    }

    @Test
    void updateTestFailure() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("C0020");

        Mockito.when(categoryRepository.findByIdentifier("C0020")).thenReturn(null);
        CategoryDto response = categoryService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Category category = new Category();
        category.setDeleted(false);

        Mockito.when(categoryRepository.findByIdentifier("C0020")).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);

        categoryService.delete("C0020");
        Mockito.verify(categoryRepository).findByIdentifier("C0020");
        Mockito.verify(categoryRepository).save(category);
        Assertions.assertTrue(category.isDeleted());
    }

    @Test
    void findAllTest() {
        Category category = new Category();
        category.setIdentifier("C0020");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("C0020");

        List<Category> list = List.of(category);
        Page<Category> page = new PageImpl<>(list, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(categoryRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(List.of(dto));

        WsDto<CategoryDto> response = categoryService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("C0020", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void findChildCategoriesTest() {
        Category category = new Category();
        category.setIdentifier("C0021");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("C0021");

        List<Category> categories = List.of(category);
        Mockito.when(categoryRepository.findBySuperCategoryIsNotNull()).thenReturn(categories);
        Mockito.when(modelMapper.map(Mockito.eq(categories), Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(List.of(dto));

        List<CategoryDto> response = categoryService.findChildCategories();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("C0021", response.get(0).getIdentifier());
    }
}