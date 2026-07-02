package com.ust.pos.category.service.impl;

import com.ust.pos.api.BaseService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.model.Brand;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl extends BaseService implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
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
        if (categoryDto.getSuperCategory().isEmpty()) {
            categoryDto.setSuperCategory(null);
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
            categoryDto.setMessage("Category with identifier - " + identifier + " is not found");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        if (categoryDto.getSuperCategory().isEmpty()) {
            categoryDto.setSuperCategory(null);
        }
        Category category = modelMapper.map(categoryDto, Category.class);
        categoryRepository.save(category);
        return categoryDto;
    }

    @Override
    public void delete(String identifier) {
        Category category = categoryRepository.findByIdentifierAndDeletedFalse(identifier);
        if (category != null) {
            category.setDeleted(true);
            categoryRepository.save(category);
        }
    }

    @Override
    public List<CategoryDto> findAll() {
        Type listOfType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(categoryRepository.findByDeletedFalse(), listOfType);
    }

    @Override
    public List<CategoryDto> findAllWithoutNull() {
        Type listOfType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        List<CategoryDto> categoryDtos = modelMapper.map(categoryRepository.findAll(), listOfType);
        return categoryDtos.stream().filter(c -> c.getSuperCategory() != null)
                .toList();
    }

    @Override
    public CategoryDto findByIdentifier(String identifier) {
        return modelMapper.map(categoryRepository.findByIdentifierAndDeletedFalse(identifier), CategoryDto.class);
    }

    @Override
    public Page<CategoryDto> findAll(String search, Pageable pageable) {
        Page<Category> brands;

        if (search != null && !search.trim().isEmpty()) {
            Specification<Category> specification = buildGlobalSearchSpec(Category.class, search);
            brands = categoryRepository.findAll(specification, pageable);
        } else {
            brands = categoryRepository.findByDeletedFalse(pageable);
        }

        return brands.map(category -> modelMapper.map(category, CategoryDto.class));
    }
}
