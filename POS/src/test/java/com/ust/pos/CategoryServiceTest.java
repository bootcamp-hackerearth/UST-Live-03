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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

        Mockito.when(categoryRepository.findByIdentifier("C1"))
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

        Category existing = new Category();

        Mockito.when(categoryRepository.findByIdentifier("C1"))
                .thenReturn(existing);

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

        Mockito.when(categoryRepository.findByIdentifier("C1"))
                .thenReturn(category);

        Mockito.when(modelMapper.map(category, CategoryDto.class))
                .thenReturn(dto);

        CategoryDto response = categoryService.findByIdentifier("C1");

        Assertions.assertEquals("C1", response.getIdentifier());
    }

    @Test
    void findByIdentifierNullTest() {
        Mockito.when(categoryRepository.findByIdentifier("C1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(null, CategoryDto.class))
                .thenReturn(null);

        CategoryDto response = categoryService.findByIdentifier("C1");

        Assertions.assertNull(response);
    }

    @Test
    void updateSuccessTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("C1");

        Category existing = new Category();

        Mockito.when(categoryRepository.findByIdentifier("C1"))
                .thenReturn(existing);

        CategoryDto response = categoryService.update(categoryDto);

        Assertions.assertEquals("C1", response.getIdentifier());

        Mockito.verify(modelMapper)
                .map(categoryDto, existing);

        Mockito.verify(categoryRepository)
                .save(existing);
    }

    @Test
    void updateNotFoundTest() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("C1");

        Mockito.when(categoryRepository.findByIdentifier("C1"))
                .thenReturn(null);

        CategoryDto response = categoryService.update(categoryDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("not found"));

        Mockito.verify(categoryRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(categoryRepository)
                .deleteByIdentifier("C1");

        categoryService.delete("C1");

        Mockito.verify(categoryRepository)
                .deleteByIdentifier("C1");
    }

    @Test
    void findAllTest() {
        Category category = new Category();
        category.setIdentifier("C1");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("C1");

        List<Category> categories = List.of(category);
        List<CategoryDto> dtos = List.of(dto);

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();

        Mockito.when(categoryRepository.findAll())
                .thenReturn(categories);

        Mockito.when(modelMapper.map(categories, listType))
                .thenReturn(dtos);

        List<CategoryDto> response = categoryService.findAll();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(
                "C1",
                response.get(0).getIdentifier()
        );
    }

    @Test
    void findBySuperCategoryNotNullTest() {
        Category category = new Category();
        category.setIdentifier("SubCategory");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("SubCategory");

        List<Category> categories = List.of(category);
        List<CategoryDto> dtos = List.of(dto);

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();

        Mockito.when(categoryRepository.findBySuperCategoryIsNot(""))
                .thenReturn(categories);

        Mockito.when(modelMapper.map(categories, listType))
                .thenReturn(dtos);

        List<CategoryDto> response =
                categoryService.findBySuperCategoryNotNull();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(
                "SubCategory",
                response.get(0).getIdentifier()
        );
    }

    @Test
    void findAllWithPaginationShouldReturnCategoryDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        Category category = new Category();
        category.setIdentifier("C1");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("C1");

        Page<Category> categoryPage =
                new PageImpl<>(List.of(category));

        Mockito.when(categoryRepository.findAll(pageable))
                .thenReturn(categoryPage);

        Mockito.when(modelMapper.map(category, CategoryDto.class))
                .thenReturn(dto);

        Page<CategoryDto> response =
                categoryService.findAll(pageable, null);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getContent().size());
        Assertions.assertEquals(
                "C1",
                response.getContent().get(0).getIdentifier()
        );

        Mockito.verify(categoryRepository).findAll(pageable);
    }

    @Test
    void findAllWithSearchShouldReturnCategoryDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        Category category = new Category();
        category.setIdentifier("C1");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("C1");

        Page<Category> categoryPage =
                new PageImpl<>(List.of(category));

        Mockito.when(
                categoryRepository.findByIdentifierContainingIgnoreCase(
                        "C1",
                        pageable
                )
        ).thenReturn(categoryPage);

        Mockito.when(modelMapper.map(category, CategoryDto.class))
                .thenReturn(dto);

        Page<CategoryDto> response =
                categoryService.findAll(pageable, "C1");

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );

        Assertions.assertEquals(
                "C1",
                response.getContent().get(0).getIdentifier()
        );

        Mockito.verify(categoryRepository)
                .findByIdentifierContainingIgnoreCase(
                        "C1",
                        pageable
                );
    }
}