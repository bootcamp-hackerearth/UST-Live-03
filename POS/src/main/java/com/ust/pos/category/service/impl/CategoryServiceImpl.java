package com.ust.pos.category.service.impl;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Transactional
@Service
public class CategoryServiceImpl implements CategoryService {
    public static final String CATEGORY_WITH_IDENTIFIER = "Category with identifier - ";
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        if (categoryRepository.findByIdentifier(identifier) != null) {
            categoryDto.setSuccess(false);
            categoryDto.setMessage(CATEGORY_WITH_IDENTIFIER + identifier + " already exists");
            return categoryDto;
        }
        if (identifier.equals(categoryDto.getSuperCategory())) {
            categoryDto.setSuccess(false);
            categoryDto.setMessage("Category cannot be its own super category");
            return categoryDto;
        }
        Category category = modelMapper.map(categoryDto, Category.class);
        category.setSuperCategory(categoryDto.getSuperCategory() == null ||
                categoryDto.getSuperCategory().isBlank() ? null : categoryDto.getSuperCategory());
        categoryRepository.save(category);
        categoryDto.setSuccess(true);
        categoryDto.setMessage("Category Added Successfully");
        return categoryDto;
    }

    @Override
    public CategoryDto findByIdentifier(String identifier) {
        return modelMapper.map(categoryRepository.findByIdentifier(identifier), CategoryDto.class);
    }

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        return modelMapper.map(categoryPage.getContent(), listType);
    }

    @Override
    public List<CategoryDto> findAllActive() {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(categoryRepository.findByStatusTrueAndSuperCategoryIsNot(""), listType);
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
        categoryDto.setMessage(CATEGORY_WITH_IDENTIFIER + identifier + " Updated ");
        categoryDto.setSuccess(true);
        return categoryDto;
    }

    @Override
    public CategoryDto toggleStatus(String identifier) {
        Category category = categoryRepository.findByIdentifier(identifier);
        category.setStatus(!category.isStatus());
        categoryRepository.save(category);
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public boolean delete(String identifier) {
        categoryRepository.deleteByIdentifier(identifier);
        return true;
    }
}