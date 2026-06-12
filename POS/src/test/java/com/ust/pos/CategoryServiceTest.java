package com.ust.pos;

import com.ust.pos.category.impl.CategoryServiceImpl;
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
import org.springframework.data.domain.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;


    @Test
    void save_success_withNullSuperCategory() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setSuperCategory(null);

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);

        CategoryDto response = categoryService.save(dto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void save_success_withBlankSuperCategory() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setSuperCategory("   ");

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);

        CategoryDto response = categoryService.save(dto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void save_success_withSuperCategoryPresent() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setSuperCategory("PARENT");

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);

        CategoryDto response = categoryService.save(dto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void save_failure_duplicate() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(new Category());

        CategoryDto response = categoryService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Category already exists", response.getMessage());
    }


    @Test
    void findByIdentifier_success() {
        Category category = new Category();
        CategoryDto dto = new CategoryDto();

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(dto);

        Assertions.assertNotNull(categoryService.findByIdentifier("CAT1"));
    }


    @Test
    void update_success_sameIdentifier() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        Category existing = new Category();
        existing.setIdentifier("CAT1");

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(existing);

        CategoryDto response = categoryService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(categoryRepository).save(existing);
    }

    @Test
    void update_failure_notFound() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);

        CategoryDto response = categoryService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Category with identifier - CAT1 not found", response.getMessage());
    }

    @Test
    void update_failure_duplicateIdentifier() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT2");

        Category existing = new Category();
        existing.setIdentifier("CAT1");

        Mockito.when(categoryRepository.findByIdentifier("CAT2")).thenReturn(existing);

        CategoryDto response = categoryService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Category already exists", response.getMessage());
    }


    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "PARENT"})
    void save_success_variousSuperCategory(String superCategory) {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setSuperCategory(superCategory);

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);

        CategoryDto response = categoryService.save(dto);

        Assertions.assertTrue(response.isSuccess());
    }



    @Test
    void delete_success() {
        Mockito.when(categoryRepository.existsBySuperCategory("CAT1")).thenReturn(false);

        categoryService.deleteByIdentifier("CAT1");

        Mockito.verify(categoryRepository).deleteByIdentifier("CAT1");
    }

    @Test
    void delete_failure_usedAsSuperCategory() {
        Mockito.when(categoryRepository.existsBySuperCategory("CAT1")).thenReturn(true);

        Assertions.assertThrows(IllegalStateException.class,
                () -> categoryService.deleteByIdentifier("CAT1"));
    }


    @Test
    void findAll_success() {
        Category category = new Category();
        category.setIdentifier("CAT1");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT1");

        List<Category> categories = List.of(category);
        List<CategoryDto> categoryDtos = List.of(categoryDto);

        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        Mockito.when(modelMapper.map(Mockito.eq(categories), Mockito.any(java.lang.reflect.Type.class)))
                .thenReturn(categoryDtos);

        WsDto<CategoryDto> response = categoryService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1L, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }


    @Test
    void findChildCategories_success() {
        List<Category> categories = List.of(new Category());
        List<CategoryDto> dtos = List.of(new CategoryDto());

        Mockito.when(categoryRepository.findBySuperCategoryIsNotNull()).thenReturn(categories);
        Mockito.when(modelMapper.map(Mockito.eq(categories), Mockito.any(java.lang.reflect.Type.class)))
                .thenReturn(dtos);

        Assertions.assertEquals(1, categoryService.findChildCategories().size());
    }


    @Test
    void toggle_trueToFalse() {
        Category category = new Category();
        category.setStatus(true);

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(new CategoryDto());

        categoryService.toggleStatus("CAT1");

        Assertions.assertFalse(category.isStatus());
    }

    @Test
    void toggle_falseToTrue() {
        Category category = new Category();
        category.setStatus(false);

        Mockito.when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(new CategoryDto());

        categoryService.toggleStatus("CAT1");

        Assertions.assertTrue(category.isStatus());
    }


    @Test
    void findIfTrue_success() {
        Category category = new Category();

        Mockito.when(categoryRepository.findByStatusTrue()).thenReturn(List.of(category));
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(new CategoryDto());

        Assertions.assertEquals(1, categoryService.findIfTrue().size());
    }


    @Test
    void findBySuperCategoryNotNull_success() {
        List<Category> categories = List.of(new Category());
        List<CategoryDto> dtos = List.of(new CategoryDto());

        Mockito.when(categoryRepository.findByStatusTrueAndSuperCategoryIsNot("")).thenReturn(categories);
        Mockito.when(modelMapper.map(Mockito.eq(categories), Mockito.any(java.lang.reflect.Type.class)))
                .thenReturn(dtos);

        Assertions.assertEquals(1, categoryService.findBySuperCategoryNotNull().size());
    }
}