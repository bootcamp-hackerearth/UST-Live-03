package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Save Category - Success with Super Category")
    void saveTest_Success_WithSuperCategory() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT01");
        dto.setSuperCategory("PARENT");

        Category categoryEntity = new Category();
        Mockito.when(categoryRepository.findByIdentifier("CAT01")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Category.class)).thenReturn(categoryEntity);

        CategoryDto response = categoryService.save(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Category Added Successfully", response.getMessage());
        Assertions.assertEquals("PARENT", categoryEntity.getSuperCategory());
        Mockito.verify(categoryRepository).save(categoryEntity);
    }

    @Test
    @DisplayName("Save Category - Success with Blank Super Category")
    void saveTest_Success_BlankSuperCategory() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT01");
        dto.setSuperCategory("   ");

        Category categoryEntity = new Category();
        Mockito.when(categoryRepository.findByIdentifier("CAT01")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Category.class)).thenReturn(categoryEntity);

        CategoryDto response = categoryService.save(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Category Added Successfully", response.getMessage());
        Assertions.assertNull(categoryEntity.getSuperCategory());
        Mockito.verify(categoryRepository).save(categoryEntity);
    }

    @Test
    @DisplayName("Save Category - Success with Null Super Category")
    void saveTest_Success_NullSuperCategory() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT01");
        dto.setSuperCategory(null);

        Category categoryEntity = new Category();
        Mockito.when(categoryRepository.findByIdentifier("CAT01")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Category.class)).thenReturn(categoryEntity);

        CategoryDto response = categoryService.save(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Category Added Successfully", response.getMessage());
        Assertions.assertNull(categoryEntity.getSuperCategory());
        Mockito.verify(categoryRepository).save(categoryEntity);
    }

    @Test
    @DisplayName("Save Category - Error: Self Super Category")
    void saveTest_Error_SelfSuperCategory() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT01");
        dto.setSuperCategory("CAT01");

        Mockito.when(categoryRepository.findByIdentifier("CAT01")).thenReturn(null);

        CategoryDto response = categoryService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Category cannot be its own super category", response.getMessage());
        Mockito.verify(categoryRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("Save Category - Error: Already Exists")
    void saveTest_Error_AlreadyExists() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT01");

        Mockito.when(categoryRepository.findByIdentifier("CAT01")).thenReturn(new Category());

        CategoryDto response = categoryService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Category with identifier - CAT01 already exists", response.getMessage());
        Mockito.verify(categoryRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("Update Category - Success")
    void updateTest_Success() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT01");
        Category existing = new Category();

        Mockito.when(categoryRepository.findByIdentifier("CAT01")).thenReturn(existing);

        CategoryDto response = categoryService.update(dto);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Category with identifier - CAT01 Updated ", response.getMessage());
        Mockito.verify(modelMapper).map(dto, existing);
        Mockito.verify(categoryRepository).save(existing);
    }

    @Test
    @DisplayName("Update Category - Not Found")
    void updateTest_NotFound() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT01");

        Mockito.when(categoryRepository.findByIdentifier("CAT01")).thenReturn(null);

        CategoryDto response = categoryService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Category with identifier - CAT01 not found", response.getMessage());
        Mockito.verify(categoryRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("Toggle Status - Flip boolean")
    void toggleStatus_Test() {
        Category category = new Category();
        category.setStatus(true);

        Mockito.when(categoryRepository.findByIdentifier("CAT01")).thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(new CategoryDto());

        categoryService.toggleStatus("CAT01");

        Assertions.assertFalse(category.isStatus());
        Mockito.verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("Find All - Paginated Success with WsDto Mapping")
    void findAllTest() {
        Pageable pageable = PageRequest.of(1, 10);
        List<Category> categories = List.of(new Category());

        int mockTotalRecords = 30;
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, mockTotalRecords);
        List<CategoryDto> dtos = List.of(new CategoryDto());

        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        Mockito.when(modelMapper.map(eq(categories), any(Type.class))).thenReturn(dtos);

        WsDto<CategoryDto> result = categoryService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(dtos, result.getDtoList());

        Assertions.assertEquals(30, result.getTotalRecords());

        Assertions.assertEquals(3, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(1, result.getPage());

        Mockito.verify(categoryRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Find All Active - Success")
    void findAllActiveTest() {
        List<Category> list = List.of(new Category());
        Mockito.when(categoryRepository.findByStatusTrueAndSuperCategoryIsNot("")).thenReturn(list);
        Mockito.when(modelMapper.map(eq(list), any(Type.class))).thenReturn(List.of(new CategoryDto()));

        List<CategoryDto> result = categoryService.findAllActive();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifierTest() {
        Category category = new Category();
        Mockito.when(categoryRepository.findByIdentifier("CAT01")).thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(new CategoryDto());

        CategoryDto result = categoryService.findByIdentifier("CAT01");

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Delete - Success")
    void deleteTest() {
        boolean result = categoryService.delete("CAT01");
        Assertions.assertTrue(result);
        Mockito.verify(categoryRepository).deleteByIdentifier("CAT01");
    }
}