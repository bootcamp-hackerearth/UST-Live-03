package com.ust.pos.product.service;

import com.ust.pos.dto.ProductDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    ProductDto save(ProductDto productDto);

    ProductDto update(ProductDto productDto);

    ProductDto findByIdentifier(String identifier);

    List<ProductDto> findAll(Pageable pageable);

    void delete(String identifier);

    ProductDto toggleStatus(String identifier);

    List<ProductDto> findActiveProducts();
}