package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto findByIdentifier(String identifier);

    CategoryDto save(CategoryDto categoryDto);

    List<CategoryDto> findSubCategories();

    void delete(String identifier);

    CategoryDto update(CategoryDto categoryDto);

    CategoryDto toggleStatus(String identifier, boolean status);
}