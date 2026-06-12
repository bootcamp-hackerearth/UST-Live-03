package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

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
}