package com.ust.pos.category.service;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CategoryService {

    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    CategoryDto findByIdentifier(String identifier);

    WsDto<CategoryDto> findAll(Pageable pageable);

    List<CategoryDto> findAllcontroller(Pageable pageable);

    List<CategoryDto> findSuperCategories();

    List<CategoryDto> findLeafCategories();

    List<CategoryDto> findChildCategories();

    void delete(String identifier);

    WsDto<CategoryDto> findAll(Specification<Category> example, Pageable pageable);
}