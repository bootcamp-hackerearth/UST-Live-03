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
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class CategoryServiceImplementation implements CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingcategory = categoryRepository.findByIdentifier(identifier);
        if (existingcategory != null) {
            categoryDto.setMessage("Category with identifier - " + identifier + " already exists");
            return categoryDto;
        }
        Category category = modelMapper.map(categoryDto, Category.class);
        categoryRepository.save(category);
        return categoryDto;
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingcategory = categoryRepository.findByIdentifier(identifier);
        if (existingcategory == null) {
            categoryDto.setMessage("Category with identifier - " + identifier + " not found");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        modelMapper.map(categoryDto, existingcategory);
        categoryRepository.save(existingcategory);
        return categoryDto;
    }

    @Override
    public void delete(String identifier) {
        categoryRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<CategoryDto> findAll() {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(categoryRepository.findAll(), listType);
    }

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        return modelMapper.map(categoryPage.getContent(), listType);
    }

    @Override
    public CategoryDto findByIdentifier(String identifier) {
        return modelMapper.map(categoryRepository.findByIdentifier(identifier), CategoryDto.class);
    }
}
