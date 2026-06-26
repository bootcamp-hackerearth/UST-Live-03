package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
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
    void saveTest() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("S1");
        Mockito.when(categoryRepository.findByIdentifier("S1")).thenReturn(null);
        Category category = new Category();
        Mockito.when(modelMapper.map(dto, Category.class)).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        CategoryDto response = categoryService.save(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestAlreadyExists() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("S1");
        Category existing = new Category();
        existing.setDeleted(false);
        Mockito.when(categoryRepository.findByIdentifier("S1")).thenReturn(existing);
        CategoryDto response = categoryService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveTestDeletedExists() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("S1");
        Category existing = new Category();
        existing.setDeleted(true);
        Mockito.when(categoryRepository.findByIdentifier("S1")).thenReturn(existing);
        CategoryDto response = categoryService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTest() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("S1");
        Category existing = new Category();
        Mockito.when(categoryRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(categoryRepository.save(existing)).thenReturn(existing);
        CategoryDto response = categoryService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("S1");
        Mockito.when(categoryRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(null);
        CategoryDto response = categoryService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Mockito.when(categoryRepository.existsBySuperCategoryAndDeletedFalse("S1")).thenReturn(false);
        Category category = new Category();
        Mockito.when(categoryRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        categoryService.delete("S1");
        Mockito.verify(categoryRepository).save(category);
    }

    @Test
    void deleteTestWithDependency() {
        Mockito.when(categoryRepository.existsBySuperCategoryAndDeletedFalse("S1")).thenReturn(true);
        Assertions.assertThrows(IllegalArgumentException.class, () -> categoryService.delete("S1"));
    }

    @Test
    void findAllTest() {
        Category category = new Category();
        CategoryDto dto = new CategoryDto();
        List<Category> list = List.of(category);
        List<CategoryDto> dtoList = List.of(dto);
        Pageable pageable = PageRequest.of(0, 1);
        Page<Category> page = new PageImpl<>(list);
        Mockito.when(categoryRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(Type.class))).thenReturn(dtoList);
        WsDto<CategoryDto> response = categoryService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void findByIdentifierTest() {
        Category category = new Category();
        CategoryDto dto = new CategoryDto();
        Mockito.when(categoryRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(dto);
        CategoryDto response = categoryService.findByIdentifier("S1");
        Assertions.assertNotNull(response);
    }

    @Test
    void findBySuperCategoryNotNullTest() {
        Category category = new Category();
        CategoryDto dto = new CategoryDto();
        List<Category> list = List.of(category);
        List<CategoryDto> dtoList = List.of(dto);
        Mockito.when(categoryRepository.findBySuperCategoryIsNotAndDeletedFalse("")).thenReturn(list);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(Type.class))).thenReturn(dtoList);
        List<CategoryDto> response = categoryService.findBySuperCategoryNotNull();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void updateStatusTest() {
        Category category = new Category();
        Mockito.when(categoryRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        categoryService.updateStatus("S1", true);
        Mockito.verify(categoryRepository).save(category);
    }

    @Test
    void updateStatusNullTest() {
        Mockito.when(categoryRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(null);
        categoryService.updateStatus("S1", true);
        Mockito.verify(categoryRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllActiveTest() {
        Category category = new Category();
        CategoryDto dto = new CategoryDto();
        List<Category> list = List.of(category);
        List<CategoryDto> dtoList = List.of(dto);
        Mockito.when(categoryRepository.findByStatusAndDeletedFalse(true)).thenReturn(list);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(Type.class))).thenReturn(dtoList);
        List<CategoryDto> response = categoryService.findAllActive();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findByIdentifierNullTest() {
        Mockito.when(categoryRepository.findByIdentifierAndDeletedFalse("S1"))
                .thenReturn(null);
        CategoryDto response = categoryService.findByIdentifier("S1");
        Assertions.assertNull(response);
    }

    @Test
    void deleteTestCategoryNull() {
        Mockito.when(categoryRepository.existsBySuperCategoryAndDeletedFalse("S1"))
                .thenReturn(false);
        Mockito.when(categoryRepository.findByIdentifierAndDeletedFalse("S1"))
                .thenReturn(null);
        categoryService.delete("S1");
        Mockito.verify(categoryRepository, Mockito.never())
                .save(Mockito.any());
    }
}