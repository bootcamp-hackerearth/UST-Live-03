package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("C1");

        Category category = new Category();

        Mockito.when(categoryRepository.findByIdentifierAndDeletedFalse("C1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(categoryDto, Category.class))
                .thenReturn(category);

        CategoryDto response = categoryService.save(categoryDto);

        Assertions.assertEquals("C1", response.getIdentifier());

        Mockito.verify(categoryRepository).save(category);
    }

    @Test
    void saveDuplicateCategoryTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("C1");

        Mockito.when(categoryRepository.findByIdentifierAndDeletedFalse("C1"))
                .thenReturn(new Category());

        CategoryDto response = categoryService.save(categoryDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("already exists"));

        Mockito.verify(categoryRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {
        Category category = new Category();
        category.setIdentifier("C1");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("C1");

        Mockito.when(categoryRepository.findByIdentifierAndDeletedFalse("C1"))
                .thenReturn(category);

        Mockito.when(modelMapper.map(category, CategoryDto.class))
                .thenReturn(dto);

        CategoryDto response = categoryService.findByIdentifier("C1");

        Assertions.assertEquals("C1", response.getIdentifier());
    }

    @Test
    void findByIdentifierNullTest() {
        Mockito.when(categoryRepository.findByIdentifierAndDeletedFalse("C1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map((Category) null, CategoryDto.class))
                .thenReturn(null);

        Assertions.assertNull(categoryService.findByIdentifier("C1"));
    }

    @Test
    void updateSuccessTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("C1");

        Category existing = new Category();

        Mockito.when(categoryRepository.findByIdentifierAndDeletedFalse("C1"))
                .thenReturn(existing);

        CategoryDto response = categoryService.update(categoryDto);

        Assertions.assertEquals("C1", response.getIdentifier());

        Mockito.verify(modelMapper).map(categoryDto, existing);
        Mockito.verify(categoryRepository).save(existing);
    }

    @Test
    void updateNotFoundTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("C1");

        Mockito.when(categoryRepository.findByIdentifierAndDeletedFalse("C1"))
                .thenReturn(null);

        CategoryDto response = categoryService.update(categoryDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("not found"));

        Mockito.verify(categoryRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void deleteTest() {
        Category category = new Category();

        Mockito.when(categoryRepository.findByIdentifierAndDeletedFalse("C1"))
                .thenReturn(category);

        categoryService.delete("C1");

        Assertions.assertTrue(category.isDeleted());

        Mockito.verify(categoryRepository).save(category);
    }

    @Test
    void findAllTest() {
        List<Category> categories = List.of(new Category());
        List<CategoryDto> dtos = List.of(new CategoryDto());

        Mockito.when(categoryRepository.findByDeletedFalse())
                .thenReturn(categories);

        Mockito.when(modelMapper.map(Mockito.eq(categories), Mockito.any(Type.class)))
                .thenReturn(dtos);

        List<CategoryDto> response = categoryService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findBySuperCategoryNotNullTest() {
        List<Category> categories = List.of(new Category());
        List<CategoryDto> dtos = List.of(new CategoryDto());

        Mockito.when(categoryRepository.findBySuperCategoryIsNotAndDeletedFalse(""))
                .thenReturn(categories);

        Mockito.when(modelMapper.map(Mockito.eq(categories), Mockito.any(Type.class)))
                .thenReturn(dtos);

        List<CategoryDto> response = categoryService.findBySuperCategoryNotNull();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllWithPaginationShouldReturnCategoryDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        Category category = new Category();
        CategoryDto dto = new CategoryDto();

        Page<Category> page = new PageImpl<>(List.of(category));

        Mockito.when(categoryRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(category, CategoryDto.class))
                .thenReturn(dto);

        Page<CategoryDto> response = categoryService.findAll(pageable, null);

        Assertions.assertEquals(1, response.getContent().size());

        Mockito.verify(categoryRepository).findByDeletedFalse(pageable);
    }

    @Test
    void findAllWithSearchShouldReturnCategoryDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        Category category = new Category();
        CategoryDto dto = new CategoryDto();

        Page<Category> page = new PageImpl<>(List.of(category));

        Mockito.when(categoryRepository.findAll(Mockito.<Specification<Category>>any(), Mockito.eq(pageable)))
                .thenReturn(page);

        Mockito.when(modelMapper.map(category, CategoryDto.class))
                .thenReturn(dto);

        Page<CategoryDto> response = categoryService.findAll(pageable, "C1");

        Assertions.assertEquals(1, response.getContent().size());

        Mockito.verify(categoryRepository)
                .findAll(Mockito.<Specification<Category>>any(), Mockito.eq(pageable));
    }
}