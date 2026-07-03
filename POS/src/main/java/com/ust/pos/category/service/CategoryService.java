package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CategoryService {
    CategoryDto save(CategoryDto categoryDto);

    CategoryDto findByIdentifier(String identifier);

    WsDto<CategoryDto> findAll(Pageable pageable);

    WsDto<CategoryDto> findAll(Specification<Category> example, Pageable pageable);

    List<CategoryDto> findAllActive();

    CategoryDto update(CategoryDto categoryDto);

    CategoryDto toggleStatus(String identifier);

    boolean delete(String identifier);
}
