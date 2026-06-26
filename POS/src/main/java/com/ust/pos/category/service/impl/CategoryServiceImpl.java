package com.ust.pos.category.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class CategoryServiceImpl extends BaseService implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        if (StringUtils.isEmpty(categoryDto.getSuperCategory())) {
            categoryDto.setSuperCategory(null);
        }
        String identifier = categoryDto.getIdentifier().trim();
        Category existingCategory = categoryRepository.findByIdentifier(identifier);
        if (existingCategory != null) {
            if (existingCategory.isDeleted()) {
                categoryDto.setMessage("Category with identifier " + identifier + " has been soft deleted. (Rollback by changing status)");
                categoryDto.setSuccess(false);
                return categoryDto;
            }
            categoryDto.setMessage("Category with identifier - " + identifier + " already exists");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        Category category = modelMapper.map(categoryDto, Category.class);
        setCreatedDetails(category);
        categoryRepository.save(category);
        categoryDto.setSuccess(true);
        categoryDto.setMessage("Category created successfully");
        return categoryDto;
    }

    @Override
    public WsDto<CategoryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        WsDto<CategoryDto> wsDto = new WsDto<>();
        if (pageable == null) {
            List<CategoryDto> categoryDtoList = modelMapper.map(categoryRepository.findByDeletedFalse(pageable), listType);
            wsDto.setDtoList(categoryDtoList);
            wsDto.setTotalRecords(categoryDtoList.size());
            return wsDto;
        }
        Page<Category> categoryPage = categoryRepository.findByDeletedFalse(pageable);
        wsDto.setDtoList(modelMapper.map(categoryPage.getContent(), listType));
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setTotalPages(categoryPage.getTotalPages());
        wsDto.setTotalRecords(categoryPage.getTotalElements());
        return wsDto;
    }

    @Override
    public CategoryDto findByIdentifier(String identifier) {
        return modelMapper.map(categoryRepository.findByIdentifier(identifier), CategoryDto.class);
    }

    @Override
    @Transactional
    public void deleteByIdentifier(String identifier) {
        Category category = categoryRepository.findByIdentifier(identifier);
        softDelete(category);
        setModifiedDetails(category);
        categoryRepository.save(category);
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        Category existingCategory = categoryRepository.findByIdentifier(categoryDto.getIdentifier());
        if (existingCategory == null) {
            categoryDto.setMessage("Category with identifier - " + categoryDto.getIdentifier() + " not found");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        modelMapper.map(categoryDto, existingCategory);
        setModifiedDetails(existingCategory);
        categoryRepository.save(existingCategory);
        categoryDto.setSuccess(true);
        categoryDto.setMessage("Category updated successfully");
        return categoryDto;
    }

    @Override
    public List<CategoryDto> findSubCategories() {
        return categoryRepository.findBySuperCategoryNotNull().stream().map(category -> modelMapper.map(category, CategoryDto.class)).toList();
    }

}