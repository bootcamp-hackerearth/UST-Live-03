package com.ust.pos.category.impl;

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
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryDto save(CategoryDto categoryDto) {

        if (categoryRepository.findByIdentifier(categoryDto.getIdentifier()) != null) {
            categoryDto.setSuccess(false);
            categoryDto.setMessage("Category already exists");
            return categoryDto;
        }

        Category category = new Category();
        category.setIdentifier(categoryDto.getIdentifier());

        if (categoryDto.getSuperCategory() == null || categoryDto.getSuperCategory().trim().isEmpty()) {
            category.setSuperCategory(null);
        } else {
            category.setSuperCategory(categoryDto.getSuperCategory());
        }

        categoryRepository.save(category);
        return categoryDto;
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {

        Category existing = categoryRepository.findByIdentifier(categoryDto.getIdentifier());

        if (existing == null) {
            categoryDto.setSuccess(false);
            categoryDto.setMessage("Category with identifier - " + categoryDto.getIdentifier() + " not found");
            return categoryDto;
        }


        if (!existing.getIdentifier().equalsIgnoreCase(categoryDto.getIdentifier()) &&
                categoryRepository.findByIdentifier(categoryDto.getIdentifier()) != null) {
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

        categoryRepository.save(existing);
        return categoryDto;
    }

    @Override
    @Transactional
    public void deleteByIdentifier(String identifier) {

        if (categoryRepository.existsBySuperCategory(identifier)) {
            throw new IllegalStateException("Cannot delete category. It is used as a super category.");
        }

        categoryRepository.deleteByIdentifier(identifier);
    }

    @Override
    public CategoryDto findByIdentifier(String identifier) {

        return modelMapper.map(categoryRepository.findByIdentifier(identifier), CategoryDto.class);
    }

    @Override
    public WsDto<CategoryDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        WsDto<CategoryDto> categoryWsDto = new WsDto<>();
        categoryWsDto.setDtoList(modelMapper.map(categoryPage.getContent(), listType));
        categoryWsDto.setTotalRecords(categoryPage.getTotalElements());
        categoryWsDto.setTotalPages(categoryPage.getTotalPages());
        categoryWsDto.setSizePerPage(pageable.getPageSize());
        categoryWsDto.setPage(pageable.getPageNumber());

        return categoryWsDto;
    }

    @Override
    public List<CategoryDto> findChildCategories() {

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();

        return modelMapper.map(categoryRepository.findBySuperCategoryIsNotNull(), listType);

    }

    @Override
    public CategoryDto toggleStatus(String identifier) {

        Category category = categoryRepository.findByIdentifier(identifier);
        category.setStatus(!category.isStatus());
        categoryRepository.save(category);
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public List<CategoryDto> findIfTrue() {

        return categoryRepository.findByStatusTrue().stream()
                .map(category -> modelMapper.map(category, CategoryDto.class)).toList();
    }

    @Override
    public List<CategoryDto> findBySuperCategoryNotNull() {

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(categoryRepository.findByStatusTrueAndSuperCategoryIsNot(""), listType);
    }

}