package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    public static final String PARENT = "PARENT";
    public static final String ADMIN = "admin";
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

        when(repository.findByIdentifier("CAT1"))
                .thenReturn(null);

        service.save(dto);

        verify(repository).save(any(Category.class));

        CategoryDto dto2 = new CategoryDto();
        dto2.setIdentifier("CAT2");
        dto2.setSuperCategory(PARENT);

        when(repository.findByIdentifier("CAT2"))
                .thenReturn(null);

        service.save(dto2);

        Category duplicate = new Category();
        duplicate.setDeleted(false);

        when(repository.findByIdentifier("CAT1"))
                .thenReturn(duplicate);

        CategoryDto result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Category with identifier - CAT1 already exists",
                result.getMessage()
        );

        duplicate.setDeleted(true);

        when(repository.findByIdentifier("CAT1"))
                .thenReturn(duplicate);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Category with Identifier CAT1 already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void updateAndDeleteTest() {

        CategoryDto notFound = new CategoryDto();
        notFound.setId(1L);

        when(repository.findById(1L))
                .thenReturn(Optional.empty());

        CategoryDto result = service.update(notFound);

        assertFalse(result.isSuccess());

        Category existing = new Category();
        existing.setId(2L);
        existing.setIdentifier("OLD");

        CategoryDto duplicateDto = new CategoryDto();
        duplicateDto.setId(2L);
        duplicateDto.setIdentifier("NEW");

        when(repository.findById(2L))
                .thenReturn(Optional.of(existing));

        when(repository.findByIdentifierAndDeletedFalse("NEW"))
                .thenReturn(new Category());

        result = service.update(duplicateDto);

        assertFalse(result.isSuccess());

        Category updated = new Category();
        updated.setId(3L);
        updated.setIdentifier("CAT3");
        updated.setCreatedBy(ADMIN);
        updated.setCreatedOn(LocalDateTime.now());

        CategoryDto updateDto = new CategoryDto();
        updateDto.setId(3L);
        updateDto.setIdentifier("CAT3");
        updateDto.setSuperCategory(PARENT);

        when(repository.findById(3L))
                .thenReturn(Optional.of(updated));

        service.update(updateDto);

        verify(repository).save(updated);

        when(repository.existsBySuperCategory("CAT1"))
                .thenReturn(true);

        assertThrows(
                IllegalStateException.class,
                () -> service.delete("CAT1")
        );

        when(repository.existsBySuperCategory("CAT2"))
                .thenReturn(false);

        when(repository.findByIdentifierAndDeletedFalse("CAT2"))
                .thenReturn(updated);

        service.delete("CAT2");

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

        when(repository.findByIdentifierAndDeletedFalse("INVALID"))
                .thenReturn(null);

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.findByIdentifier("INVALID")
                );

        assertEquals(
                "Category with identifier 'INVALID' not found",
                exception.getMessage()
        );
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Category> page =
                new PageImpl<>(
                        List.of(new Category()),
                        pageable,
                        1
                );

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(repository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new CategoryDto()));

        WsDto<CategoryDto> result =
                service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());

        Specification<Category> specification =
                (root, query, cb) -> cb.conjunction();

        WsDto<CategoryDto> specResult =
                service.findAll(specification, pageable);

        assertEquals(1, specResult.getDtoList().size());
        assertEquals(1, specResult.getTotalRecords());

        verify(repository)
                .findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void saveEmptySuperCategoryCoverage() {

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT_EMPTY");
        dto.setSuperCategory("   ");

        when(repository.findByIdentifier("CAT_EMPTY"))
                .thenReturn(null);

        service.save(dto);

        verify(repository).save(any(Category.class));
    }

    @Test
    void toggleStatusFalseToTrueTest() {

        Category category = new Category();
        category.setStatus(false);

        CategoryDto dto = new CategoryDto();

        when(repository.findByIdentifierAndDeletedFalse("CAT4"))
                .thenReturn(category);

        when(repository.save(category))
                .thenReturn(category);

        when(mapper.map(category, CategoryDto.class))
                .thenReturn(dto);

        CategoryDto result = service.toggleStatus("CAT4");

        assertNotNull(result);
        assertTrue(category.getStatus());

        verify(repository).save(category);
    }

    @Test
    void updateIdentifierChangedButNotDuplicateCoverage() {

        Category existing = new Category();
        existing.setId(10L);
        existing.setIdentifier("OLD");
        existing.setCreatedBy(ADMIN);
        existing.setCreatedOn(LocalDateTime.now());

        CategoryDto dto = new CategoryDto();
        dto.setId(10L);
        dto.setIdentifier("NEW");
        dto.setSuperCategory(PARENT);

        when(repository.findById(10L))
                .thenReturn(Optional.of(existing));

        when(repository.findByIdentifierAndDeletedFalse("NEW"))
                .thenReturn(null);

        service.update(dto);

        verify(repository).save(existing);
    }

    @Test
    void updateNullSuperCategoryCoverage() {

        Category existing = new Category();
        existing.setId(20L);
        existing.setIdentifier("CAT1");
        existing.setCreatedBy(ADMIN);
        existing.setCreatedOn(LocalDateTime.now());

        CategoryDto dto = new CategoryDto();
        dto.setId(20L);
        dto.setIdentifier("CAT1");
        dto.setSuperCategory("");

        when(repository.findById(20L))
                .thenReturn(Optional.of(existing));

        service.update(dto);

        verify(repository).save(existing);

        assertNull(existing.getSuperCategory());
    }

    @Test
    void childAndActiveCategoryTest() {

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

        assertTrue(service.findAllActive().isEmpty());
    }

    @Test
    void toggleStatusTest() {

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

        NoSuchElementException exception =
                assertThrows(
                        NoSuchElementException.class,
                        () -> service.toggleStatus("CAT3")
                );

        assertEquals(
                "category not found with identifier: CAT3",
                exception.getMessage()
        );
    }
}