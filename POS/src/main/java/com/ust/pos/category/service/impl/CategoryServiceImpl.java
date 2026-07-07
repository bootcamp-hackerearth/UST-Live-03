package com.ust.pos.category.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
@Transactional
public class CategoryServiceImpl extends BaseService implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingCategory = categoryRepository.findByIdentifier(identifier);
        if (existingCategory != null) {
            if (Boolean.TRUE.equals(existingCategory.getDeleted())) {
                categoryDto.setMessage("Category - " + identifier + " was deleted and cannot be recreated");
            } else {
                categoryDto.setMessage("Category with identifier - " + identifier + " already exists");
            }
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        Category category = modelMapper.map(categoryDto, Category.class);
        setCreatedDetails(category);
        categoryRepository.save(category);
        categoryDto.setSuccess(true);
        return categoryDto;
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingCategory = categoryRepository.findByIdentifierAndDeletedFalse(identifier);
        if (existingCategory == null) {
            categoryDto.setMessage("Category with identifier - " + identifier + " not found");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        modelMapper.map(categoryDto, existingCategory);
        setModifiedDetails(existingCategory);
        categoryRepository.save(existingCategory);
        categoryDto.setSuccess(true);
        return categoryDto;
    }

    @Override
    public void delete(String identifier) {
        if (categoryRepository.existsBySuperCategoryAndDeletedFalse(identifier)) {
            throw new IllegalArgumentException("Cannot delete category. It is used as a super category.");
        }
        Category category = categoryRepository.findByIdentifierAndDeletedFalse(identifier);
        if (category != null) {
            softDelete(category);
            setModifiedDetails(category);
            categoryRepository.save(category);
        }
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
    public CategoryDto findByIdentifier(String identifier) {
        Category category = categoryRepository.findByIdentifierAndDeletedFalse(identifier);
        if (category == null) {
            throw new ResourceNotFoundException("category with identifier " + identifier + " not found");
        }
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public List<CategoryDto> findBySuperCategoryNotNull() {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(
                categoryRepository.findBySuperCategoryIsNotAndDeletedFalse(""),
                listType
        );
    }

    @Override
    public void updateStatus(String identifier, boolean status) {
        Category category = categoryRepository.findByIdentifierAndDeletedFalse(identifier);
        if (category != null) {
            category.setStatus(status);
            setModifiedDetails(category);
            categoryRepository.save(category);
        }
    }

    @Override
    public List<CategoryDto> findAllActive() {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(
                categoryRepository.findByStatusAndDeletedFalse(true),
                listType
        );
    }

    @Override
    public WsDto<CategoryDto> findAll(Specification<Category> example, Pageable pageable) {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        Page<Category> page = categoryRepository.findAll(example, pageable);
        WsDto<CategoryDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }
}