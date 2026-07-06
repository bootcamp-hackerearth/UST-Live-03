package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;


public interface CategoryService {
    WsDto<CategoryDto> findAll(Pageable pageable);

    CategoryDto findByIdentifier(String categoryCode);

    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    void delete(String identifier);

    List<CategoryDto> findAllCategoriesWithNoSuper();

    void toggleStatus(String identifier);

    WsDto<CategoryDto> findAll(Specification<Category> example, Pageable pageable);
}
