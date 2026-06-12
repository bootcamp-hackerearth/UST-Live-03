package com.ust.pos.category.service.impl;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public WsDto<CategoryDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();

        Page<Category> categoryPage = categoryRepository.findAll(pageable);

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
            categoryDto.setMessage("Category - " + identifier + " already exists");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        Category category = modelMapper.map(categoryDto, Category.class);
        categoryRepository.save(category);
        return categoryDto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        categoryRepository.deleteByIdentifier(identifier);
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

        Type listType = new TypeToken<List<CategoryDto>>() {}.getType();

        return modelMapper.map(filteredCategories, listType);
    }
}
