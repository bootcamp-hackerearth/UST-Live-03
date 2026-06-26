package com.ust.pos.category.service.impl;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private static final String CATEGORY_WITH_IDENTIFIER = "Category with identifier - ";

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingCategory = categoryRepository.findByIdentifier(identifier);

        if (existingCategory != null) {
            if (Boolean.TRUE.equals(existingCategory.getIsDeleted())) {
                categoryDto.setMessage(CATEGORY_WITH_IDENTIFIER + identifier + " was deleted. Contact admin for further support or try with a different identifier.");
            } else {
                categoryDto.setMessage(CATEGORY_WITH_IDENTIFIER + identifier + " already exists");
            }
            categoryDto.setSuccess(false);
            return categoryDto;
        }

        Category category = modelMapper.map(categoryDto, Category.class);
        category.setIsDeleted(false);
        categoryRepository.save(category);
        categoryDto.setSuccess(true);
        return categoryDto;
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingCategory = categoryRepository.findByIdentifier(identifier);

        if (existingCategory == null) {
            categoryDto.setMessage(CATEGORY_WITH_IDENTIFIER + identifier + " not found");
            categoryDto.setSuccess(false);
            return categoryDto;
        }

        modelMapper.map(categoryDto, existingCategory);
        categoryRepository.save(existingCategory);
        return categoryDto;
    }

    @Override
    public CategoryDto delete(String identifier) {
        CategoryDto categoryDto = new CategoryDto();
        Category category = categoryRepository.findByIdentifier(identifier);

        if (category == null) {
            categoryDto.setMessage(CATEGORY_WITH_IDENTIFIER + identifier + " not found");
            categoryDto.setSuccess(false);
            return categoryDto;
        }

        if (categoryRepository.existsBySuperCategoryAndIsDeleted(identifier, false)) {
            categoryDto.setMessage("Cannot delete category because it is used as a super category");
            categoryDto.setSuccess(false);
            return categoryDto;
        }

        category.setIsDeleted(true);
        category.setStatus(false);
        categoryRepository.save(category);
        categoryDto.setSuccess(true);
        categoryDto.setMessage("Category deleted successfully");
        return categoryDto;
    }

    @Override
    public PaginatedResponseDto<CategoryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        Page<Category> categoryPage = categoryRepository.findByIsDeleted(false, pageable);
        List<CategoryDto> items = modelMapper.map(categoryPage.getContent(), listType);
        PaginatedResponseDto<CategoryDto> response = new PaginatedResponseDto<>();
        response.setItems(items);
        response.setTotalRecords(categoryPage.getTotalElements());
        response.setTotalPages(categoryPage.getTotalPages());
        response.setSizePerPage(pageable.getPageSize());
        response.setPage(pageable.getPageNumber());
        return response;
    }

    @Override
    public CategoryDto findByIdentifier(String identifier) {
        return modelMapper.map(categoryRepository.findByIdentifier(identifier), CategoryDto.class);
    }

    @Override
    public List<CategoryDto> findAllActive() {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(categoryRepository.findByStatusAndIsDeleted(true, false), listType);
    }

    @Override
    public void changeStatus(String identifier, boolean status) {
        Category category = categoryRepository.findByIdentifier(identifier);
        category.setStatus(status);
        categoryRepository.save(category);
    }
}