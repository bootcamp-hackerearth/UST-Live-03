package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Category;
import com.ust.pos.modell.CategoryRepository;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl service;

    @Mock
    private CategoryRepository repository;

    @Mock
    private ModelMapper mapper;

    @Test
    void saveSuccessWithNullSuperCategory() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        when(repository.findByIdentifier("CAT1"))
                .thenReturn(null);

        service.save(dto);

        verify(repository).save(any(Category.class));
    }

    @Test
    void saveSuccessWithSuperCategory() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT2");
        dto.setSuperCategory("PARENT");

        when(repository.findByIdentifier("CAT2"))
                .thenReturn(null);

        service.save(dto);

        verify(repository).save(any(Category.class));
    }

    @Test
    void saveDuplicateCategory() {

        Category category = new Category();
        category.setDeleted(false);

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        when(repository.findByIdentifier("CAT1"))
                .thenReturn(category);

        CategoryDto result = service.save(dto);

        assertFalse(result.isSuccess());
    }

    @Test
    void saveSoftDeletedCategory() {

        Category category = new Category();
        category.setDeleted(true);

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT1");

        when(repository.findByIdentifier("CAT1"))
                .thenReturn(category);

        CategoryDto result = service.save(dto);

        assertFalse(result.isSuccess());
    }

    @Test
    void updateNotFound() {

        CategoryDto dto = new CategoryDto();
        dto.setId(1L);

        when(repository.findById(1L))
                .thenReturn(Optional.empty());

        CategoryDto result = service.update(dto);

        assertFalse(result.isSuccess());
    }

    @Test
    void updateDuplicateIdentifier() {

        Category existing = new Category();
        existing.setId(1L);
        existing.setIdentifier("OLD");

        CategoryDto dto = new CategoryDto();
        dto.setId(1L);
        dto.setIdentifier("NEW");

        when(repository.findById(1L))
                .thenReturn(Optional.of(existing));

        when(repository.findByIdentifierAndDeletedFalse("NEW"))
                .thenReturn(new Category());

        CategoryDto result = service.update(dto);

        assertFalse(result.isSuccess());
    }

    @Test
    void updateSuccessBranches() {

        Category existing = new Category();
        existing.setId(1L);
        existing.setIdentifier("OLD");
        existing.setCreatedBy("user");
        existing.setCreatedOn(LocalDateTime.now());

        CategoryDto dto = new CategoryDto();
        dto.setId(1L);
        dto.setIdentifier("OLD");
        dto.setSuperCategory("PARENT");

        when(repository.findById(1L))
                .thenReturn(Optional.of(existing));

        service.update(dto);

        verify(repository).save(existing);
    }

    @Test
    void deleteBranches() {

        when(repository.existsBySuperCategory("CAT1"))
                .thenReturn(true);

        assertThrows(
                IllegalStateException.class,
                () -> service.delete("CAT1")
        );

        Category category = new Category();

        when(repository.existsBySuperCategory("CAT2"))
                .thenReturn(false);

        when(repository.findByIdentifierAndDeletedFalse("CAT2"))
                .thenReturn(category);

        service.delete("CAT2");

        verify(repository).save(category);

        when(repository.existsBySuperCategory("CAT3"))
                .thenReturn(false);

        when(repository.findByIdentifierAndDeletedFalse("CAT3"))
                .thenReturn(null);

        service.delete("CAT3");
    }

    @Test
    void findByIdentifierTest() {

        Category category = new Category();
        CategoryDto dto = new CategoryDto();

        when(repository.findByIdentifierAndDeletedFalse("CAT1"))
                .thenReturn(category);

        when(mapper.map(category, CategoryDto.class))
                .thenReturn(dto);

        assertNotNull(service.findByIdentifier("CAT1"));
    }

    @Test
    void findAllAndEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Category> page =
                new PageImpl<>(List.of(new Category()), pageable, 1);

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new CategoryDto()));

        WsDto<CategoryDto> result = service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());

        Page<Category> empty =
                new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(empty);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(Collections.emptyList());

        assertTrue(service.findAll(pageable).getDtoList().isEmpty());
    }

    @Test
    void childAndActiveCategoriesTest() {

        Category category = new Category();
        CategoryDto dto = new CategoryDto();

        when(repository.findBySuperCategoryIsNotNull())
                .thenReturn(List.of(category));

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(dto));

        assertEquals(
                1,
                service.findChildCategories().size()
        );

        when(repository.findByStatusTrue())
                .thenReturn(List.of(category));

        when(mapper.map(category, CategoryDto.class))
                .thenReturn(dto);

        assertEquals(
                1,
                service.findAllActive().size()
        );

        when(repository.findByStatusTrue())
                .thenReturn(Collections.emptyList());

        assertTrue(
                service.findAllActive().isEmpty()
        );
    }

    @Test
    void toggleStatusBranches() {

        Category category = new Category();
        category.setStatus(true);

        CategoryDto dto = new CategoryDto();

        when(repository.findByIdentifierAndDeletedFalse("CAT1"))
                .thenReturn(category);

        when(repository.save(category))
                .thenReturn(category);

        when(mapper.map(category, CategoryDto.class))
                .thenReturn(dto);

        service.toggleStatus("CAT1");

        assertFalse(category.getStatus());

        Category nullStatusCategory = new Category();
        nullStatusCategory.setStatus(null);

        when(repository.findByIdentifierAndDeletedFalse("CAT2"))
                .thenReturn(nullStatusCategory);

        when(repository.save(nullStatusCategory))
                .thenReturn(nullStatusCategory);

        when(mapper.map(nullStatusCategory, CategoryDto.class))
                .thenReturn(dto);

        service.toggleStatus("CAT2");

        assertTrue(nullStatusCategory.getStatus());

        when(repository.findByIdentifierAndDeletedFalse("CAT3"))
                .thenReturn(null);

        assertThrows(
                RuntimeException.class,
                () -> service.toggleStatus("CAT3")
        );
    }
}