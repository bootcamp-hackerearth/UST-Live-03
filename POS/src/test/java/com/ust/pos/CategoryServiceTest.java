package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    void save_success() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");
        dto.setName("Electronics");

        when(categoryRepository.existsByIdentifier("CAT1"))
                .thenReturn(false);

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(new Category());

        CategoryDto response = categoryService.save(dto);

        Assertions.assertTrue(response.isSuccess());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void save_duplicate() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        when(categoryRepository.existsByIdentifier("CAT1"))
                .thenReturn(true);

        CategoryDto response = categoryService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Identifier already exists", response.getMessage());

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void find_success() {

        Category category = new Category();
        CategoryDto mapped = new CategoryDto();
        mapped.setIdentifier("CAT1");

        when(categoryRepository.findByIdentifier("CAT1"))
                .thenReturn(Optional.of(category));

        when(modelMapper.map(category, CategoryDto.class))
                .thenReturn(mapped);

        CategoryDto response = categoryService.findByIdentifier("CAT1");

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("CAT1", response.getIdentifier());
    }

    @Test
    void find_not_found() {

        when(categoryRepository.findByIdentifier("CAT1"))
                .thenReturn(Optional.empty());

        CategoryDto response = categoryService.findByIdentifier("CAT1");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Category not found", response.getMessage());
    }

    @Test
    void update_success() {

        Category category = new Category();

        when(categoryRepository.findByIdentifier("CAT1"))
                .thenReturn(Optional.of(category));

        when(categoryRepository.save(category))
                .thenReturn(category);

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        CategoryDto response = categoryService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        verify(categoryRepository).save(category);
    }

    @Test
    void update_not_found() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        when(categoryRepository.findByIdentifier("CAT1"))
                .thenReturn(Optional.empty());

        CategoryDto response = categoryService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Category not found", response.getMessage());
    }

    @Test
    void delete_found() {

        Category category = new Category();

        when(categoryRepository.findByIdentifier("CAT1"))
                .thenReturn(Optional.of(category));

        categoryService.delete("CAT1");

        verify(categoryRepository).delete(category);
    }

    @Test
    void delete_not_found() {

        when(categoryRepository.findByIdentifier("CAT1"))
                .thenReturn(Optional.empty());

        categoryService.delete("CAT1");

        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void find_all() {

        List<Category> list = List.of(new Category());
        Page<Category> page = new PageImpl<>(list);

        List<CategoryDto> mappedList = List.of(new CategoryDto());

        when(categoryRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        when(modelMapper.map(eq(list), ArgumentMatchers.<Type>any()))
                .thenReturn(mappedList);

        WsDto<CategoryDto> result =
                categoryService.findAll(PageRequest.of(0, 5));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
    }

    @Test
    void super_categories() {

        Category parent = new Category();
        parent.setIdentifier("A");
        parent.setSuperCategoryIdentifier(null);

        Category child = new Category();
        child.setIdentifier("B");
        child.setSuperCategoryIdentifier("A");

        when(categoryRepository.findAll())
                .thenReturn(List.of(parent, child));

        when(modelMapper.map(parent, CategoryDto.class))
                .thenReturn(new CategoryDto());

        List<CategoryDto> result =
                categoryService.findSuperCategories();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void leaf_categories() {

        Category parent = new Category();
        parent.setIdentifier("A");

        Category child = new Category();
        child.setIdentifier("B");
        child.setSuperCategoryIdentifier("A");

        when(categoryRepository.findAll())
                .thenReturn(List.of(parent, child));

        when(modelMapper.map(child, CategoryDto.class))
                .thenReturn(new CategoryDto());

        List<CategoryDto> result =
                categoryService.findLeafCategories();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void child_categories() {

        Category child = new Category();
        child.setIdentifier("B");
        child.setSuperCategoryIdentifier("A");

        when(categoryRepository.findAll())
                .thenReturn(List.of(child));

        when(modelMapper.map(child, CategoryDto.class))
                .thenReturn(new CategoryDto());

        List<CategoryDto> result =
                categoryService.findChildCategories();

        Assertions.assertEquals(1, result.size());
    }
}