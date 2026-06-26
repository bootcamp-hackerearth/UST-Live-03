package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Spy
    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setIdentifier("CAT1");
        category.setStatus(true);
        category.setDeleted(false);
        category.setSuperCategory("MAIN");

        categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT1");
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> page = new PageImpl<>(Collections.singletonList(category));

        when(categoryRepository.findByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(categoryDto));

        WsDto<CategoryDto> result = categoryService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void testSave_NewCategory() {
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);
        when(modelMapper.map(categoryDto, Category.class)).thenReturn(category);

        doNothing().when(categoryService).setAuditFields(category, true);

        CategoryDto result = categoryService.save(categoryDto);

        assertNotNull(result);
        verify(categoryRepository).save(category);
    }

    @Test
    void testSave_AlreadyExists() {
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);

        CategoryDto result = categoryService.save(categoryDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_SoftDeleted() {
        category.setDeleted(true);

        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);

        CategoryDto result = categoryService.save(categoryDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("soft deleted"));
    }

    @Test
    void testDelete() {
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);

        doNothing().when(categoryService).softDelete(category);
        doNothing().when(categoryService).setAuditFields(category, false);

        categoryService.delete("CAT1");

        verify(categoryRepository).save(category);
    }

    @Test
    void testFindByIdentifier() {
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        CategoryDto result = categoryService.findByIdentifier("CAT1");

        assertNotNull(result);
    }

    @Test
    void testUpdate_Success() {
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);

        doNothing().when(modelMapper).map(categoryDto, category);
        doNothing().when(categoryService).setAuditFields(category, false);

        CategoryDto result = categoryService.update(categoryDto);

        assertNotNull(result);
        verify(categoryRepository).save(category);
    }

    @Test
    void testUpdate_NotFound() {
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);

        CategoryDto result = categoryService.update(categoryDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testFindSubCategories() {
        when(categoryRepository.findBySuperCategoryIsNot(" "))
                .thenReturn(Collections.singletonList(category));
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        List<CategoryDto> result = categoryService.findSubCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testChangeToggleStatus() {
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        CategoryDto result = categoryService.changeToggleStatus("CAT1", false);

        assertNotNull(result);
        assertFalse(category.isStatus());
        verify(categoryRepository).save(category);
    }

    @Test
    void testFindActiveStatus() {
        category.setStatus(true);
        Category inactive = new Category();
        inactive.setStatus(false);

        List<Category> categories = List.of(category, inactive);

        when(categoryRepository.findAll()).thenReturn(categories);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(categoryDto));

        List<CategoryDto> result = categoryService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
