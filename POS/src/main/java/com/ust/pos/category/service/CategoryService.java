package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface CategoryService {

    CategoryDto save(CategoryDto categoryDto);
    CategoryDto update(CategoryDto categoryDto);
    boolean delete(String identifier);
    WsDto<CategoryDto> findAll(Pageable pageable);
    CategoryDto findByIdentifier(String identifier);
    List<CategoryDto> findBySuperCategoryNotNull();
    CategoryDto toggleStatus(String identifier);
    List<CategoryDto> findIfTrue();
    List<CategoryDto> findAllActive();

}