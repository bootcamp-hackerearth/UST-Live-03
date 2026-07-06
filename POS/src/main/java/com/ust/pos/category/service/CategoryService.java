package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.model.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CategoryService {
    PaginationResponseDto<CategoryDto> findAll(Pageable pageable);

    PaginationResponseDto<CategoryDto> findAll(Specification<Category> example, Pageable pageable);

    CategoryDto findByIdentifier(String identifier);

    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    List<CategoryDto> findBySuperCategoryIsNotNullAndStatusTrue();

    CategoryDto updateStatus(String identifier, boolean status);

    void delete(String identifier);
}
