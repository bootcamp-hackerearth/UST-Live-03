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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    void testFindByIdentifier_Success() {
        String identifier = "CAT-001";

        Category category = new Category();
        category.setIdentifier(identifier);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier(identifier);

        when(categoryRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class))
                .thenReturn(categoryDto);

        CategoryDto result = categoryService.findByIdentifier(identifier);

        assertEquals(identifier, result.getIdentifier());
    }

    @Test
    void testSave_Success() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT-001");

        Category category = new Category();
        category.setIdentifier("CAT-001");

        when(categoryRepository.findByIdentifier("CAT-001"))
                .thenReturn(null);
        when(modelMapper.map(categoryDto, Category.class))
                .thenReturn(category);

        CategoryDto result = categoryService.save(categoryDto);

        assertTrue(result.isSuccess());
        assertTrue(category.isStatus());
        assertFalse(category.getDeleted());

        verify(categoryRepository).save(category);
    }

    @Test
    void testSave_AlreadyExists() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT-001");

        Category existingCategory = new Category();
        existingCategory.setIdentifier("CAT-001");
        existingCategory.setDeleted(false);

        when(categoryRepository.findByIdentifier("CAT-001"))
                .thenReturn(existingCategory);

        CategoryDto result = categoryService.save(categoryDto);

        assertFalse(result.isSuccess());
        assertEquals("Category with identifier - CAT-001 already exists", result.getMessage());

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testSave_DeletedCategoryExists() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT-001");

        Category existingCategory = new Category();
        existingCategory.setIdentifier("CAT-001");
        existingCategory.setDeleted(true);

        when(categoryRepository.findByIdentifier("CAT-001"))
                .thenReturn(existingCategory);

        CategoryDto result = categoryService.save(categoryDto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Category with identifier - CAT-001 was deleted and cannot be created again.",
                result.getMessage()
        );

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testUpdate_Success() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT-001");

        Category existingCategory = new Category();
        existingCategory.setIdentifier("CAT-001");
        existingCategory.setDeleted(true);

        when(categoryRepository.findByIdentifierAndDeletedFalse("CAT-001"))
                .thenReturn(existingCategory);

        CategoryDto result = categoryService.update(categoryDto);

        assertTrue(result.isSuccess());
        assertFalse(existingCategory.getDeleted());

        verify(modelMapper).map(categoryDto, existingCategory);
        verify(categoryRepository).save(existingCategory);
    }

    @Test
    void testUpdate_NotFound() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT-001");

        when(categoryRepository.findByIdentifierAndDeletedFalse("CAT-001"))
                .thenReturn(null);

        CategoryDto result = categoryService.update(categoryDto);

        assertFalse(result.isSuccess());
        assertEquals("Category with identifier - CAT-001 not found", result.getMessage());

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testDelete_Success() {
        String identifier = "CAT-001";

        Category category = new Category();
        category.setIdentifier(identifier);
        category.setDeleted(false);

        when(categoryRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(category);

        categoryService.delete(identifier);

        assertTrue(category.getDeleted());

        verify(categoryRepository).save(category);
    }

    @Test
    void testFindAll_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        Category category = new Category();
        category.setIdentifier("CAT-001");

        Page<Category> categoryPage =
                new PageImpl<>(List.of(category), pageable, 1);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT-001");

        when(categoryRepository.findAllByDeletedFalse(pageable))
                .thenReturn(categoryPage);

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(List.of(categoryDto));

        WsDto<CategoryDto> result = categoryService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void testFindChildCategories_Success() {
        Category category = new Category();
        category.setIdentifier("CAT-CHILD");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT-CHILD");

        when(categoryRepository.findBySuperCategoryIsNotAndDeletedFalse(""))
                .thenReturn(List.of(category));

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(List.of(categoryDto));

        List<CategoryDto> result = categoryService.findChildCategories();

        assertEquals(1, result.size());
        assertEquals("CAT-CHILD", result.get(0).getIdentifier());
    }

    @Test
    void testToggleStatus_Success() {
        String identifier = "CAT-001";

        Category category = new Category();
        category.setIdentifier(identifier);
        category.setStatus(true);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier(identifier);

        when(categoryRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class))
                .thenReturn(categoryDto);

        CategoryDto result = categoryService.toggleStatus(identifier);

        assertFalse(category.isStatus());
        assertNotNull(result);

        verify(categoryRepository).save(category);
    }

    @Test
    void testToggleStatus_NotFound() {
        String identifier = "CAT-001";

        when(categoryRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(null);

        CategoryDto result = categoryService.toggleStatus(identifier);

        assertFalse(result.isSuccess());
        assertEquals("Category not found", result.getMessage());

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testFindActiveCategories_Success() {
        Category category = new Category();
        category.setIdentifier("CAT-001");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("CAT-001");

        when(categoryRepository.findByStatusTrueAndDeletedFalse())
                .thenReturn(List.of(category));

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(List.of(categoryDto));

        List<CategoryDto> result = categoryService.findActiveCategories();

        assertEquals(1, result.size());
        assertEquals("CAT-001", result.get(0).getIdentifier());
    }

    @Test
    void findAll_ShouldReturnWsDtoWithCategoryDtos() {
        Specification<Category> specification = (root, query, criteriaBuilder) -> null;
        Pageable pageable = PageRequest.of(0, 10);

        Category category = new Category();
        category.setId(1L);
        category.setIdentifier("CAT001");
        category.setSuperCategory("Electronics");

        List<Category> categoryList = List.of(category);
        Page<Category> categoryPage =
                new PageImpl<>(categoryList, pageable, categoryList.size());

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setIdentifier("CAT001");
        categoryDto.setSuperCategory("Electronics");

        List<CategoryDto> categoryDtoList = List.of(categoryDto);

        when(categoryRepository.findAll(specification, pageable))
                .thenReturn(categoryPage);

        when(modelMapper.map(eq(categoryList), any(Type.class)))
                .thenReturn(categoryDtoList);

        WsDto<CategoryDto> result = categoryService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(categoryDtoList, result.getDtoList());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(categoryRepository, times(1)).findAll(specification, pageable);
        verify(modelMapper, times(1)).map(eq(categoryList), any(Type.class));
    }

}