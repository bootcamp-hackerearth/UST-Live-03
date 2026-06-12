package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    CategoryDto save(CategoryDto categoryDto);

    CategoryDto findById(Long id);

    CategoryDto update(CategoryDto categoryDto);

    WsDto<CategoryDto> findAll(Pageable pageable);

    void deleteById(Long id);

    List<CategoryDto> findSubCategories();

    CategoryDto changeCategoryStatus(String identifier, boolean status);
}
