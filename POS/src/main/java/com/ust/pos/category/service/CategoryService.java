package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    void delete(String identifier);

    WsDto<CategoryDto> findAll(Pageable pageable);

    List<CategoryDto> findAll();

    Page<CategoryDto> findAll(Example<Category> example, Pageable pageable);

    CategoryDto findByIdentifier(String identifier);
}
