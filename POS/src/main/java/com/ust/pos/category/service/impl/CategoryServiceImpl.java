package com.ust.pos.category.service.impl;

import com.ust.pos.base.service.BaseService; // 1. Imported the BaseService package
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Category;
import com.ust.pos.modell.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends BaseService implements CategoryService { // 2. Extended BaseService

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingCategory = categoryRepository.findByIdentifier(identifier);

        if (existingCategory != null) {

            if (Boolean.TRUE.equals(existingCategory.getDeleted())) {
                categoryDto.setMessage("Category with identifier - " + identifier + " was deleted and cannot be created again.");
                categoryDto.setSuccess(false);
                return categoryDto;
            }

            categoryDto.setMessage("Category with identifier - " + identifier + " already exists");
            categoryDto.setSuccess(false);
            return categoryDto;
        }

        Category category = new Category();
        category.setIdentifier(identifier);

        if (categoryDto.getSuperCategory() == null ||
                categoryDto.getSuperCategory().trim().isEmpty()) {
            category.setSuperCategory(null);
        } else {
            category.setSuperCategory(categoryDto.getSuperCategory());
        }

        setCreatedDetails(category);
        categoryRepository.save(category);
        categoryDto.setMessage("Category created successfully");
        categoryDto.setSuccess(true);
        return categoryDto;
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        Optional<Category> optional = categoryRepository.findById(categoryDto.getId());

        if (optional.isEmpty()) {
            categoryDto.setSuccess(false);
            categoryDto.setMessage("Category not found");
            return categoryDto;
        }

        Category existing = optional.get();

        if (!existing.getIdentifier().equalsIgnoreCase(categoryDto.getIdentifier()) &&
                categoryRepository.findByIdentifierAndDeletedFalse(categoryDto.getIdentifier()) != null) {

            categoryDto.setSuccess(false);
            categoryDto.setMessage("Category already exists");
            return categoryDto;
        }

        existing.setIdentifier(categoryDto.getIdentifier());

        if (categoryDto.getSuperCategory() == null || categoryDto.getSuperCategory().trim().isEmpty()) {
            existing.setSuperCategory(null);
        } else {
            existing.setSuperCategory(categoryDto.getSuperCategory());
        }

        setModifiedDetails(existing);
        categoryRepository.save(existing);
        return categoryDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {

        if (categoryRepository
                .existsBySuperCategoryAndDeletedFalse(identifier)) {

            throw new IllegalStateException(
                    "Cannot delete category. It is used as a super category."
            );
        }

        Category category =
                categoryRepository
                        .findByIdentifierAndDeletedFalse(
                                identifier
                        );

        if (category != null) {

            softDelete(category);
            setModifiedDetails(category);
            categoryRepository.save(category);
        }
    }


    @Override
    public CategoryDto findByIdentifier(String identifier) {
        return modelMapper.map(categoryRepository.findByIdentifierAndDeletedFalse(identifier), CategoryDto.class);
    }

    @Override
    public WsDto<CategoryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CategoryDto>>() {}.getType();
        Page<Category> categoryPage = categoryRepository.findAllByDeletedFalse(pageable);
        WsDto<CategoryDto> categoryWsDto = new WsDto<>();
        categoryWsDto.setDtoList(modelMapper.map(categoryPage.getContent(), listType));
        categoryWsDto.setTotalRecords(categoryPage.getTotalElements());
        categoryWsDto.setTotalPage(categoryPage.getTotalPages());
        categoryWsDto.setSizePerPage(pageable.getPageSize());
        categoryWsDto.setPage(pageable.getPageNumber());
        return categoryWsDto;
    }

    @Override
    public List<CategoryDto> findChildCategories() {
        Type listType = new TypeToken<List<CategoryDto>>() {}.getType();
        return modelMapper.map(categoryRepository.findBySuperCategoryIsNotNull(), listType);
    }

    @Override
    @Transactional
    public CategoryDto toggleStatus(String identifier) {
        Category category = categoryRepository.findByIdentifierAndDeletedFalse(identifier);

        if (category == null) {
            throw new IllegalArgumentException("Product not found with identifier: " + identifier);
        }

        Boolean currentStatus = category.getStatus();
        category.setStatus(currentStatus == null ? Boolean.TRUE : !currentStatus);
        setModifiedDetails(category);
        Category saved = categoryRepository.save(category);
        return modelMapper.map(saved, CategoryDto.class);
    }

    @Override
    public List<CategoryDto> findAllActive() {
        return categoryRepository.findByStatusTrueAndDeletedFalse()
                .stream()
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .toList();
    }
}