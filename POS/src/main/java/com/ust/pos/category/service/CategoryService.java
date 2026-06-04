package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    CategoryDto save(CategoryDto categoryDto);

    CategoryDto findById(Long id);

    CategoryDto update(CategoryDto categoryDto);

    List<CategoryDto> findAll(Pageable pageable);

    void deleteById(Long id);

    List<CategoryDto> findSubCategories();
}
