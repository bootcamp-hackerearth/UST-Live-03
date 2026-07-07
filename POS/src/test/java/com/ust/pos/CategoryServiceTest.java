package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.Category;
import com.ust.pos.models.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    private CategoryDto createDto(String identifier) {
        CategoryDto dto = new CategoryDto();
        dto.setIdentifier(identifier);
        return dto;
    }

    @Test
    void saveTest() {
        CategoryDto dto = createDto("CAT1");
        when(categoryRepository.findByIdentifier("CAT1")).thenReturn(null);
        CategoryDto result = categoryService.save(dto);
        assertTrue(result.isSuccess());
        assertEquals("Category created successfully", result.getMessage());
        Category existing = new Category();
        existing.setDeleted(false);
        when(categoryRepository.findByIdentifier("CAT2")).thenReturn(existing);
        result = categoryService.save(createDto("CAT2"));
        assertFalse(result.isSuccess());
        assertEquals("Category with identifier - CAT2 already exists", result.getMessage());
        Category deleted = new Category();
        deleted.setDeleted(true);
        when(categoryRepository.findByIdentifier("CAT3")).thenReturn(deleted);
        result = categoryService.save(createDto("CAT3"));
        assertFalse(result.isSuccess());
        assertEquals("Category with identifier - CAT3 was deleted and cannot be created again.", result.getMessage());
        CategoryDto nullParent = createDto("CAT4");
        nullParent.setSuperCategory(null);
        when(categoryRepository.findByIdentifier("CAT4")).thenReturn(null);
        categoryService.save(nullParent);
        verify(categoryRepository, atLeastOnce()).save(argThat(c -> c.getSuperCategory() == null));
        CategoryDto blankParent = createDto("CAT5");
        blankParent.setSuperCategory(" ");
        when(categoryRepository.findByIdentifier("CAT5")).thenReturn(null);
        categoryService.save(blankParent);
        verify(categoryRepository, atLeastOnce()).save(argThat(c -> c.getSuperCategory() == null));
        CategoryDto parentDto = createDto("CAT6");
        parentDto.setSuperCategory("PARENT");
        when(categoryRepository.findByIdentifier("CAT6")).thenReturn(null);
        categoryService.save(parentDto);
        verify(categoryRepository).save(argThat(c -> "PARENT".equals(c.getSuperCategory())));
    }

    @Test
    void updateTest() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setIdentifier("OLD");
        CategoryDto dto = new CategoryDto();
        dto.setId(1L);
        dto.setIdentifier("NEW");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findByIdentifierAndDeletedFalse("NEW")).thenReturn(null);
        CategoryDto result = categoryService.update(dto);
        assertNotNull(result);
        assertEquals("NEW", existing.getIdentifier());
        verify(categoryRepository).save(existing);
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());
        CategoryDto notFound = new CategoryDto();
        notFound.setId(2L);
        result = categoryService.update(notFound);
        assertFalse(result.isSuccess());
        assertEquals("Category not found", result.getMessage());
        Category duplicateEntity = new Category();
        duplicateEntity.setId(3L);
        duplicateEntity.setIdentifier("OLD");
        Category duplicate = new Category();
        duplicate.setId(99L);
        when(categoryRepository.findById(3L)).thenReturn(Optional.of(duplicateEntity));
        when(categoryRepository.findByIdentifierAndDeletedFalse("DUP")).thenReturn(duplicate);
        CategoryDto duplicateDto = new CategoryDto();
        duplicateDto.setId(3L);
        duplicateDto.setIdentifier("DUP");
        result = categoryService.update(duplicateDto);
        assertFalse(result.isSuccess());
        assertEquals("Category already exists", result.getMessage());
        Category category1 = new Category();
        category1.setId(4L);
        category1.setIdentifier("CAT4");
        CategoryDto dto1 = new CategoryDto();
        dto1.setId(4L);
        dto1.setIdentifier("CAT4");
        dto1.setSuperCategory(null);
        when(categoryRepository.findById(4L)).thenReturn(Optional.of(category1));
        categoryService.update(dto1);
        assertNull(category1.getSuperCategory());
        Category category2 = new Category();
        category2.setId(5L);
        category2.setIdentifier("CAT5");
        CategoryDto dto2 = new CategoryDto();
        dto2.setId(5L);
        dto2.setIdentifier("CAT5");
        dto2.setSuperCategory("PARENT");
        when(categoryRepository.findById(5L)).thenReturn(Optional.of(category2));
        categoryService.update(dto2);
        assertEquals("PARENT", category2.getSuperCategory());
    }

    @Test
    void deleteTest() {
        Category category = new Category();
        category.setIdentifier("CAT1");
        when(categoryRepository.existsBySuperCategoryAndDeletedFalse("CAT1")).thenReturn(false);
        when(categoryRepository.findByIdentifierAndDeletedFalse("CAT1")).thenReturn(category);
        categoryService.delete("CAT1");
        assertTrue(category.getDeleted());
        verify(categoryRepository).save(category);
        when(categoryRepository.existsBySuperCategoryAndDeletedFalse("CAT2")).thenReturn(true);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> categoryService.delete("CAT2"));
        assertEquals("Cannot delete category. It is used as a super category.", ex.getMessage());
        when(categoryRepository.existsBySuperCategoryAndDeletedFalse("CAT3")).thenReturn(false);
        when(categoryRepository.findByIdentifierAndDeletedFalse("CAT3")).thenReturn(null);
        assertDoesNotThrow(() -> categoryService.delete("CAT3"));
    }

    @Test
    void findByIdentifierTest() {
        Category category = new Category();
        CategoryDto dto = new CategoryDto();
        when(categoryRepository.findByIdentifierAndDeletedFalse("CAT1")).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(dto);
        CategoryDto result = categoryService.findByIdentifier("CAT1");
        assertNotNull(result);
        verify(categoryRepository).findByIdentifierAndDeletedFalse("CAT1");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Category category = new Category();
        Page<Category> page = new PageImpl<>(List.of(category), pageable, 1);
        when(categoryRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(new CategoryDto()));
        WsDto<CategoryDto> result = categoryService.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void findAllWithSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 10);
        @SuppressWarnings("unchecked")
        Specification<Category> specification = mock(Specification.class);
        Page<Category> page = new PageImpl<>(List.of(new Category()), pageable, 1);
        when(categoryRepository.findAll(specification, pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(new CategoryDto()));
        WsDto<CategoryDto> result = categoryService.findAll(specification, pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        verify(categoryRepository).findAll(specification, pageable);
    }

    @Test
    void findChildCategoriesTest() {
        List<Category> categories = List.of(new Category());
        List<CategoryDto> dtos = List.of(new CategoryDto());
        when(categoryRepository.findBySuperCategoryIsNotNull()).thenReturn(categories);
        when(modelMapper.map(eq(categories), any(Type.class))).thenReturn(dtos);
        List<CategoryDto> result = categoryService.findChildCategories();
        assertEquals(1, result.size());
    }

    @Test
    void toggleStatusAndFindAllActiveTest() {
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));
        when(modelMapper.map(any(Category.class), eq(CategoryDto.class))).thenReturn(new CategoryDto());
        Category category = new Category();
        category.setStatus(true);
        when(categoryRepository.findByIdentifierAndDeletedFalse("CAT1")).thenReturn(category);
        categoryService.toggleStatus("CAT1");
        assertFalse(category.getStatus());
        category.setStatus(false);
        when(categoryRepository.findByIdentifierAndDeletedFalse("CAT2")).thenReturn(category);
        categoryService.toggleStatus("CAT2");
        assertTrue(category.getStatus());
        category.setStatus(null);
        when(categoryRepository.findByIdentifierAndDeletedFalse("CAT3")).thenReturn(category);
        categoryService.toggleStatus("CAT3");
        assertTrue(category.getStatus());
        when(categoryRepository.findByIdentifierAndDeletedFalse("CAT4")).thenReturn(null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> categoryService.toggleStatus("CAT4"));
        assertEquals("Product not found with identifier: CAT4", ex.getMessage());
        when(categoryRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of(category));
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(new CategoryDto());
        assertEquals(1, categoryService.findAllActive().size());
        when(categoryRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of());
        assertTrue(categoryService.findAllActive().isEmpty());
    }
}