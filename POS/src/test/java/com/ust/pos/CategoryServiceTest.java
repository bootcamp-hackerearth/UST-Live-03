package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

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
        categoryDto.setIdentifier("CAT-001");
        categoryDto.setSuperCategory("SUPER-CAT");

        category = new Category();
        category.setIdentifier("CAT-001");
        category.setStatus(true);
        category.setDeleted(false);
    }

    @Test
    @DisplayName("Save Category - Success")
    void save_Success() {
        when(categoryRepository.findByIdentifier("CAT-001")).thenReturn(null);
        when(modelMapper.map(categoryDto, Category.class)).thenReturn(category);

        CategoryDto result = categoryService.save(categoryDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("Added Successfully"));
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("Save Category - Success with Blank Super Category")
    void save_Success_BlankSuperCategory() {
        categoryDto.setSuperCategory(" ");
        when(categoryRepository.findByIdentifier("CAT-001")).thenReturn(null);
        when(modelMapper.map(categoryDto, Category.class)).thenReturn(category);

        CategoryDto result = categoryService.save(categoryDto);

        Assertions.assertTrue(result.isSuccess());
        verify(categoryRepository).save(category);
        Assertions.assertNull(category.getSuperCategory());
    }

    @Test
    @DisplayName("Save Category - Failure: Already Exists")
    void save_Failure_AlreadyExists() {
        category.setDeleted(false);
        when(categoryRepository.findByIdentifier("CAT-001")).thenReturn(category);

        CategoryDto result = categoryService.save(categoryDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Save Category - Failure: Previously Soft-Deleted")
    void save_Failure_PreviouslyDeleted() {
        category.setDeleted(true);
        when(categoryRepository.findByIdentifier("CAT-001")).thenReturn(category);

        CategoryDto result = categoryService.save(categoryDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("was previously deleted"));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Save Category - Failure: Own Super Category")
    void save_Failure_OwnSuperCategory() {
        categoryDto.setSuperCategory("CAT-001");
        when(categoryRepository.findByIdentifier("CAT-001")).thenReturn(null);

        CategoryDto result = categoryService.save(categoryDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("cannot be its own super category"));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifier_Success() {
        when(categoryRepository.findByIdentifier("CAT-001")).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        CategoryDto result = categoryService.findByIdentifier("CAT-001");

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Find By Identifier - Failure: Not Found Exception")
    void findByIdentifier_Failure_NotFound() {
        when(categoryRepository.findByIdentifier("CAT-001")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> categoryService.findByIdentifier("CAT-001"));
    }

    @Test
    @DisplayName("Find All Categories - Paginated Success")
    void findAll_PaginatedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = new PageImpl<>(List.of(category), pageable, 1);

        when(categoryRepository.findByDeletedFalse(pageable)).thenReturn(categoryPage);
        when(modelMapper.map(eq(categoryPage.getContent()), any(Type.class))).thenReturn(List.of(categoryDto));

        WsDto<CategoryDto> result = categoryService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    @DisplayName("Find All Categories with Specification - Success")
    void findAll_WithSpecification_Success() {
        Specification<Category> spec = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = new PageImpl<>(List.of(category), pageable, 1);

        when(categoryRepository.findAll(spec, pageable)).thenReturn(categoryPage);
        when(modelMapper.map(eq(categoryPage.getContent()), any(Type.class))).thenReturn(List.of(categoryDto));

        WsDto<CategoryDto> result = categoryService.findAll(spec, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    @DisplayName("Find All Active Categories - Success")
    void findAllActive_Success() {
        List<Category> activeCategories = List.of(category);
        when(categoryRepository.findAllByStatusAndDeletedFalse(true)).thenReturn(activeCategories);
        when(modelMapper.map(eq(activeCategories), any(Type.class))).thenReturn(List.of(categoryDto));

        List<CategoryDto> result = categoryService.findAllActive();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Update Category - Success")
    void update_Success() {
        when(categoryRepository.findByIdentifier("CAT-001")).thenReturn(category);

        CategoryDto result = categoryService.update(categoryDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("Updated"));
        verify(categoryRepository).save(category);
        verify(modelMapper).map(categoryDto, category);
    }

    @Test
    @DisplayName("Update Category - Failure: Not Found")
    void update_Failure_NotFound() {
        when(categoryRepository.findByIdentifier("CAT-001")).thenReturn(null);

        CategoryDto result = categoryService.update(categoryDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Toggle Status - Success")
    void toggleStatus_Success() {
        when(categoryRepository.findByIdentifier("CAT-001")).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        CategoryDto result = categoryService.toggleStatus("CAT-001");

        Assertions.assertFalse(category.isStatus());
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("Delete Category - Success")
    void delete_Success() {
        when(categoryRepository.findByIdentifier("CAT-001")).thenReturn(category);

        boolean result = categoryService.delete("CAT-001");

        Assertions.assertTrue(result);
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("Delete Category - Failure: Not Found")
    void delete_Failure_NotFound() {
        when(categoryRepository.findByIdentifier("CAT-001")).thenReturn(null);

        boolean result = categoryService.delete("CAT-001");

        Assertions.assertFalse(result);
        verify(categoryRepository, never()).save(any(Category.class));
    }
}