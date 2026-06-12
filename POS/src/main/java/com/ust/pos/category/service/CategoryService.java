package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    WsDto<CategoryDto> findAll(Pageable pageable);

    CategoryDto save(CategoryDto categoryDto);

    void delete(String identifier);

    CategoryDto findByIdentifier(String identifier);

    CategoryDto update(CategoryDto categoryDto);

    List<CategoryDto> findSubCategories();

    CategoryDto changeToggleStatus(String identifier, boolean status);

    List<CategoryDto> findActiveStatus();
}
