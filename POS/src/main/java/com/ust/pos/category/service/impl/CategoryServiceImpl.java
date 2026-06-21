package com.ust.pos.category.service.impl;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.common.CommonService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class CategoryServiceImpl extends CommonService implements CategoryService {
    public static final String CATEGORY_WITH_IDENTIFIER = "Category with identifier - ";
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryDto findByIdentifier(String identifier) {
        Category category = categoryRepository.findByIdentifier(identifier);
        return category == null ? null : modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public CategoryDto save(CategoryDto dto) {

        Category existing = categoryRepository.findByIdentifier(dto.getIdentifier());
        if (existing != null) {
            if (existing.isDeleted()) {
                dto.setMessage(CATEGORY_WITH_IDENTIFIER + dto.getIdentifier() + " has been soft deleted.(Rollback by changing status");
                dto.setSuccess(false);
                return dto;
            }
            dto.setSuccess(false);
            dto.setMessage(CATEGORY_WITH_IDENTIFIER + dto.getIdentifier() + " already exists");
            return dto;
        }

        Category category = new Category();
        category.setIdentifier(dto.getIdentifier());
        category.setStatus(true);

        String superCategory = dto.getSuperCategory();

        if (superCategory == null || superCategory.trim().isEmpty()) {
            category.setSuperCategory(null);
        } else {
            category.setSuperCategory(superCategory.trim());
        }

        setAuditFields(category, true);
        categoryRepository.save(category);

        CategoryDto response = modelMapper.map(category, CategoryDto.class);
        response.setSuccess(true);
        return response;
    }

    @Override
    public CategoryDto update(CategoryDto dto) {

        Category category = categoryRepository.findByIdentifier(dto.getIdentifier());

        if (category == null) {
            dto.setSuccess(false);
            dto.setMessage(CATEGORY_WITH_IDENTIFIER + dto.getIdentifier() + " not found");
            return dto;
        }

        String superCategory = dto.getSuperCategory();

        if (superCategory == null || superCategory.trim().isEmpty()) {
            category.setSuperCategory(null);
        } else {
            category.setSuperCategory(superCategory.trim());
        }

        setAuditFields(category, false);
        categoryRepository.save(category);

        CategoryDto response = modelMapper.map(category, CategoryDto.class);
        response.setSuccess(true);
        return response;
    }

    @Override
    public boolean delete(String identifier) {
        Category category = categoryRepository.findByIdentifier(identifier);
        if (category == null) {
            return false;
        }
        softDelete(category);
        setAuditFields(category, false);
        categoryRepository.save(category);
        return true;
    }

    @Override
    public WsDto<CategoryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        Page<Category> categoryPage = categoryRepository.findByDeletedFalse(pageable);
        WsDto<CategoryDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(categoryPage.getContent(), listType));
        wsDto.setTotalRecords(categoryPage.getTotalElements());
        wsDto.setTotalPages(categoryPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }


    @Override
    public List<CategoryDto> findSuperCategories() {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();

        return modelMapper.map(categoryRepository.findBySuperCategoryIsNull(), listType);
    }


    @Override
    public CategoryDto toggleStatus(String identifier) {
        Category category = categoryRepository.findByIdentifier(identifier);
        if (category == null) return null;

        if (category.getSuperCategory() == null) {
            return modelMapper.map(category, CategoryDto.class);
        }

        category.setStatus(!category.isStatus());
        categoryRepository.save(category);

        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public List<CategoryDto> findIfTrue() {

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();

        List<Category> categories = categoryRepository.findBySuperCategoryIsNotNull().stream().filter(Category::isStatus).toList();

        return modelMapper.map(categories, listType);
    }
}