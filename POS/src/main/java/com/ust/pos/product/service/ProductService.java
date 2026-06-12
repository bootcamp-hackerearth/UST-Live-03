package com.ust.pos.product.service;

import com.ust.pos.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductDto save(ProductDto productDto);

    ProductDto update(ProductDto productDto);

    void delete(String identifier);

    List<ProductDto> findAll();

    ProductDto findByIdentifier(String identifier);

    Page<ProductDto> findAll(String search,Pageable pageable);

    void toggleStatus(String identifier);

}
