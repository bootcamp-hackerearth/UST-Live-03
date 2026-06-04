package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.modell.Category;
import com.ust.pos.modell.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl service;

    @Mock
    private CategoryRepository repository;

    @Mock
    private ModelMapper mapper;

    private static final String CAT1 = "CAT1";

    @Test
    void save_shouldHandleAllCases() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier(CAT1);
        when(repository.findByIdentifier(CAT1)).thenReturn(null);
        dto.setSuperCategory(null);
        CategoryDto result1 = service.save(dto);
        assertTrue(result1.isSuccess() || result1.getMessage() == null);
        verify(repository).save(any());
        dto.setSuperCategory(" ");
        CategoryDto result2 = service.save(dto);
        assertTrue(result2.isSuccess() || result2.getMessage() == null);
        dto.setSuperCategory("PARENT");
        CategoryDto result3 = service.save(dto);
        assertTrue(result3.isSuccess() || result3.getMessage() == null);
        when(repository.findByIdentifier(CAT1)).thenReturn(new Category());
        CategoryDto duplicate = service.save(dto);
        assertFalse(duplicate.isSuccess());
        assertEquals("Category already exists", duplicate.getMessage());
    }
    @Test
    void delete_shouldHandleUsedAndUnusedCategory() {
        when(repository.existsBySuperCategory(CAT1)).thenReturn(true);
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.deleteByIdentifier(CAT1)
        );
        assertEquals(
                "Cannot delete category. It is used as a super category.",
                ex.getMessage()
        );
        when(repository.existsBySuperCategory(CAT1)).thenReturn(false);
        service.deleteByIdentifier(CAT1);
        verify(repository).deleteByIdentifier(CAT1);
    }

    @Test
    void findByIdentifier_shouldHandleBothCases() {
        Category category = new Category();
        category.setIdentifier(CAT1);
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier(CAT1);
        when(repository.findByIdentifier(CAT1)).thenReturn(category);
        when(mapper.map(category, CategoryDto.class)).thenReturn(dto);
        assertEquals(CAT1,
                service.findByIdentifier(CAT1).getIdentifier());
        when(repository.findByIdentifier("X")).thenReturn(null);
        when(mapper.map(null, CategoryDto.class)).thenReturn(null);
        assertNull(service.findByIdentifier("X"));
    }

    @Test
    void findAll_shouldHandleDataAndEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        Type type = new TypeToken<List<CategoryDto>>() {}.getType();
        Category category = new Category();
        category.setIdentifier(CAT1);
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier(CAT1);
        Page<Category> page =
                new PageImpl<>(List.of(category), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.map(any(), eq(type))).thenReturn(List.of(dto));
        assertEquals(1, service.findAll(pageable).size());
        Page<Category> emptyPage =
                new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(emptyPage);
        when(mapper.map(List.of(), type)).thenReturn(List.of());
        assertTrue(service.findAll(pageable).isEmpty());
    }
    @Test
    void update_shouldCoverAllBranches() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setIdentifier("CAT1");
        when(repository.findById(1L)).thenReturn(Optional.empty());
        CategoryDto notFoundDto = new CategoryDto();
        notFoundDto.setId(1L);
        CategoryDto notFound = service.update(notFoundDto);
        assertFalse(notFound.isSuccess());
        assertEquals("Category not found", notFound.getMessage());
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.findByIdentifier("NEW")).thenReturn(new Category());
        CategoryDto duplicateDto = new CategoryDto();
        duplicateDto.setId(1L);
        duplicateDto.setIdentifier("NEW");
        CategoryDto duplicate = service.update(duplicateDto);
        assertFalse(duplicate.isSuccess());
        assertEquals("Category already exists", duplicate.getMessage());
        when(repository.findByIdentifier("NEW")).thenReturn(null);
        CategoryDto successDto = new CategoryDto();
        successDto.setId(1L);
        successDto.setIdentifier("NEW");
        successDto.setSuperCategory(null); // null branch
        CategoryDto success1 = service.update(successDto);
        successDto.setSuperCategory(" "); // empty branch
        CategoryDto success2 = service.update(successDto);
        successDto.setSuperCategory("PARENT"); // value branch
        CategoryDto success3 = service.update(successDto);
        assertTrue(success1.isSuccess() || success1.getMessage() == null);
        assertTrue(success2.isSuccess() || success2.getMessage() == null);
        assertTrue(success3.isSuccess() || success3.getMessage() == null);
        verify(repository, atLeastOnce()).save(any());
    }

    @Test
    void findChildCategories_shouldHandleDataAndEmpty() {
        Type type = new TypeToken<List<CategoryDto>>() {}.getType();
        Category child = new Category();
        child.setIdentifier("CHILD");
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CHILD");
        when(repository.findBySuperCategoryIsNotNull())
                .thenReturn(List.of(child));
        when(mapper.map(any(), eq(type))).thenReturn(List.of(dto));
        assertEquals(1, service.findChildCategories().size());
        when(repository.findBySuperCategoryIsNotNull())
                .thenReturn(List.of());
        when(mapper.map(List.of(), type)).thenReturn(List.of());
        assertTrue(service.findChildCategories().isEmpty());
    }
}
