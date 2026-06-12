package com.ust.pos;

import com.ust.pos.category.service.impl.CategoryServiceImpl;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;


    @Test
    void findAll_success() {

        Category category = new Category();
        CategoryDto dto = new CategoryDto();
        Pageable pageable = Mockito.mock(Pageable.class);


        Page<Category> page = new PageImpl<>(List.of(category));

        when(categoryRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        when(modelMapper.map(
                        Mockito.eq(List.of(category)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        WsDto<CategoryDto> result =
                categoryService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());    }

    @Test
    void save_success() {

        CategoryDto input = new CategoryDto();
        input.setIdentifier("CAT01");

        when(categoryRepository.findByIdentifier("CAT01"))
                .thenReturn(null);

        Category entity = new Category();
        when(modelMapper.map(input, Category.class))
                .thenReturn(entity);

        when(categoryRepository.save(entity))
                .thenReturn(entity);

        CategoryDto result = categoryService.save(input);

        assertEquals("CAT01", result.getIdentifier());
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void save_failure_duplicate() {

        CategoryDto input = new CategoryDto();
        input.setIdentifier("CAT01");

        when(categoryRepository.findByIdentifier("CAT01"))
                .thenReturn(new Category());

        CategoryDto result = categoryService.save(input);

        Assertions.assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
    }

    @Test
    void delete_success() {

        Mockito.doNothing()
                .when(categoryRepository).deleteByIdentifier("CAT01");

        categoryService.delete("CAT01");

        Mockito.verify(categoryRepository)
                .deleteByIdentifier("CAT01");
    }

    @Test
    void findByIdentifier_success() {

        Category category = new Category();
        category.setIdentifier("CAT01");

        CategoryDto dto = new CategoryDto();
        dto.setIdentifier("CAT01");

        when(categoryRepository.findByIdentifier("CAT01"))
                .thenReturn(category);

        when(modelMapper.map(category, CategoryDto.class))
                .thenReturn(dto);

        CategoryDto result =
                categoryService.findByIdentifier("CAT01");

        assertEquals("CAT01", result.getIdentifier());
    }

    @Test
    void update_success() {

        CategoryDto input = new CategoryDto();
        input.setIdentifier("CAT01");

        Category existing = new Category();

        when(categoryRepository.findByIdentifier("CAT01"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper).map(input, existing);

        when(categoryRepository.save(existing))
                .thenReturn(existing);

        CategoryDto result = categoryService.update(input);

        assertEquals("CAT01", result.getIdentifier());
    }

    @Test
    void update_failure_notFound() {

        CategoryDto input = new CategoryDto();
        input.setIdentifier("CAT01");

        when(categoryRepository.findByIdentifier("CAT01"))
                .thenReturn(null);

        CategoryDto result = categoryService.update(input);

        Assertions.assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
    }

    @Test
    void findSubCategories_success() {

        Category category = new Category();

        CategoryDto dto = new CategoryDto();

        when(categoryRepository.findBySuperCategoryIsNot(" "))
                .thenReturn(List.of(category));

        when(modelMapper.map(category, CategoryDto.class))
                .thenReturn(dto);

        List<CategoryDto> result = categoryService.findSubCategories();

        assertEquals(1, result.size());
    }

    @Test
    void changeToggleStatus_enable() {

        Category category = new Category();
        category.setStatus(false);

        CategoryDto dto = new CategoryDto();

        when(categoryRepository.findByIdentifier("CAT01"))
                .thenReturn(category);

        when(categoryRepository.save(category))
                .thenReturn(category);

        when(modelMapper.map(category, CategoryDto.class))
                .thenReturn(dto);

        CategoryDto result =
                categoryService.changeToggleStatus("CAT01", true);

        Assertions.assertTrue(category.isStatus());
        assertNotNull(result);
    }

    @Test
    void changeToggleStatus_disable() {

        Category category = new Category();
        category.setStatus(true);

        CategoryDto dto = new CategoryDto();

        when(categoryRepository.findByIdentifier("CAT01"))
                .thenReturn(category);

        when(categoryRepository.save(category))
                .thenReturn(category);

        when(modelMapper.map(category, CategoryDto.class))
                .thenReturn(dto);

        CategoryDto result =
                categoryService.changeToggleStatus("CAT01", false);

        Assertions.assertFalse(category.isStatus());
        assertNotNull(result);
    }

    @Test
    void testFindActiveStatus() {
        // 1. Arrange: Create Category data
        Category active = new Category();
        active.setStatus(true);

        Category inactive = new Category();
        inactive.setStatus(false);

        // Stub the repository to return both active and inactive categories
        when(categoryRepository.findAll())
                .thenReturn(List.of(active, inactive));

        // Prepare the expected DTO output list
        CategoryDto dto = new CategoryDto();
        List<CategoryDto> expectedDtoList = List.of(dto);

        // FIX: Stub modelMapper to expect the precisely filtered list and ANY generic Type
        when(modelMapper.map(
                Mockito.eq(List.of(active)),
                Mockito.any(java.lang.reflect.Type.class)))
                .thenReturn(expectedDtoList);

        // 2. Act: Call your service layer method
        List<CategoryDto> result = categoryService.findActiveStatus();

        // 3. Assert: Verify the behavior
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}