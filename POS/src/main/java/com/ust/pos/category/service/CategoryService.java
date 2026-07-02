package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;

import com.ust.pos.dto.PageDto;
import com.ust.pos.model.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


import java.util.List;

public interface CategoryService {
   CategoryDto save(CategoryDto  categoryDto);

   CategoryDto update(CategoryDto  categoryDto);

    boolean delete(String identifier);

    PageDto<CategoryDto> findAll(Pageable pageable);

    CategoryDto findByIdentifier(String identifier);

   List<CategoryDto> findBySubCategory();

    void toggleStatus(String identifier);

    List<CategoryDto> findActiveCategories();

    PageDto<CategoryDto> findAll(Specification<Category> spec, Pageable pageable, String keyword);
}
