package com.ust.pos.category.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl extends BaseService implements CategoryService {

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
        Category existingCategory = categoryRepository.findByIdentifier(identifier);
        if (existingCategory != null) {
            categoryDto.setMessage(CATEGORY_WITH_IDENTIFIER + identifier + " already exists");
            if(existingCategory.isDeleted()){
                categoryDto.setMessage(CATEGORY_WITH_IDENTIFIER + identifier + " was deleted , Please Contact the Administrator to add.");
            }
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        Category category = modelMapper.map(categoryDto, Category.class);
        setCreatedDetails(category);
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
        setModifiedDetails(existingCategory);
        categoryRepository.save(existingCategory);
        return categoryDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Category category = categoryRepository.findByIdentifier(identifier);
        softDelete(category);
        setModifiedDetails(category);
    }

    @Override
    public WsDto<CategoryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        Page<Category> categoryPage = categoryRepository.findByIsDeletedFalse(pageable);

        WsDto<CategoryDto> categoryWsDto = new WsDto<>();
        categoryWsDto.setDtoList(modelMapper.map(categoryPage.getContent(), listType));
        categoryWsDto.setTotalRecords(categoryPage.getTotalElements());
        categoryWsDto.setTotalPages(categoryPage.getTotalPages());
        categoryWsDto.setSizePerPage(pageable.getPageSize());
        categoryWsDto.setPage(pageable.getPageNumber());

        return categoryWsDto;
    }

    @Override
    public CategoryDto findByIdentifier(String identifier) {
        Category category = categoryRepository.findByIdentifierAndIsDeletedFalse(identifier);
        if (category == null) {
            throw new ResourceNotFoundException("Category with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public List<CategoryDto> findAllWithSuperCategoryEmpty() {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        List<CategoryDto> allCategories = modelMapper.map(categoryRepository.findByIsDeletedFalse(), listType);
        List<CategoryDto> notEmptyCategories = new ArrayList<>();
        for (CategoryDto category : allCategories) {
            if (!category.getSuperCategory().isEmpty()) {
                notEmptyCategories.add(category);
            }
        }
        return notEmptyCategories;
    }

    @Override
    public WsDto<CategoryDto> findAll(Specification<Category> example, Pageable pageable) {

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        Page<Category> page = categoryRepository.findAll(example, pageable);

        WsDto<CategoryDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

}
