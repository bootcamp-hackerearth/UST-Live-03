package com.ust.pos.product.service;

import com.ust.pos.dto.ProductDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductDto save(ProductDto productDto);

    List<ProductDto> findAll(Pageable pageable);

    ProductDto findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    ProductDto update(ProductDto productDto);
}