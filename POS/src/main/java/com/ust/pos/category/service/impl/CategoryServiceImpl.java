package com.ust.pos.category.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Transactional
@Service
public class CategoryServiceImpl extends BaseService implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            ModelMapper modelMapper) {

        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public PaginationResponseDto<CategoryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CategoryDto>>() {}.getType();
        PaginationResponseDto<CategoryDto> response = new PaginationResponseDto<>();
        if (pageable == null) {
            List<Category> categorys = categoryRepository.findAll();
            response.setDtoList(modelMapper.map(categorys, listType));
            response.setTotalRecords(categorys.size());
            response.setTotalPages(1);
            response.setSizePerPage(categorys.size());
            response.setPage(0);
        } else {
            Page<Category> categoryPage = categoryRepository.findByDeletedFalse(pageable);
            response.setDtoList(modelMapper.map(categoryPage.getContent(), listType));
            response.setTotalRecords(categoryPage.getTotalElements());
            response.setTotalPages(categoryPage.getTotalPages());
            response.setSizePerPage(pageable.getPageSize());
            response.setPage(pageable.getPageNumber());
        }
        return response;
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
        if (category != null) {
            if (category.isDeleted()) {
                categoryDto.setMessage("Category with identifier - " + identifier + " has been soft deleted.");
                categoryDto.setSuccess(false);
                return categoryDto;
            }
            categoryDto.setMessage("Category " + identifier + " already exists");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        Category newCategory = modelMapper.map(categoryDto, Category.class);
        setCreatedDetails(newCategory);
        categoryRepository.save(newCategory);
        categoryDto.setMessage("Successfully added the category");
        categoryDto.setSuccess(true);
        return categoryDto;
    }

    @Override
    public List<CategoryDto> findSubCategories() {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(categoryRepository.findBySuperCategoryIsNotNull(), listType);
    }

    @Override
    public void delete(String identifier) {
        Category category = categoryRepository.findByIdentifier(identifier);
        if (category == null) {
            throw new EntityNotFoundException("Category not found");
        }
        softDelete(category);
        setModifiedDetails(category);
        categoryRepository.save(category);
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
        setModifiedDetails(existingCategory);
        categoryRepository.save(existingCategory);
        return categoryDto;
    }

    @Override
    public CategoryDto toggleStatus(String identifier, boolean status) {
        Category category = categoryRepository.findByIdentifier(identifier);
        if (category != null) {
            category.setStatus(!category.isStatus());
            setModifiedDetails(category);
            categoryRepository.save(category);
        }
        return modelMapper.map(category, CategoryDto.class);
    }
}