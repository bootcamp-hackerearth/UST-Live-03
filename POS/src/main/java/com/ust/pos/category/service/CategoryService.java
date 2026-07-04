package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface CategoryService {

    CategoryDto findByIdentifier(String identifier);

    CategoryDto save(CategoryDto dto);

    CategoryDto update(CategoryDto dto);

    boolean delete(String identifier);

    WsDto<CategoryDto> findAll(Pageable pageable);

    List<CategoryDto> findSuperCategories();

    CategoryDto toggleStatus(String identifier);

    List<CategoryDto> findIfTrue();

    WsDto<CategoryDto> findAll(Specification<Category> spec, Pageable pageable, String keyword);
}
