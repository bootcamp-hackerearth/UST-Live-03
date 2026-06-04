package com.ust.pos.product.service;

import com.ust.pos.dto.ProductDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    ProductDto findByIdentifier(String identifier);

    ProductDto save(ProductDto productDto);

    ProductDto update(ProductDto productDto);

    void delete(String identifier);

    List<ProductDto> findAll(Pageable pageable);

    List<ProductDto> findAllActive();

    void changeStatus(String identifier, boolean status);
}
