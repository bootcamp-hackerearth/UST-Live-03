package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    CategoryDto save(CategoryDto categoryDto);

    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    CategoryDto update(CategoryDto categoryDto);

    List<CategoryDto> findSubCategories();
}
