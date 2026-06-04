package com.ust.pos.category.service.impl;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
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
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryDto findByIdentifier(String identifier) {
        Category category = categoryRepository.findByIdentifier(identifier);
        return category == null ? null : modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public CategoryDto save(CategoryDto dto) {

        if (categoryRepository.findByIdentifier(dto.getIdentifier()) != null) {
            dto.setSuccess(false);
            dto.setMessage("Category with identifier - " + dto.getIdentifier() + " already exists");
            return dto;
        }

        Category category = new Category();
        category.setIdentifier(dto.getIdentifier());
        category.setStatus(true);

        String superCategory = dto.getSuperCategory();

        if (superCategory == null || superCategory.trim().isEmpty()) {
            category.setSuperCategory(null);
        } else {
            category.setSuperCategory(superCategory.trim());
        }

        categoryRepository.save(category);

        CategoryDto response = modelMapper.map(category, CategoryDto.class);
        response.setSuccess(true);
        return response;
    }

    @Override
    public CategoryDto update(CategoryDto dto) {

        Category category = categoryRepository.findByIdentifier(dto.getIdentifier());

        if (category == null) {
            dto.setSuccess(false);
            dto.setMessage("Category with identifier - " + dto.getIdentifier() + " not found");
            return dto;
        }

        String superCategory = dto.getSuperCategory();

        if (superCategory == null || superCategory.trim().isEmpty()) {
            category.setSuperCategory(null);
        } else {
            category.setSuperCategory(superCategory.trim());
        }

        categoryRepository.save(category);

        CategoryDto response = modelMapper.map(category, CategoryDto.class);
        response.setSuccess(true);
        return response;
    }

    @Override
    public boolean delete(String identifier) {
        categoryRepository.deleteByIdentifier(identifier);
        return true;
    }

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        return modelMapper.map(categoryPage.getContent(), listType);
    }


    @Override
    public List<CategoryDto> findSuperCategories() {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();

        return modelMapper.map(categoryRepository.findBySuperCategoryIsNull(), listType);
    }


    @Override
    public CategoryDto toggleStatus(String identifier) {
        Category category = categoryRepository.findByIdentifier(identifier);
        if (category == null) return null;

        if (category.getSuperCategory() == null) {
            return modelMapper.map(category, CategoryDto.class);
        }

        category.setStatus(!category.isStatus());
        categoryRepository.save(category);

        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public List<CategoryDto> findIfTrue() {

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();

        List<Category> categories = categoryRepository.findBySuperCategoryIsNotNull().stream().filter(Category::isStatus).toList();

        return modelMapper.map(categories, listType);
    }
}