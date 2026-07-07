package com.ust.pos.category.service.impl;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.common.CommonService;
import com.ust.pos.dto.*;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
@Service
public class CategoryServiceImpl extends CommonService implements CategoryService {

    public static final String CATEGORY_WITH_IDENTIFIER = "Category with identifier - ";
    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {

        String identifier = categoryDto.getIdentifier();

        Category existingCategory =
                categoryRepository.findByIdentifier(identifier);

        if (existingCategory != null) {

            if (Boolean.TRUE.equals(existingCategory.getDeleted())) {

                categoryDto.setMessage(
                        CATEGORY_WITH_IDENTIFIER + identifier +
                                " has been soft deleted. Restore it by changing status."
                );

                categoryDto.setSuccess(false);
                return categoryDto;
            }

            categoryDto.setMessage(
                    CATEGORY_WITH_IDENTIFIER + identifier +
                            " already exists"
            );

            categoryDto.setSuccess(false);
            return categoryDto;
        }

        Category category = modelMapper.map(categoryDto, Category.class);

        category.setDeleted(false);
        category.setStatus(true);

        setAuditFields(category, true);

        categoryRepository.save(category);

        return categoryDto;
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
        setAuditFields(existingCategory,false);
        categoryRepository.save(existingCategory);
        return categoryDto;
    }

    @Override
    @Transactional
    public boolean delete(String identifier) {

        Category category = categoryRepository.findByIdentifier(identifier);

        if (category == null) {
            return false;
        }

        softDelete(category);
        setAuditFields(category, false);

        categoryRepository.save(category);

        return true;
    }

    @Override
    public PageDto<CategoryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        Page<Category> categoryPage = categoryRepository.findByDeletedFalse(pageable);
        PageDto<CategoryDto> pageDto = new PageDto<>();
        pageDto.setDtoList(modelMapper.map(categoryPage.getContent(), listType));
        pageDto.setTotalRecords(categoryPage.getTotalElements());
        pageDto.setTotalPages(categoryPage.getTotalPages());
        pageDto.setSizePerPage(pageable.getPageSize());
        pageDto.setPage(pageable.getPageNumber());
        return pageDto;
    }


    @Override
    public PageDto<CategoryDto> findAll(Specification<Category> spec, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        Page<Category> categoryPage = categoryRepository.findAll(spec, pageable);
        PageDto<CategoryDto> pageDto = new PageDto<>();
        pageDto.setDtoList(modelMapper.map(categoryPage.getContent(), listType));
        pageDto.setTotalRecords(categoryPage.getTotalElements());
        pageDto.setTotalPages(categoryPage.getTotalPages());
        pageDto.setSizePerPage(pageable.getPageSize());
        pageDto.setPage(pageable.getPageNumber());
        pageDto.setKeyword(keyword);
        return pageDto;
    }

    @Override
    public CategoryDto findByIdentifier(String identifier) {
        Category category=categoryRepository.findByIdentifier(identifier);
        if (category == null) {
            throw new ResourceNotFoundException("Category with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(category, CategoryDto.class);
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
        Type listType = new TypeToken<List<CategoryDto>>() {}.getType();
        return modelMapper.map(categoryRepository.findByStatusTrue(),listType);
    }
}
