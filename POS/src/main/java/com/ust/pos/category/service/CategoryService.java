package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    CategoryDto findByIdentifier(String identifier);

    List<CategoryDto> findAll(Pageable pageable);

    List<CategoryDto> findSuperCategories();

    List<CategoryDto> findLeafCategories();

    List<CategoryDto> findChildCategories();

    void delete(String identifier);
}