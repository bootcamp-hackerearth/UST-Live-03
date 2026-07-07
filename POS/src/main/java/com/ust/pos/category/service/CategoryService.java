package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.model.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CategoryService {

    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    CategoryDto delete(String identifier);

    PaginatedResponseDto<CategoryDto> findAll(Pageable pageable);

    CategoryDto findByIdentifier(String identifier);

    List<CategoryDto> findAllActive();

    void changeStatus(String identifier, boolean status);

    PaginatedResponseDto<CategoryDto> findAll(Specification<Category> example, Pageable pageable);
}