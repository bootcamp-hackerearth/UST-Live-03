package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    CategoryDto save(CategoryDto userDto);

    CategoryDto update(CategoryDto userDto);

    void delete(String identifier);

    WsDto<CategoryDto> findAll(Pageable pageable);

    CategoryDto findByIdentifier(String identifier);

    List<CategoryDto> findBySuperCategoryNotNull();

    void toggleStatus(String identifier);

    List<CategoryDto> findAllList();
}
