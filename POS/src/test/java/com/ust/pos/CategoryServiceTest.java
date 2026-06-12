package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest_WhenCategoryAlreadyExists() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT001");

        Category existingCategory = new Category();

        Mockito.when(categoryRepository.findByIdentifier("CAT001"))
                .thenReturn(existingCategory);

        CategoryDto response = categoryService.save(categoryDto);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("already exists"));
    }

    @Test
    void saveTest_WhenNewCategory() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT001");

        Category category = new Category();

        Mockito.when(categoryRepository.findByIdentifier("CAT001"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(categoryDto, Category.class))
                .thenReturn(category);

        Mockito.when(categoryRepository.save(category))
                .thenReturn(category);

        CategoryDto response = categoryService.save(categoryDto);

        assertNotNull(response);
        Mockito.verify(categoryRepository).save(category);
    }

    @Test
    void updateTest_WhenCategoryNotFound() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT001");

        Mockito.when(categoryRepository.findByIdentifier("CAT001"))
                .thenReturn(null);

        CategoryDto response = categoryService.update(categoryDto);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("not found"));
    }

    @Test
    void updateTest_WhenCategoryExists() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT001");

        Category existingCategory = new Category();

        Mockito.when(categoryRepository.findByIdentifier("CAT001"))
                .thenReturn(existingCategory);

        CategoryDto response = categoryService.update(categoryDto);

        assertNotNull(response);
        Mockito.verify(modelMapper).map(categoryDto, existingCategory);
        Mockito.verify(categoryRepository).save(existingCategory);
    }

    @Test
    void deleteTest() {
        boolean result = categoryService.delete("CAT001");

        assertTrue(result);
        Mockito.verify(categoryRepository)
                .deleteByIdentifier("CAT001");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Category category = new Category();
        List<Category> categoryList = Arrays.asList(category);

        Page<Category> page =
                new PageImpl<>(categoryList, pageable, categoryList.size());

        List<CategoryDto> dtoList =
                Arrays.asList(new CategoryDto());

        Type listType = new TypeToken<List<CategoryDto>>() {}.getType();

        Mockito.when(categoryRepository.findAll(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(page.getContent(), listType))
                .thenReturn(dtoList);

        PageDto<CategoryDto> result =
                categoryService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());

        Mockito.verify(categoryRepository)
                .findAll(pageable);
    }

    @Test
    void findByIdentifierTest() {
        String identifier = "CAT001";

        Category category = new Category();
        CategoryDto categoryDto = new CategoryDto();

        Mockito.when(categoryRepository.findByIdentifier(identifier))
                .thenReturn(category);

        Mockito.when(modelMapper.map(category, CategoryDto.class))
                .thenReturn(categoryDto);

        CategoryDto response =
                categoryService.findByIdentifier(identifier);

        assertNotNull(response);

        Mockito.verify(categoryRepository)
                .findByIdentifier(identifier);
    }

    @Test
    void findBySubCategoryTest() {
        Category category1 = new Category();
        Category category2 = new Category();

        List<Category> categories =
                Arrays.asList(category1, category2);

        Mockito.when(categoryRepository.findBySupercategoryIsNot(""))
                .thenReturn(categories);

        Mockito.when(modelMapper.map(Mockito.any(Category.class),
                        Mockito.eq(CategoryDto.class)))
                .thenReturn(new CategoryDto());

        List<CategoryDto> result =
                categoryService.findBySubCategory();

        assertNotNull(result);
        assertEquals(2, result.size());

        Mockito.verify(categoryRepository)
                .findBySupercategoryIsNot("");
    }

    @Test
    void toggleStatusTest_WhenCategoryExists() {
        String identifier = "CAT001";

        Category category = new Category();
        category.setStatus(true);

        Mockito.when(categoryRepository.findByIdentifier(identifier))
                .thenReturn(category);

        categoryService.toggleStatus(identifier);

        assertFalse(category.getStatus());
        Mockito.verify(categoryRepository)
                .save(category);
    }

    @Test
    void toggleStatusTest_WhenCategoryNotFound() {
        String identifier = "CAT001";

        Mockito.when(categoryRepository.findByIdentifier(identifier))
                .thenReturn(null);

        categoryService.toggleStatus(identifier);

        Mockito.verify(categoryRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findActiveCategoriesTest() {
        List<Category> categories =
                Arrays.asList(new Category());

        List<RoleDto> mappedList =
                Arrays.asList(new RoleDto());

        Type listType = new TypeToken<List<RoleDto>>() {}.getType();

        Mockito.when(categoryRepository.findByStatusTrue())
                .thenReturn(categories);

        Mockito.when(modelMapper.map(categories, listType))
                .thenReturn(mappedList);

        List<?> result =
                categoryService.findActiveCategories();

        assertNotNull(result);
        assertEquals(1, result.size());

        Mockito.verify(categoryRepository)
                .findByStatusTrue();
    }
}