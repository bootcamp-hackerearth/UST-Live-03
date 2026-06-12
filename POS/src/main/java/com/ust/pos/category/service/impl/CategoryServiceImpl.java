package com.ust.pos.category.service.impl;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryDto findByIdentifier(String identifier) {
        return modelMapper.map(categoryRepository.findByIdentifier(identifier), CategoryDto.class);
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingCategory = categoryRepository.findByIdentifier(identifier);
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
        Category existingCategory = categoryRepository.findByIdentifier(identifier);
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
    public boolean delete(String identifier) {
        categoryRepository.deleteByIdentifier(identifier);
        return true;
    }

    @Override
    public WsDto<CategoryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        WsDto<CategoryDto> categoryDtoWsDto = new WsDto<>();
        categoryDtoWsDto.setDtoList(modelMapper.map(categoryPage.getContent(), listType));
        categoryDtoWsDto.setTotalRecords(categoryPage.getTotalElements());
        categoryDtoWsDto.setTotalPages(categoryPage.getTotalPages());
        categoryDtoWsDto.setSizePerPage(pageable.getPageSize());
        categoryDtoWsDto.setPage(pageable.getPageNumber());
        return categoryDtoWsDto;
    }

    @Override
    public List<CategoryDto> findBySuperCategoryNotNull() {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(categoryRepository.findByStatusTrueAndSuperCategoryIsNot(""), listType);
    }

    @Override
    public CategoryDto toggleStatus(String identifier) {
        Category category = categoryRepository.findByIdentifier(identifier);
        category.setStatus(!category.isStatus());
        categoryRepository.save(category);
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public List<CategoryDto> findIfTrue() {
        Type listType = new TypeToken<List<ShelfsDto>>() {
        }.getType();
        return modelMapper.map(categoryRepository.findByStatusIsTrue(), listType);
    }

    @Override
    public List<CategoryDto> findAllActive() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDto> activeAssignableCategories = new ArrayList<>();
        for (Category category : categories) {
            String superCategory = category.getSuperCategory();
            if (superCategory != null && !superCategory.trim().isEmpty()
                    && category.isStatus()) {
                activeAssignableCategories.add(
                        modelMapper.map(category, CategoryDto.class)
                );
            }
        }
        return activeAssignableCategories;
    }
}