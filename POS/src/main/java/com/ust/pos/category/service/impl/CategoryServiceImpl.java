package com.ust.pos.category.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import jakarta.transaction.Transactional;
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
    private final ModelMapper modelMapper;

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(ModelMapper modelMapper, CategoryRepository categoryRepository) {
        this.modelMapper = modelMapper;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public WsDto<CategoryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        Page<Category> userPage = categoryRepository.findByDeletedFalse(pageable);

        WsDto<CategoryDto> userWsDto = new WsDto<>();
        userWsDto.setDtoList(modelMapper.map(userPage.getContent(), listType));
        userWsDto.setTotalRecords(userPage.getTotalElements());
        userWsDto.setTotalPages(userPage.getTotalPages());
        userWsDto.setSizePerPage(pageable.getPageSize());
        userWsDto.setPage(pageable.getPageNumber());

        return userWsDto;   }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingCategory = categoryRepository.findByIdentifier(identifier);
        if (existingCategory != null) {
            if(existingCategory.isDeleted()) {
                categoryDto.setMessage(CATEGORY_WITH_IDENTIFIER + identifier + "has been soft deleted.(Rollback by changing status");
                categoryDto.setSuccess(false);
                return categoryDto;
            }

                categoryDto.setMessage(CATEGORY_WITH_IDENTIFIER + identifier + " already exists");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        Category category = modelMapper.map(categoryDto, Category.class);
        setAuditFields(category,true);
        categoryRepository.save(category);
        return categoryDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Category category = categoryRepository.findByIdentifier(identifier);
        softDelete(category);
        setAuditFields(category,false);
        categoryRepository.save(category);
    }

    @Override
    public CategoryDto findByIdentifier(String identifier) {
        return modelMapper.map(categoryRepository.findByIdentifier(identifier), CategoryDto.class);
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingCategory = categoryRepository.findByIdentifier(identifier);
        if (existingCategory == null) {
            categoryDto.setMessage(CATEGORY_WITH_IDENTIFIER + identifier + " already exists");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        modelMapper.map(categoryDto, existingCategory);
        setAuditFields(existingCategory,false);
        categoryRepository.save(existingCategory);
        return categoryDto;
    }

    @Override
    public List<CategoryDto> findSubCategories() {
        return categoryRepository.findBySuperCategoryIsNot(" ").stream()
                .map(cat -> modelMapper.map(cat, CategoryDto.class))
                .toList();
    }

    @Override
    public CategoryDto changeToggleStatus(String identifier, boolean status) {
        Category category=categoryRepository.findByIdentifier(identifier);
        if(category!=null)
        {
            category.setStatus(status);
            categoryRepository.save(category);
        }
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public List<CategoryDto> findActiveStatus() {
        List<Category> allCategory = categoryRepository.findAll();
        List<Category> activeCategory = allCategory.stream().filter(Category::isStatus).toList();

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(activeCategory, listType);
    }
}
