package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto findByIdentifier(String identifier);

    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    List<CategoryDto> findBySuperCategoryIsNotNullAndStatusTrue();

    CategoryDto updateStatus(String identifier, boolean status);

    void delete(String identifier);
}
