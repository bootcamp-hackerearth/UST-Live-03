package com.ust.pos.category.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends BaseService implements CategoryService {

    public static final String CATEGORY_WITH_IDENTIFIER = "Category with identifier - ";
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public CategoryDto findByIdentifier(String identifier) {
        Category category = categoryRepository.findByIdentifierAndDeletedFalse(identifier);
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingCategory = categoryRepository.findByIdentifier(identifier);
        if (existingCategory != null) {
            if (Boolean.TRUE.equals(existingCategory.getDeleted())) {
                categoryDto.setMessage(CATEGORY_WITH_IDENTIFIER + identifier + " was deleted and cannot be created again.");
                categoryDto.setSuccess(false);
                return categoryDto;
            }
            categoryDto.setMessage(CATEGORY_WITH_IDENTIFIER + identifier + " already exists");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        Category category = modelMapper.map(categoryDto, Category.class);
        category.setStatus(true);
        category.setDeleted(false);
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
            categoryDto.setMessage(CATEGORY_WITH_IDENTIFIER + identifier + " not found");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        modelMapper.map(categoryDto, existingCategory);
        existingCategory.setDeleted(false);
        setModifiedDetails(existingCategory);
        categoryRepository.save(existingCategory);
        categoryDto.setSuccess(true);
        return categoryDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Category category = categoryRepository.findByIdentifierAndDeletedFalse(identifier);
        softDelete(category);
        setModifiedDetails(category);
        categoryRepository.save(category);
    }

    @Override
    public WsDto<CategoryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        Page<Category> categoryPage = categoryRepository.findAllByDeletedFalse(pageable);
        WsDto<CategoryDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(categoryPage.getContent(), listType));
        wsDto.setTotalRecords(categoryPage.getTotalElements());
        wsDto.setTotalPage(categoryPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public WsDto<CategoryDto> findAll(Specification<Category> example, Pageable pageable) {

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        Page<Category> page = categoryRepository.findAll(example, pageable);

        WsDto<CategoryDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPage(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

    @Override
    public List<CategoryDto> findChildCategories() {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(categoryRepository.findBySuperCategoryIsNotAndDeletedFalse(""), listType
        );
    }

    @Override
    public CategoryDto toggleStatus(String identifier) {
        Category category = categoryRepository.findByIdentifierAndDeletedFalse(identifier);
        if (category == null) {
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setSuccess(false);
            categoryDto.setMessage("Category not found");
            return categoryDto;
        }
        category.setStatus(!category.isStatus());
        setModifiedDetails(category);
        categoryRepository.save(category);
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public List<CategoryDto> findActiveCategories() {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(categoryRepository.findByStatusTrueAndDeletedFalse(), listType);
    }

}
