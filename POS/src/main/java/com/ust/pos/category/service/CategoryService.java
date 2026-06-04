package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface CategoryService {

    CategoryDto findByIdentifier(String identifier);

    CategoryDto save(CategoryDto dto);

    CategoryDto update(CategoryDto dto);

    boolean delete(String identifier);

    List<CategoryDto> findAll(Pageable pageable);

    List<CategoryDto> findSuperCategories();

    CategoryDto toggleStatus(String identifier);

    List<CategoryDto> findIfTrue();
}
