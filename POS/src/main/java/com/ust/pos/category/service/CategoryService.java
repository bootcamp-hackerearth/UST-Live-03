package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    void delete(String username);

    WsDto<CategoryDto> findAll(Pageable pagebale);

    CategoryDto findByIdentifier(String identifier);

    List<CategoryDto> findChildCategories();
}