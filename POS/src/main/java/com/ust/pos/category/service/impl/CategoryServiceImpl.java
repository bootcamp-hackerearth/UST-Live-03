package com.ust.pos.category.service.impl;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryDto findByIdentifier(String identifier) {
        return modelMapper.map(categoryRepository.findByIdentifierAndDeletedFalse(identifier), CategoryDto.class);
    }

    @Override
    public List<CategoryDto> findBySuperCategoryNotNull() {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(categoryRepository.findBySuperCategoryIsNotAndDeletedFalse(""), listType);
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();

        Category existingCategory = categoryRepository.findByIdentifierAndDeletedFalse(identifier);

        if (existingCategory != null) {
            categoryDto.setMessage("Category with identifier - " + identifier + " already exists");
            categoryDto.setSuccess(false);
            return categoryDto;
        }

        Category category = modelMapper.map(categoryDto, Category.class);
        categoryRepository.save(category);

        return categoryDto;
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingCategory = categoryRepository.findByIdentifierAndDeletedFalse(identifier);

        if (existingCategory == null) {
            categoryDto.setMessage("Category with identifier - " + identifier + " not found");
            categoryDto.setSuccess(false);
            return categoryDto;
        }

        modelMapper.map(categoryDto, existingCategory);
        categoryRepository.save(existingCategory);
        return categoryDto;
    }

    @Override
    public void delete(String identifier) {
        Category category =
                categoryRepository.findByIdentifierAndDeletedFalse(identifier);

        if (category != null) {
            category.setDeleted(true);
            categoryRepository.save(category);
        }
    }

    @Override
    public List<CategoryDto> findAll() {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(categoryRepository.findByDeletedFalse(), listType);
    }

    @Override
    public Page<CategoryDto> findAll(Pageable pageable, String search) {
        Page<Category> categories;
        if (search != null && !search.trim().isEmpty()) {
            categories = categoryRepository.findByIdentifierContainingIgnoreCaseAndDeletedFalse(search, pageable);
        } else {
            categories = categoryRepository.findByDeletedFalse(pageable);
        }
        return categories.map(category -> modelMapper.map(category, CategoryDto.class));
    }
}