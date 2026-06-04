package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto findByIdentifier(String categoryCode);

    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    void delete(String identifier);

    List<CategoryDto> findAllCategoriesWithNoSuper();
}
