package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    void delete(String identifier);

    List<CategoryDto> findAll();

    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto findByIdentifier(String identifier);

    List<CategoryDto> findAllWithoutNull();
}
