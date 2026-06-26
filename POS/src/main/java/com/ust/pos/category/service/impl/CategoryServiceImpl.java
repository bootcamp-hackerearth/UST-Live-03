package com.ust.pos.category.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.category.service.CategoryService;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl extends BaseService implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WsDto<CategoryDto> findAll(Pageable pageable) {

        Type type = new TypeToken<List<CategoryDto>>() {}.getType();
        Page<Category> page = categoryRepository.findByDeletedFalse(pageable);

        WsDto<CategoryDto> ws = new WsDto<>();
        ws.setDtoList(modelMapper.map(page.getContent(), type));
        ws.setTotalRecords(page.getTotalElements());
        ws.setTotalPages(page.getTotalPages());
        ws.setSizePerPage(pageable.getPageSize());
        ws.setPage(pageable.getPageNumber());

        return ws;
    }

    @Override
    public List<CategoryDto> findAllcontroller(Pageable pageable) {

        Type type = new TypeToken<List<CategoryDto>>() {}.getType();
        Page<Category> page = categoryRepository.findByDeletedFalse(pageable);
        return modelMapper.map(page.getContent(), type);
    }

    @Override
    public CategoryDto findByIdentifier(String identifier) {

        CategoryDto dto = new CategoryDto();
        Category category = categoryRepository.findByIdentifier(identifier).orElse(null);

        if (category == null || Boolean.TRUE.equals(category.getDeleted())) {
            dto.setSuccess(false);
            dto.setMessage("Category not found");
            return dto;
        }
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public CategoryDto save(CategoryDto dto) {

        CategoryDto response = new CategoryDto();

        if (categoryRepository.existsByIdentifier(dto.getIdentifier().trim())) {
            response.setSuccess(false);
            response.setMessage("Identifier already exists");
            return response;
        }

        Category category = new Category();
        category.setIdentifier(dto.getIdentifier());
        category.setName(dto.getName());
        category.setSuperCategoryIdentifier(dto.getSuperCategoryIdentifier());
        category.setStatus(true);
        setCreatedDetails(category);
        categoryRepository.save(category);
        response.setSuccess(true);
        response.setMessage("Category created successfully");
        return response;
    }

    @Override
    public CategoryDto update(CategoryDto dto) {

        CategoryDto response = new CategoryDto();
        Category category = categoryRepository.findByIdentifier(dto.getIdentifier()).orElse(null);

        if (category == null || Boolean.TRUE.equals(category.getDeleted())) {
            response.setSuccess(false);
            response.setMessage("Category not found");
            return response;
        }

        category.setName(dto.getName());
        String superCat = dto.getSuperCategoryIdentifier();
        category.setSuperCategoryIdentifier(
                (superCat == null || superCat.trim().isEmpty()) ? null : superCat);
        setModifiedDetails(category);
        categoryRepository.save(category);
        response.setSuccess(true);
        response.setMessage("Category updated successfully");

        return response;
    }

    @Override
    public void delete(String identifier) {

        Category category = categoryRepository.findByIdentifier(identifier).orElse(null);

        if (category == null) return;

        category.setDeleted(true);
        setModifiedDetails(category);
        categoryRepository.save(category);
    }

    @Override
    public List<CategoryDto> findSuperCategories() {

        List<Category> list = categoryRepository.findByDeletedFalse();
        List<CategoryDto> result = new ArrayList<>();

        for (Category c : list) {
            if (c.getSuperCategoryIdentifier() == null
                    || c.getSuperCategoryIdentifier().trim().isEmpty()) {
                result.add(modelMapper.map(c, CategoryDto.class));
            }
        }

        return result;
    }

    @Override
    public List<CategoryDto> findLeafCategories() {

        List<Category> list = categoryRepository.findByDeletedFalse();
        List<CategoryDto> result = new ArrayList<>();

        for (Category c : list) {
            boolean isParent = false;
            for (Category other : list) {
                if (c.getIdentifier().equals(other.getSuperCategoryIdentifier())) {
                    isParent = true;
                    break;
                }
            }
            if (!isParent) {
                result.add(modelMapper.map(c, CategoryDto.class));
            }
        }
        return result;
    }

    @Override
    public List<CategoryDto> findChildCategories() {

        List<Category> list = categoryRepository.findByDeletedFalse();
        List<CategoryDto> result = new ArrayList<>();

        for (Category c : list) {
            if (c.getSuperCategoryIdentifier() != null
                    && !c.getSuperCategoryIdentifier().trim().isEmpty()) {
                result.add(modelMapper.map(c, CategoryDto.class));
            }
        }
        return result;
    }
}