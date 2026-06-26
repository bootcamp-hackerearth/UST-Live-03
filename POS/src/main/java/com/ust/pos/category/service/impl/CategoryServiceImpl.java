package com.ust.pos.category.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl extends BaseService implements CategoryService {

    private static final String VALIDATION_MESSAGE = "Category with identifier - ";

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryDto findByIdentifier(String identifier) {

        Category category = categoryRepository.findByIdentifier(identifier);

        if (category == null) {
            return null;
        }

        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public List<CategoryDto> findActiveCategory() {

        List<Category> categoryList = categoryRepository.findByStatus(true);
        return categoryList.stream().map(category -> modelMapper.map(category, CategoryDto.class)).toList();
    }

    @Override
    public void toggleStatus(String identifier) {

        Category category = categoryRepository.findByIdentifier(identifier);

        if (category != null) {
            category.setStatus(!category.isStatus());
            categoryRepository.save(category);
        }
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {

        String identifier = categoryDto.getIdentifier();
        Category existingCategory = categoryRepository.findByIdentifier(identifier);

        if (existingCategory != null) {
            categoryDto.setMessage(
                    existingCategory.isDeleted()
                            ? VALIDATION_MESSAGE + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : VALIDATION_MESSAGE + identifier
                            + " already exists."
            );
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
            categoryDto.setMessage(VALIDATION_MESSAGE + identifier + " not found");
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
        setModifiedDetails(category);
        softDelete(category);
    }

    @Override
    public WsDto<CategoryDto> findAll(Pageable pageable) {

        Page<Category> categoryPage = categoryRepository.findByIsDeletedFalse(pageable);

        WsDto<CategoryDto> categoryDto = new WsDto<>();

        List<CategoryDto> categoryDtos = categoryPage.getContent()
                .stream()
                .map(product -> modelMapper.map(product, CategoryDto.class))
                .toList();

        categoryDto.setContent(categoryDtos);
        categoryDto.setPage(categoryPage.getNumber());
        categoryDto.setSizePerPage(categoryPage.getSize());
        categoryDto.setTotalPages(categoryPage.getTotalPages());
        categoryDto.setTotalRecords(categoryPage.getTotalElements());

        return categoryDto;
    }

    @Override
    public List<CategoryDto> findAllCategoriesWithNoSuper() {

        List<Category> categoryPage = categoryRepository.findByStatus(true);

        List<Category> filteredCategories = categoryPage
                .stream()
                .filter(category -> category.getSuperCategory() == null
                        || category.getSuperCategory().isEmpty())
                .toList();

        return filteredCategories
                .stream()
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .toList();
    }
}



