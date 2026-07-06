package com.ust.pos.category.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
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
public class CategoryServiceImpl extends BaseService implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WsDto<CategoryDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();

        Page<Category> categoryPage = categoryRepository.findByIsDeletedFalse(pageable);

        WsDto<CategoryDto> dto = new WsDto<>();

        dto.setContent(modelMapper.map(categoryPage.getContent(), listType));
        dto.setTotalRecords(categoryPage.getTotalElements());
        dto.setTotalPages(categoryPage.getTotalPages());
        dto.setSizePerPage(pageable.getPageSize());
        dto.setPage(pageable.getPageNumber());

        return dto;
    }

    @Override
    public CategoryDto findByIdentifier(String identifier) {
        return modelMapper.map(categoryRepository.findByIdentifier(identifier), CategoryDto.class);
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingCategory = categoryRepository.findByIdentifier(identifier);

        if (existingCategory != null) {
            categoryDto.setMessage(
                    existingCategory.isDeleted()
                            ? "Category - " + identifier + " already exists but was deleted, Please contact Administrator"
                            : "Category - " + identifier + " already exists"
            );

            categoryDto.setSuccess(false);
            return categoryDto;
        }
        Category category = modelMapper.map(categoryDto, Category.class);
        setCreatedDetails(category);
        categoryRepository.save(category);
        return categoryDto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        Category category = categoryRepository.findByIdentifier(identifier);
        setModifiedDetails(category);
        softDelete(category);
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingCategory = categoryRepository.findByIdentifier(identifier);
        if (existingCategory == null) {
            categoryDto.setMessage("Category with category - " + identifier + " not found");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        modelMapper.map(categoryDto, existingCategory);
        setModifiedDetails(existingCategory);
        categoryRepository.save(existingCategory);
        return categoryDto;
    }

    @Override
    public List<CategoryDto> findAllCategoriesWithNoSuper() {

        List<Category> categoryList = categoryRepository.findAll();

        List<Category> filteredCategories = categoryList.stream()
                .filter(category ->
                        category.getSuperCategory() != null &&
                                !category.getSuperCategory().isEmpty()
                )
                .toList();

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();

        return modelMapper.map(filteredCategories, listType);
    }

    @Override
    public void toggleStatus(String identifier) {

        Category category = categoryRepository
                .findByIdentifier(identifier);

        if (category != null) {
            category.setStatus(!category.isStatus());
            categoryRepository.save(category);
        }

    }

    @Override
    public WsDto<CategoryDto> findAll(Specification<Category> example, Pageable pageable) {

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        Page<Category> page = categoryRepository.findAll(example, pageable);

        WsDto<CategoryDto> wsDto = new WsDto<>();
        wsDto.setContent(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}
