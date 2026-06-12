package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    CategoryDto save(CategoryDto categoryDto);

    CategoryDto findByIdentifier(String identifier);

    WsDto<CategoryDto> findAll(Pageable pageable);

    List<CategoryDto> findAllActive();

    CategoryDto update(CategoryDto categoryDto);

    CategoryDto toggleStatus(String identifier);

    boolean delete(String identifier);
}
