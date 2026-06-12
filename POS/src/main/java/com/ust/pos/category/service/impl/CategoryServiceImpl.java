package com.ust.pos.category.service.impl;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.*;
import com.ust.pos.model.*;
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
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        String identifier =categoryDto .getIdentifier();
        Category existingProduct =categoryRepository.findByIdentifier(identifier);
        if (existingProduct != null) {
           categoryDto .setMessage("Product with identifier - " + identifier + " already exists");
           categoryDto .setSuccess(false);
            return categoryDto ;
        }
        Category category= modelMapper.map(categoryDto, Category.class);
       categoryRepository.save(category);
        return categoryDto ;
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingRole = categoryRepository.findByIdentifier(identifier);
        if (existingRole == null) {
            categoryDto.setMessage("Role with identifier - " + identifier + " not found");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        modelMapper.map(categoryDto, existingRole);
        categoryRepository.save(existingRole);
        return categoryDto;
    }

    @Override
    @Transactional
    public boolean delete(String identifier) {
        categoryRepository.deleteByIdentifier(identifier);
        return true;
    }

    @Override
    public PageDto<CategoryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        PageDto<CategoryDto> pageDto = new PageDto<>();
        pageDto.setDtoList(modelMapper.map(categoryPage.getContent(), listType));
        pageDto.setTotalRecords(categoryPage.getTotalElements());
        pageDto.setTotalPages(categoryPage.getTotalPages());
        pageDto.setSizePerPage(pageable.getPageSize());
        pageDto.setPage(pageable.getPageNumber());
        return pageDto;
    }

    @Override
    public CategoryDto findByIdentifier(String identifier) {
        return modelMapper.map(categoryRepository.findByIdentifier(identifier), CategoryDto.class);
    }

    @Override
    public List<CategoryDto> findBySubCategory() {
        return categoryRepository.findBySupercategoryIsNot("").stream()
                .map(cat -> modelMapper.map(cat, CategoryDto.class))
                .toList();
    }

    @Override
    public void toggleStatus(String identifier) {
        Category category = categoryRepository.findByIdentifier(identifier);
        if (category != null) {
            boolean currentStatus = Boolean.TRUE.equals(category.getStatus());
            category.setStatus(!currentStatus);
            categoryRepository.save(category);
        }
    }

    @Override
    public List<CategoryDto> findActiveCategories() {
        Type listType = new TypeToken<List<RoleDto>>() {}.getType();
        return modelMapper.map(categoryRepository.findByStatusTrue(),listType);
    }
}
