package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CategoryService {

    CategoryDto save(CategoryDto categoryDto);

    CategoryDto findById(Long id);

    CategoryDto update(CategoryDto categoryDto);

    WsDto<CategoryDto> findAll(Pageable pageable);

    WsDto<CategoryDto> findAll(Specification<Category> spec, Pageable pageable);

    void delete(String identifier);

    List<CategoryDto> findSubCategories();

    CategoryDto changeCategoryStatus(String identifier, boolean status);
}