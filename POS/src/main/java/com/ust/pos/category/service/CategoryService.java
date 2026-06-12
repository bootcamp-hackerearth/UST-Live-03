package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.NodeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    void delete(String identifier);

    List<CategoryDto> findAll();

    List<CategoryDto> findAllWithoutNull();

    Page<CategoryDto> findAll(Pageable pageable,String search);

    CategoryDto findByIdentifier(String identifier);
}
