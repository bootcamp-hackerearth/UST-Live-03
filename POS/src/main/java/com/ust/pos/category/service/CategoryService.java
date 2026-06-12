package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.PaginatedResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    void delete(String identifier);

    PaginatedResponseDto<CategoryDto> findAll(Pageable pageable);

    CategoryDto findByIdentifier(String identifier);

    List<CategoryDto> findAllActive();

    void changeStatus(String identifier, boolean status);
}
