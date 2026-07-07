package com.ust.pos.product.service;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ProductService {

    ProductDto findByIdentifier(String identifier);

    ProductDto save(ProductDto productDto);

    ProductDto update(ProductDto productDto);

    ProductDto delete(String identifier);

    PaginatedResponseDto<ProductDto> findAll(Pageable pageable);

    List<ProductDto> findAllActive();

    void changeStatus(String identifier, boolean status);

    PaginatedResponseDto<ProductDto> findAll(Specification<Product> example, Pageable pageable);
}