package com.ust.pos.category.service.impl;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class CategoryServiceImpl extends CommonService implements CategoryService {

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
            if (existingCategory.isDeleted()) {
                categoryDto.setMessage("Category with identifier - " + identifier + "has been soft deleted.(Rollback by changing status");
                categoryDto.setSuccess(false);
                return categoryDto;
            }
            categoryDto.setMessage("Category with identifier - " + identifier + " already exists");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        Category category = modelMapper.map(categoryDto, Category.class);
        setAuditFields(category, true);
        categoryRepository.save(category);
        return categoryDto;
    }

    @Override
    public CategoryDto findById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id " + id));

        return modelMapper.map(category, CategoryDto.class);

    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        Category existing = categoryRepository.findById(categoryDto.getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        existing.setIdentifier(categoryDto.getIdentifier());
        existing.setSupercategory(categoryDto.getSupercategory());
        setAuditFields(existing, false);
        Category updatedCategory = categoryRepository.save(existing);
        return modelMapper.map(updatedCategory, CategoryDto.class);
    }

    @Override
    public WsDto<CategoryDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        Page<Category> categoryPage = categoryRepository.findByDeletedFalse(pageable);
        WsDto<CategoryDto> categoryWsDto = new WsDto<>();
        categoryWsDto.setDtoList(modelMapper.map(categoryPage.getContent(), listType));
        categoryWsDto.setTotalRecords(categoryPage.getTotalElements());
        categoryWsDto.setTotalPages(categoryPage.getTotalPages());
        categoryWsDto.setSizePerPage(pageable.getPageSize());
        categoryWsDto.setPage(pageable.getPageNumber());

        return categoryWsDto;
    }

    @Override
    public WsDto<CategoryDto> findAll(Specification<Category> spec, Pageable pageable) {
        Type listType = new TypeToken<List<CategoryDto>>() {}.getType();
        Page<Category> page = categoryRepository.findAll(spec, pageable);
        WsDto<CategoryDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public void delete(String identifier) {
        Category category = categoryRepository.findByIdentifier(identifier);
        softDelete(category);
        setAuditFields(category, false);
        categoryRepository.save(category);
    }

    @Override
    public List<CategoryDto> findSubCategories() {
        return categoryRepository.findByStatusTrueAndSupercategoryIsNot("").stream()
                .map(cat -> modelMapper.map(cat, CategoryDto.class))
                .toList();
    }

    @Override
    public CategoryDto changeCategoryStatus(String identifier, boolean status) {
        Category category = categoryRepository.findByIdentifier(identifier);
        if (category == null) {
            return null;
        }
        category.setStatus(status);
        categoryRepository.save(category);
        return modelMapper.map(category, CategoryDto.class);
    }
}