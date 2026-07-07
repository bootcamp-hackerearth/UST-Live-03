package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImplementation;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
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
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImplementation categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveCategorySuccess() {

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("ELEC");

        Category category = new Category();

        Mockito.when(
                categoryRepository.findByIdentifierAndDeletedFalse("ELEC")
        ).thenReturn(null);

        Mockito.when(
                modelMapper.map(categoryDto, Category.class)
        ).thenReturn(category);

        CategoryDto response =
                categoryService.save(categoryDto);

        Assertions.assertNull(response.getMessage());

        Mockito.verify(categoryRepository)
                .save(category);

        Assertions.assertFalse(category.getDeleted());
    }

    @Test
    void saveCategoryAlreadyExists() {

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("ELEC");

        Mockito.when(
                categoryRepository.findByIdentifierAndDeletedFalse("ELEC")
        ).thenReturn(new Category());

        CategoryDto response =
                categoryService.save(categoryDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Category with identifier - ELEC already exists",
                response.getMessage()
        );

        Mockito.verify(categoryRepository,
                Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateCategorySuccess() {

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("ELEC");

        Category existingCategory = new Category();

        Mockito.when(
                categoryRepository.findByIdentifierAndDeletedFalse("ELEC")
        ).thenReturn(existingCategory);

        CategoryDto response =
                categoryService.update(categoryDto);

        Assertions.assertNull(response.getMessage());

        Mockito.verify(modelMapper)
                .map(categoryDto, existingCategory);

        Mockito.verify(categoryRepository)
                .save(existingCategory);
    }

    @Test
    void updateCategoryNotFound() {

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setIdentifier("ELEC");

        Mockito.when(
                categoryRepository.findByIdentifierAndDeletedFalse("ELEC")
        ).thenReturn(null);

        CategoryDto response =
                categoryService.update(categoryDto);

        Assertions.assertEquals(
                "Category with identifier - ELEC not found",
                response.getMessage()
        );

        Mockito.verify(categoryRepository,
                Mockito.never()).save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {

        Category category = new Category();
        category.setIdentifier("ELEC");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("ELEC");

        Mockito.when(
                categoryRepository.findByIdentifierAndDeletedFalse("ELEC")
        ).thenReturn(category);

        Mockito.when(
                modelMapper.map(category, CategoryDto.class)
        ).thenReturn(dto);

        CategoryDto response =
                categoryService.findByIdentifier("ELEC");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("ELEC", response.getIdentifier());
    }

    @Test
    void findAllCategoriesTest() {

        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category());
        categoryList.add(new Category());

        List<CategoryDto> dtoList = new ArrayList<>();
        dtoList.add(new CategoryDto());
        dtoList.add(new CategoryDto());

        Mockito.when(
                categoryRepository.findByDeletedFalse()
        ).thenReturn(categoryList);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(categoryList),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtoList);

        List<CategoryDto> response =
                categoryService.findAll();

        Assertions.assertEquals(2, response.size());
    }

    @Test
    void findAllWithPaginationTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        List<Category> categories =
                List.of(new Category());

        Page<Category> categoryPage =
                new PageImpl<>(categories, pageable, 1);

        List<CategoryDto> categoryDtos =
                List.of(new CategoryDto());

        Type listType =
                new TypeToken<List<CategoryDto>>(){}.getType();

        Mockito.when(
                categoryRepository.findByDeletedFalse(pageable)
        ).thenReturn(categoryPage);

        Mockito.when(
                modelMapper.map(categories, listType)
        ).thenReturn(categoryDtos);

        WsDto<CategoryDto> response =
                categoryService.findAll(pageable);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );
    }

    @Test
    void findAllSearchTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Category category = new Category();
        category.setIdentifier("ELE");

        Example<Category> example = Example.of(
                category,
                ExampleMatcher.matching()
                        .withMatcher(
                                "identifier",
                                ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase()
                        )
        );

        Page<Category> page =
                new PageImpl<>(List.of(category));

        Mockito.when(
                categoryRepository.findAll(Mockito.any(Example.class), Mockito.eq(pageable))
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(category, CategoryDto.class)
        ).thenReturn(new CategoryDto());

        Page<CategoryDto> response =
                categoryService.findAll(example, pageable);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );
    }

    @Test
    void findAllWithoutSearchTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Category category = new Category();

        Example<Category> example = Example.of(new Category());

        Page<Category> page =
                new PageImpl<>(List.of(category));

        Mockito.when(
                categoryRepository.findAll(Mockito.any(Example.class), Mockito.eq(pageable))
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(category, CategoryDto.class)
        ).thenReturn(new CategoryDto());

        Page<CategoryDto> response =
                categoryService.findAll(example, pageable);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );
    }

    @Test
    void deleteCategoryTest() {

        Category category = new Category();
        category.setDeleted(false);

        Mockito.when(
                categoryRepository.findByIdentifierAndDeletedFalse("ELEC")
        ).thenReturn(category);

        categoryService.delete("ELEC");

        Assertions.assertTrue(category.getDeleted());

        Mockito.verify(categoryRepository)
                .save(category);
    }

    @Test
    void deleteCategoryNotFoundTest() {

        Mockito.when(
                categoryRepository.findByIdentifierAndDeletedFalse("ELEC")
        ).thenReturn(null);

        categoryService.delete("ELEC");

        Mockito.verify(categoryRepository,
                Mockito.never()).save(Mockito.any());
    }
}