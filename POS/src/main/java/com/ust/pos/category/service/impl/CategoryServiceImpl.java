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

    private static final String CATEGORY_WITH_IDENTIFIER = "Category with identifier - ";

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryDto findByIdentifier(String identifier) {
        return modelMapper.map(categoryRepository.findByIdentifier(identifier), CategoryDto.class);
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        categoryDto.setIdentifier(categoryDto.getIdentifier().trim());
        String identifier = categoryDto.getIdentifier();
        Category existingCategory = categoryRepository.findByIdentifier(identifier);
        if (existingCategory != null) {
            if (!existingCategory.isDeleted()) {
                categoryDto.setMessage(CATEGORY_WITH_IDENTIFIER + identifier + " already exists");
                categoryDto.setSuccess(false);
                return categoryDto;
            }
            categoryDto.setMessage(CATEGORY_WITH_IDENTIFIER + identifier + " was previously deleted. " +
                    "Please contact backend team to restore.");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        Category category = modelMapper.map(categoryDto, Category.class);
        setAuditFields(category, true);
        categoryRepository.save(category);
        categoryDto.setSuccess(true);
        categoryDto.setMessage("Category created successfully");
        return categoryDto;
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingCategory = categoryRepository.findByIdentifier(identifier);
        if (existingCategory == null) {
            categoryDto.setMessage(CATEGORY_WITH_IDENTIFIER + identifier + " not found");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        modelMapper.map(categoryDto, existingCategory);
        setAuditFields(existingCategory,false);
        categoryRepository.save(existingCategory);
        return categoryDto;
    }

    @Override
    public boolean delete(String identifier) {
        Category category = categoryRepository.findByIdentifier(identifier);
        if (category == null) return false;
        softDelete(category);
        setAuditFields(category,false);
        categoryRepository.save(category);
        return true;
    }

    @Override
    public WsDto<CategoryDto> findAll(Pageable pageable) {
        Type typeList = new TypeToken<List<CategoryDto>>() {
        }.getType();
        Page<Category> categoryPage = categoryRepository.findByDeletedFalse(pageable);
        WsDto<CategoryDto> categoryDtoWsDto = new WsDto<>();
        categoryDtoWsDto.setDtoList(modelMapper.map(categoryPage.getContent(), typeList));
        categoryDtoWsDto.setTotalRecords(categoryPage.getTotalElements());
        categoryDtoWsDto.setTotalPages(categoryPage.getTotalPages());
        categoryDtoWsDto.setSizePerPage(pageable.getPageSize());
        categoryDtoWsDto.setPage(pageable.getPageNumber());
        return categoryDtoWsDto;
    }

    @Override
    public List<CategoryDto> findBySuperCategoryNotNull() {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(categoryRepository.findByStatusTrueAndDeletedFalseAndSuperCategoryIsNot(""), listType);
    }

    public List<CategoryDto> findAllActiveCategories() {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(
                categoryRepository.findByStatusTrueAndDeletedFalse(),
                listType
        );
    }

    @Override
    public CategoryDto toggleStatus(String identifier) {
        Category category = categoryRepository.findByIdentifier(identifier);
        category.setStatus(!category.isStatus());
        setAuditFields(category,false);
        categoryRepository.save(category);
        return modelMapper.map(category, CategoryDto.class);
    }
}