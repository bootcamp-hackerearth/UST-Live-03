package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
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

    @Test
    void saveTest() {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        when(repository.findByIdentifier("CAT1")).thenReturn(null);

        dto.setSuperCategory(null);
        CategoryDto r1 = service.save(dto);
        assertTrue(r1.getMessage() == null || r1.isSuccess());

        dto.setSuperCategory(" ");
        CategoryDto r2 = service.save(dto);
        assertTrue(r2.getMessage() == null || r2.isSuccess());

        dto.setSuperCategory("PARENT");
        CategoryDto r3 = service.save(dto);
        assertTrue(r3.getMessage() == null || r3.isSuccess());

        when(repository.findByIdentifier("CAT1")).thenReturn(new Category());
        CategoryDto duplicate = service.save(dto);
        assertFalse(duplicate.isSuccess());
        assertEquals("Category already exists", duplicate.getMessage());
    }

    @Test
    void updateTest() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setIdentifier("CAT1");

        CategoryDto dto = new CategoryDto();
        dto.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.empty());

        CategoryDto notFound = service.update(dto);
        assertFalse(notFound.isSuccess());
        assertEquals("Category not found", notFound.getMessage());

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.findByIdentifier("NEW")).thenReturn(new Category());

        dto.setIdentifier("NEW");

        CategoryDto duplicate = service.update(dto);
        assertFalse(duplicate.isSuccess());
        assertEquals("Category already exists", duplicate.getMessage());

        // ✅ reset duplicate condition
        when(repository.findByIdentifier("NEW")).thenReturn(null);

        // ✅ use NEW dto (important)
        CategoryDto successDto = new CategoryDto();
        successDto.setId(1L);
        successDto.setIdentifier("NEW");

        successDto.setSuperCategory(null);
        CategoryDto s1 = service.update(successDto);

        successDto.setSuperCategory(" ");
        CategoryDto s2 = service.update(successDto);

        successDto.setSuperCategory("PARENT");
        CategoryDto s3 = service.update(successDto);

        assertTrue(s1.getMessage() == null || s1.isSuccess());
        assertTrue(s2.getMessage() == null || s2.isSuccess());
        assertTrue(s3.getMessage() == null || s3.isSuccess());

        verify(repository, atLeastOnce()).save(any());
    }

    @Test
    void deleteTest() {
        when(repository.existsBySuperCategory("CAT1")).thenReturn(true);
        Exception ex = assertThrows(IllegalStateException.class, () -> service.delete("CAT1"));
        assertEquals("Cannot delete category. It is used as a super category.", ex.getMessage());

        when(repository.existsBySuperCategory("CAT1")).thenReturn(false);
        service.delete("CAT1");
        verify(repository).deleteByIdentifier("CAT1");
    }

    @Test
    void findByIdentifierTest() {
        Category category = new Category();
        category.setIdentifier("CAT1");
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        when(repository.findByIdentifier("CAT1")).thenReturn(category);
        when(mapper.map(category, CategoryDto.class)).thenReturn(dto);

        CategoryDto result = service.findByIdentifier("CAT1");
        assertEquals("CAT1", result.getIdentifier());

        when(repository.findByIdentifier("X")).thenReturn(null);
        when(mapper.map(null, CategoryDto.class)).thenReturn(null);

        assertNull(service.findByIdentifier("X"));
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Type type = new TypeToken<List<CategoryDto>>() {}.getType();

        Category category = new Category();
        category.setIdentifier("CAT1");
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        Page<Category> page = new PageImpl<>(List.of(category), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.map(page.getContent(), type)).thenReturn(List.of(dto));

        WsDto<CategoryDto> result = service.findAll(pageable);
        assertEquals(1, result.getDtoList().size());

        Page<Category> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(emptyPage);
        when(mapper.map(emptyPage.getContent(), type)).thenReturn(List.of());

        WsDto<CategoryDto> empty = service.findAll(pageable);
        assertTrue(empty.getDtoList().isEmpty());
    }

    @Test
    void findChildCategoriesTest() {
        Type type = new TypeToken<List<CategoryDto>>() {}.getType();

        Category child = new Category();
        child.setIdentifier("CHILD");
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CHILD");

        when(repository.findBySuperCategoryIsNotNull()).thenReturn(List.of(child));
        when(mapper.map(any(), eq(type))).thenReturn(List.of(dto));

        List<CategoryDto> result = service.findChildCategories();
        assertEquals(1, result.size());

        when(repository.findBySuperCategoryIsNotNull()).thenReturn(List.of());
        when(mapper.map(List.of(), type)).thenReturn(List.of());

        List<CategoryDto> empty = service.findChildCategories();
        assertTrue(empty.isEmpty());
    }
}