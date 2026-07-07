package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CategoryService {
    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    WsDto<CategoryDto> findAll(Pageable pageable);

    void delete(String identifier);

    CategoryDto findByIdentifier(String identifier);

    public List<CategoryDto> findBySuperCategoryNotNull();

    void updateStatus(String identifier, boolean status);

    List<CategoryDto> findAllActive();

    WsDto<CategoryDto> findAll(Specification<Category> example, Pageable pageable);
}