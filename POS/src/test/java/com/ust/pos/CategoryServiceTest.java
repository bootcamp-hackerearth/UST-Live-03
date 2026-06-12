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
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        Category category = new Category();
        category.setIdentifier("CAT1");

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Category.class)).thenReturn(category);

        CategoryDto result = categoryService.save(dto);

        Assertions.assertEquals("CAT1", result.getIdentifier());

        verify(categoryRepository).save(category);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        Category existing = new Category();
        existing.setIdentifier("CAT1");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(existing);

        CategoryDto result = categoryService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Category with identifier - CAT1 already exists", result.getMessage());
    }

    @Test
    void updateSuccessTest() {
        Category existing = new Category();
        existing.setIdentifier("CAT2");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT2");

        Mockito.when(categoryRepository.findByIdentifier("CAT2")).thenReturn(existing);
        Mockito.when(modelMapper.map(dto, Category.class)).thenReturn(existing);

        CategoryDto result = categoryService.update(dto);

        Assertions.assertEquals("CAT2", result.getIdentifier());

        verify(categoryRepository).save(existing);
    }

    @Test
    void updateFailureNotFoundTest() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("UNKNOWN");

        Mockito.when(categoryRepository.findByIdentifier("UNKNOWN")).thenReturn(null);

        CategoryDto result = categoryService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Category with identifier - UNKNOWN is not found", result.getMessage());
    }

    @Test
    void deleteTest() {
        categoryService.delete("CAT3");
        verify(categoryRepository).deleteByIdentifier("CAT3");
    }

    @Test
    void findAllTest() {
        List<Category> categories = Arrays.asList(new Category(), new Category());
        List<CategoryDto> dtoList = Arrays.asList(new CategoryDto(), new CategoryDto());

        Pageable pageable = PageRequest.of(0, 10);

        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        Mockito.when(modelMapper.map(Mockito.eq(categories), Mockito.any(Type.class))).thenReturn(dtoList);

        WsDto<CategoryDto> result = categoryService.findAll(pageable);

        Assertions.assertEquals(2, result.getDtoList().size());
        Assertions.assertEquals(2, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());

        Mockito.verify(categoryRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(Mockito.eq(categories), Mockito.any(Type.class));
    }

    @Test
    void findByIdentifierSuccessTest() {
        Category category = new Category();
        category.setIdentifier("CAT4");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT4");

        Mockito.when(categoryRepository.findByIdentifier("CAT4")).thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(dto);

        CategoryDto result = categoryService.findByIdentifier("CAT4");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("CAT4", result.getIdentifier());
    }

    @Test
    void findBySuperCategoryNotNullTest() {
        Category cat1 = new Category();
        cat1.setIdentifier("CAT_CHILD");

        List<Category> categories = Arrays.asList(cat1);

        CategoryDto dto1 = new CategoryDto();
        dto1.setIdentifier("CAT_CHILD");

        List<CategoryDto> dtoList = Arrays.asList(dto1);

        Mockito.when(categoryRepository.findBySuperCategoryIsNot("")).thenReturn(categories);
        Mockito.when(modelMapper.map(Mockito.eq(categories), Mockito.any(Type.class))).thenReturn(dtoList);

        List<CategoryDto> result = categoryService.findBySuperCategoryNotNull();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("CAT_CHILD", result.get(0).getIdentifier());
    }

    @Test
    void toggleStatusTest() {
        Category category = new Category();
        category.setIdentifier("CAT1");
        category.setStatus(true);

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);

        categoryService.toggleStatus("CAT1");

        Assertions.assertFalse(category.getStatus());

        Mockito.verify(categoryRepository).save(category);
    }
}