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
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends BaseService implements CategoryService {

    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingCategory = categoryRepository.findByIdentifier(identifier);
        if (existingCategory != null) {
            if (Boolean.TRUE.equals(existingCategory.getDeleted())) {
                categoryDto.setMessage("Category identifier - " + identifier + " not available ");
                categoryDto.setSuccess(false);
                return categoryDto;
            }
            categoryDto.setMessage("Category with identifier - " + identifier + " already exists");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        Category category = modelMapper.map(categoryDto, Category.class);
        setCreatedDetails(category);
        setModifiedDetails(category);
        categoryRepository.save(category);
        return categoryDto;
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingCategory = categoryRepository.findByIdentifier(identifier);
        if (existingCategory == null) {
            categoryDto.setMessage("category with identifier - " + identifier + " not found");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        modelMapper.map(categoryDto, existingCategory);
        setModifiedDetails(existingCategory);
        categoryRepository.save(existingCategory);
        return categoryDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Category category = categoryRepository.findByIdentifierAndDeletedFalse(identifier);
        setModifiedDetails(category);
        softDelete(category);
    }

    @Override
    public WsDto<CategoryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        Page<Category> categoryPage = categoryRepository.findAllByDeletedFalse(pageable);

        WsDto<CategoryDto> categoryWsDto = new WsDto<>();
        categoryWsDto.setDtoList(modelMapper.map(categoryPage.getContent(), listType));
        categoryWsDto.setTotalRecords(categoryPage.getTotalElements());
        categoryWsDto.setTotalPages(categoryPage.getTotalPages());
        categoryWsDto.setSizePerPage(pageable.getPageSize());
        categoryWsDto.setPage(pageable.getPageNumber());
        return categoryWsDto;
    }

    @Override
    public CategoryDto findByIdentifier(String identifier) {
        return modelMapper.map(categoryRepository.findByIdentifierAndDeletedFalse(identifier), CategoryDto.class);
    }

    @Override
    public List<CategoryDto> findAllWithSuperCategory() {
        List<Category> superCategory = categoryRepository.findAllByDeletedFalse();
        List<Category> categories = superCategory.stream().filter(category -> !category.getSuperCategory().isEmpty()).toList();
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();

        return modelMapper.map(categories, listType);
    }
}
