package com.ust.pos.product.service;

import com.ust.pos.dto.ProductDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    List<ProductDto> findAll(Pageable pageable);

    ProductDto findByIdentifier(String productCode);

    ProductDto save(ProductDto productDto);

    ProductDto update(ProductDto productDto);

    void delete(String identifier);
}
