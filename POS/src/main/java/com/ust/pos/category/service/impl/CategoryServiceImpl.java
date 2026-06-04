package com.ust.pos.category.service.impl;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ModelMapper modelMapper;

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        if (StringUtils.isEmpty(categoryDto.getSuperCategory())) {
            categoryDto.setSuperCategory(null);
        }
        Category existingCategory = categoryRepository.findByIdentifier(categoryDto.getIdentifier());
        if (existingCategory != null) {
            categoryDto.setMessage("Category with identifier - " + categoryDto.getIdentifier() + " already exists");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        Category category = modelMapper.map(categoryDto, Category.class);
        categoryRepository.save(category);
        return categoryDto;
    }

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        if (pageable == null) {
            return modelMapper.map(categoryRepository.findAll(), listType);
        }
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        return modelMapper.map(categoryPage.getContent(), listType);
    }

    @Override
    public CategoryDto findByIdentifier(String identifier) {
        return modelMapper.map(categoryRepository.findByIdentifier(identifier), CategoryDto.class);
    }

    @Transactional
    @Override
    public void deleteByIdentifier(String identifier) {
        categoryRepository.deleteByIdentifier(identifier);
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        Category existingCategory = categoryRepository.findByIdentifier(categoryDto.getIdentifier());
        if (existingCategory == null) {
            categoryDto.setMessage("Category with identifier - " + categoryDto.getIdentifier() + "not found");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        modelMapper.map(categoryDto, existingCategory);
        categoryRepository.save(existingCategory);
        return categoryDto;
    }

    @Override
    public List<CategoryDto> findSubCategories() {
        return categoryRepository.findBySuperCategoryNotNull().stream().map(category -> modelMapper.map(category, CategoryDto.class)).toList();
    }

}