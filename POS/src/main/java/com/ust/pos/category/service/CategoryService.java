package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CategoryService {

    CategoryDto save(CategoryDto categoryDto);

    WsDto<CategoryDto> findAll(Pageable pageable);

    CategoryDto findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    CategoryDto update(CategoryDto categoryDto);

    List<CategoryDto> findSubCategories();

    WsDto<CategoryDto> findAll(Specification<Category> specification, Pageable pageable);

}