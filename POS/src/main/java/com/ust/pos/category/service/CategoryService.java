package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    List<CategoryDto> findAll(Pageable pageable);

    void delete(String identifier);

    CategoryDto findByIdentifier(String identifier);

    public List<CategoryDto> findBySuperCategoryNotNull();

    void updateStatus(String identifier, boolean status);

    List<CategoryDto> findAllActive();
}