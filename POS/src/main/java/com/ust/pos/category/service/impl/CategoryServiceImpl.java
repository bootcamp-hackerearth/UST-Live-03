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

@Transactional
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

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

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        if (StringUtils.isEmpty(categoryDto.getSuperCategory())) {
            categoryDto.setSuperCategory(null);
        }
        String identifier = categoryDto.getIdentifier();
        Category category = categoryRepository.findByIdentifier(identifier);
        if (category == null) {
            categoryRepository.save(modelMapper.map(categoryDto, Category.class));
            categoryDto.setMessage("Successfully added the category");
            categoryDto.setSuccess(true);
        } else {
            categoryDto.setMessage("Category " + identifier + " already exists");
            categoryDto.setSuccess(false);
        }
        return categoryDto;
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingCategory = categoryRepository.findByIdentifier(identifier);
        if (existingCategory == null) {
            categoryDto.setMessage("Category not found");
            categoryDto.setSuccess(false);
        } else {
            modelMapper.map(categoryDto, existingCategory);
            categoryRepository.save(existingCategory);
            categoryDto.setMessage("Category updated successfully");
            categoryDto.setSuccess(true);
        }
        return categoryDto;
    }

    @Override
    public List<CategoryDto> findBySuperCategoryIsNotNullAndStatusTrue() {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(categoryRepository.findBySuperCategoryIsNotNullAndStatusTrue(), listType);
    }

    @Override
    @Transactional
    public CategoryDto updateStatus(String identifier, boolean status) {
        CategoryDto response = new CategoryDto();

        Category category = categoryRepository.findByIdentifier(identifier);
        if (category == null) {
            response.setSuccess(false);
            response.setMessage("Category not found");
            return response;
        }

        category.setStatus(status);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");

        return response;
    }

    @Override
    public void delete(String identifier) {
        categoryRepository.deleteByIdentifier(identifier);
    }
}
