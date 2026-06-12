package com.ust.pos.category.service.impl;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.PaginatedResponseDto;
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
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryDto save(CategoryDto categoryDto) {

        String identifier = categoryDto.getIdentifier();
        Category existingCategory = categoryRepository.findByIdentifier(identifier);
        if (existingCategory != null) {
            categoryDto.setMessage("Category with identifier - " + identifier + " already exists");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        Category category = modelMapper.map(categoryDto, Category.class);
        categoryRepository.save(category);
        categoryDto.setSuccess(true);
        return categoryDto;
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingCategory = categoryRepository.findByIdentifier(identifier);
        if (existingCategory == null) {
            categoryDto.setMessage("Category with identifier - " + identifier + " not found");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        modelMapper.map(categoryDto, existingCategory);
        categoryRepository.save(existingCategory);
        return categoryDto;
    }

    @Override
    public void delete(String identifier) {

        if (categoryRepository.existsBySuperCategory(identifier)) {
            throw new IllegalStateException("Cannot delete category because it is used as a super category.");
        }
        categoryRepository.deleteByIdentifier(identifier);
    }

    @Override
    public PaginatedResponseDto<CategoryDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        List<CategoryDto> items = modelMapper.map(categoryPage.getContent(), listType);

        PaginatedResponseDto<CategoryDto> response = new PaginatedResponseDto<>();
        response.setItems(items);
        response.setTotalRecords(categoryPage.getTotalElements());
        response.setTotalPages(categoryPage.getTotalPages());
        response.setSizePerPage(pageable.getPageSize());
        response.setPage(pageable.getPageNumber());

        return response;
    }

    @Override
    public CategoryDto findByIdentifier(String identifier) {
        return modelMapper.map(categoryRepository.findByIdentifier(identifier), CategoryDto.class);
    }

    @Override
    public List<CategoryDto> findAllActive() {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(categoryRepository.findByStatus(true), listType);
    }

    @Override
    public void changeStatus(String identifier, boolean status) {
        Category category = categoryRepository.findByIdentifier(identifier);
        category.setStatus(status);
        categoryRepository.save(category);
    }
}
