package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private CategoryDto categoryDto;
    private Category category;

    @BeforeEach
    void setUp() {
        categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT1");
        categoryDto.setSuperCategory("SUP1");

        category = new Category();
        category.setIdentifier("CAT1");
        category.setSuperCategory("SUP1");
        category.setDeleted(false);
        category.setStatus(true);
    }

    @Test
    @DisplayName("Save Category - Success")
    void save_Success() {
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);
        when(modelMapper.map(categoryDto, Category.class)).thenReturn(category);

        CategoryDto result = categoryService.save(categoryDto);

        Assertions.assertTrue(result.isSuccess());
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("Save Category - Success with Blank SuperCategory")
    void save_Success_BlankSuperCategory() {
        categoryDto.setSuperCategory("");
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);
        when(modelMapper.map(categoryDto, Category.class)).thenReturn(category);

        CategoryDto result = categoryService.save(categoryDto);

        Assertions.assertTrue(result.isSuccess());
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("Save Category - Failure: Already Exists")
    void save_Failure_AlreadyExists() {
        category.setDeleted(false);
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);

        CategoryDto result = categoryService.save(categoryDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    @DisplayName("Save Category - Failure: Previously Deleted")
    void save_Failure_PreviouslyDeleted() {
        category.setDeleted(true);
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);

        CategoryDto result = categoryService.save(categoryDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("was previously deleted"));
    }

    @Test
    @DisplayName("Save Category - Failure: Own SuperCategory")
    void save_Failure_OwnSuperCategory() {
        categoryDto.setSuperCategory("CAT1");
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);

        CategoryDto result = categoryService.save(categoryDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Category cannot be its own super category", result.getMessage());
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifier_Success() {
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        CategoryDto result = categoryService.findByIdentifier("CAT1");

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Find All - Paginated Success")
    void findAll_PaginatedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> page = new PageImpl<>(List.of(category));
        when(categoryRepository.findByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(categoryDto));

        WsDto<CategoryDto> result = categoryService.findAll(pageable);

        Assertions.assertEquals(1, result.getTotalRecords());
    }

    @Test
    @DisplayName("Find All Active - Success")
    void findAllActive_Success() {
        List<Category> categories = List.of(category);

        when(categoryRepository.findAllByStatusAndDeletedFalse(true))
                .thenReturn(categories);

        when(modelMapper.map(eq(categories), any(Type.class)))
                .thenReturn(List.of(categoryDto));

        List<CategoryDto> result = categoryService.findAllActive();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Update Category - Success")
    void update_Success() {
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);

        CategoryDto result = categoryService.update(categoryDto);

        Assertions.assertTrue(result.isSuccess());
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("Update Category - Failure: Not Found")
    void update_Failure_NotFound() {
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);

        CategoryDto result = categoryService.update(categoryDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Toggle Status - Success")
    void toggleStatus_Success() {
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        categoryService.toggleStatus("CAT1");

        Assertions.assertFalse(category.isStatus());
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("Delete Category - Success")
    void delete_Success() {
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);

        boolean result = categoryService.delete("CAT1");

        Assertions.assertTrue(result);
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("Delete Category - Failure: Not Found")
    void delete_Failure_NotFound() {
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);

        boolean result = categoryService.delete("CAT1");

        Assertions.assertFalse(result);
    }
}