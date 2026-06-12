package com.ust.pos.category.service.impl;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryDto findByIdentifier(String identifier) {

        Category category = categoryRepository.findByIdentifier(identifier);

        if (category == null) {
            return null;
        }

        return modelMapper.map(category, CategoryDto.class);
    }

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
    @Transactional
    public void delete(String identifier) {

        categoryRepository.deleteByIdentifier(identifier);
    }

    @Override
    public WsDto<CategoryDto> findAll(Pageable pageable) {

        Page<Category> categoryPage = categoryRepository.findAll(pageable);

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
    public WsDto<CategoryDto> findAllCategoriesWithNoSuper(Pageable pageable) {

        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        List<Category> filteredCategories = categoryPage.getContent()
                .stream()
                .filter(category -> category.getSuperCategory() == null
                        || category.getSuperCategory().isEmpty())
                .toList();

        WsDto<CategoryDto> categoryDto = new WsDto<>();

        List<CategoryDto> categoryDtos = filteredCategories
                .stream()
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .toList();

        categoryDto.setContent(categoryDtos);
        categoryDto.setPage(categoryPage.getNumber());
        categoryDto.setSizePerPage(categoryPage.getSize());

        categoryDto.setTotalPages(1);
        categoryDto.setTotalRecords(filteredCategories.size());

        return categoryDto;


    }
}



