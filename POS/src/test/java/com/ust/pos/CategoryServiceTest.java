package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl service;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 2);

        Page<Category> page = new PageImpl<>(List.of(new Category()), pageable, 1);

        when(categoryRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new CategoryDto()));

        WsDto<CategoryDto> result = service.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void findByIdentifierTest() {
        Category category = new Category();
        CategoryDto dto = new CategoryDto();

        when(categoryRepository.findByIdentifier("C1")).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(dto);

        CategoryDto result = service.findByIdentifier("C1");

        assertNotNull(result);
    }

    @Test
    void saveSuccessTest() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("C1");

        when(categoryRepository.findByIdentifier("C1")).thenReturn(null);
        when(modelMapper.map(dto, Category.class)).thenReturn(new Category());

        CategoryDto result = service.save(dto);

        assertTrue(result.isSuccess());
        verify(categoryRepository).save(any());
    }

    @Test
    void saveDuplicateActiveTest() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("C1");

        Category category = new Category();
        category.setDeleted(false);

        when(categoryRepository.findByIdentifier("C1")).thenReturn(category);

        CategoryDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void saveDuplicateDeletedTest() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("C1");

        Category category = new Category();
        category.setDeleted(true);

        when(categoryRepository.findByIdentifier("C1")).thenReturn(category);

        CategoryDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("deleted"));
    }

    @Test
    void updateSuccessTest() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("C1");

        Category existing = new Category();

        when(categoryRepository.findByIdentifier("C1")).thenReturn(existing);

        CategoryDto result = service.update(dto);

        assertTrue(result.isSuccess());
        verify(categoryRepository).save(existing);
    }

    @Test
    void updateNotFoundTest() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("C1");

        when(categoryRepository.findByIdentifier("C1")).thenReturn(null);

        CategoryDto result = service.update(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Category category = new Category();
        category.setDeleted(false);

        when(categoryRepository.findByIdentifier("C1")).thenReturn(category);

        service.delete("C1");

        assertTrue(category.isDeleted());
    }

    @Test
    void findAllCategoriesWithNoSuperTest() {
        Category c1 = new Category();
        c1.setSuperCategory(List.of("A"));

        Category c2 = new Category();
        c2.setSuperCategory(null);

        when(categoryRepository.findAll()).thenReturn(List.of(c1, c2));
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new CategoryDto()));

        List<CategoryDto> result = service.findAllCategoriesWithNoSuper();

        assertEquals(1, result.size());
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Category category = new Category();
        category.setStatus(true);

        when(categoryRepository.findByIdentifier("C1")).thenReturn(category);

        service.toggleStatus("C1");

        assertFalse(category.isStatus());
        verify(categoryRepository).save(category);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Category category = new Category();
        category.setStatus(false);

        when(categoryRepository.findByIdentifier("C1")).thenReturn(category);

        service.toggleStatus("C1");

        assertTrue(category.isStatus());
        verify(categoryRepository).save(category);
    }

    @Test
    void toggleStatusNotFoundTest() {
        when(categoryRepository.findByIdentifier("C1")).thenReturn(null);

        service.toggleStatus("C1");

        verify(categoryRepository, never()).save(any());
    }
}
