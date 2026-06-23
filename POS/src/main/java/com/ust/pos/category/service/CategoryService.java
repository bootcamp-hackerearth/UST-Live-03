package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    void deleteByIdentifier(String identifier);

    CategoryDto findByIdentifier(String identifier);

    WsDto<CategoryDto> findAll(Pageable pageable);

    List<CategoryDto> findChildCategories();

    CategoryDto toggleStatus(String identifier);

    List<CategoryDto> findIfTrue();

    List<CategoryDto> findBySuperCategoryNotNull();
}
