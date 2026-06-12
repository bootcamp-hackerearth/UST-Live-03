package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface CategoryService {


    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    void delete(String identifier);

    WsDto<CategoryDto> findAll(Pageable pageable);

    WsDto<CategoryDto> findAllCategoriesWithNoSuper(Pageable pageable);

    CategoryDto findByIdentifier(String identifier);
}
