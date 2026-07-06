package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CategoryService {
    WsDto<CategoryDto> findAll(Pageable pageable);

    WsDto<CategoryDto> findAll(Specification<Category> example, Pageable pageable);

    CategoryDto save(CategoryDto categoryDto);

    void delete(String identifier);

    CategoryDto findByIdentifier(String identifier);

    CategoryDto update(CategoryDto categoryDto);

    List<CategoryDto> findSubCategories();

    CategoryDto changeToggleStatus(String identifier, boolean status);

    List<CategoryDto> findActiveStatus();
}
