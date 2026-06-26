package com.ust.pos.category.service.impl;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.commonservice.CommonService;
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
public class CategoryServiceImpl extends CommonService implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingCategory =
                categoryRepository.findByIdentifierAndIsDeleteFalse(identifier);
        if (existingCategory != null) {
            categoryDto.setMessage("Category with identifier - " + identifier + " already exists");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        if (categoryDto.getSuperCategory().isEmpty()) {
            categoryDto.setSuperCategory(null);
        }
        Category category = modelMapper.map(categoryDto, Category.class);
        setAuditFields(category, true);
        categoryRepository.save(category);
        return categoryDto;
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingCategory =
                categoryRepository.findByIdentifierAndIsDeleteFalse(identifier);
        if (existingCategory == null) {
            categoryDto.setMessage("Category with identifier - " + identifier + " is not found");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        if (categoryDto.getSuperCategory().isEmpty()) {
            categoryDto.setSuperCategory(null);
        }
        modelMapper.map(categoryDto, existingCategory);
        setAuditFields(existingCategory, false);
        categoryRepository.save(existingCategory);
        return categoryDto;
    }

    @Override
    public void delete(String identifier) {
        Category category = categoryRepository.findByIdentifierAndIsDeleteFalse(identifier);
        if (category != null) {
            category.setDelete(true);
            setAuditFields(category, false);
            categoryRepository.save(category);
        }
    }

    @Override
    public List<CategoryDto> findAll() {
        Type listOfType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(categoryRepository.findByIsDeleteFalse(), listOfType);
    }

    @Override
    public List<CategoryDto> findAllWithoutNull(String identifier) {
        Type listOfType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        List<CategoryDto> categoryDtos = modelMapper.map(categoryRepository.findByIsDeleteFalse(), listOfType);
        return categoryDtos.stream().filter(c -> c.getSuperCategory() != null &&
                !c.getIdentifier().equals(identifier)).toList();
    }

    @Override
    public List<CategoryDto> findAllWithNull() {
        Type listOfType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        List<CategoryDto> categoryDtos = modelMapper.map(categoryRepository.findByIsDeleteFalse(), listOfType);
        return categoryDtos.stream().filter(c -> c.getSuperCategory() == null).toList();
    }

    @Override
    public CategoryDto findByIdentifier(String identifier) {
        return modelMapper.map(categoryRepository.
                findByIdentifierAndIsDeleteFalse(identifier), CategoryDto.class);
    }

    @Override
    public Page<CategoryDto> findAll(Pageable pageable, String search) {
        Page<Category> categories;
        if (search != null && !search.trim().isEmpty()) {
            categories = categoryRepository.
                    findByIdentifierContainingIgnoreCaseAndIsDeleteFalse(search, pageable);
        } else {
            categories = categoryRepository.findByIsDeleteFalse(pageable);
        }
        return categories.map(category -> modelMapper.map(category, CategoryDto.class));
    }
}
