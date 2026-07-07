package com.ust.pos.category.service.impl;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import com.ust.pos.service.BaseService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl extends BaseService implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingCategory = categoryRepository.findByIdentifierAndDeletedFalse(identifier);
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
        Category existingCategory = categoryRepository.findByIdentifierAndDeletedFalse(identifier);
        if (existingCategory == null) {
            categoryDto.setMessage("Category with identifier - " + identifier + " is not found");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        Category category = modelMapper.map(categoryDto, Category.class);
        categoryRepository.save(category);
        return categoryDto;
    }

    @Override
    public void delete(String identifier) {
        Category category = categoryRepository.findByIdentifierAndDeletedFalse(identifier);
        if (category != null) {
            category.setDeleted(true);
            categoryRepository.save(category);
        }
    }

    @Override
    public List<CategoryDto> findAll() {
        Type listOfType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(categoryRepository.findByDeletedFalse(), listOfType);
    }

    @Override
    public Page<CategoryDto> findAll(Pageable pageable, String search) {
        Page<Category> categories;
        if (search != null && !search.trim().isEmpty()) {
            Specification<Category> specification = buildGlobalSearchSpec(Category.class, search);
            categories = categoryRepository.findAll(specification, pageable);
        } else {
            categories = categoryRepository.findByDeletedFalse(pageable);
        }
        return categories.map(category -> modelMapper.map(category, CategoryDto.class));
    }

    @Override
    public CategoryDto findByIdentifier(String identifier) {
        return modelMapper.map(categoryRepository.findByIdentifierAndDeletedFalse(identifier), CategoryDto.class);
    }

    @Override
    public List<CategoryDto> findAllWithoutNull() {
        Type listOfType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        List<CategoryDto> categoryDtos = modelMapper.map(categoryRepository.findByDeletedFalse(), listOfType);
        return categoryDtos.stream().filter(c -> c.getSuperCategory() != null)
                .toList();
    }
}
