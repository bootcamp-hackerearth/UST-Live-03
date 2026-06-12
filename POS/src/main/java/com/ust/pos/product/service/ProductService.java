package com.ust.pos.product.service;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.ProductDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    PaginationResponseDto<ProductDto> findAll(Pageable pageable);

    List<ProductDto> findByStatusTrue();

    ProductDto save(ProductDto productDto);

    ProductDto findByIdentifier(String identifier);

    ProductDto update(ProductDto productDto);

    ProductDto updateStatus(String identifier, boolean status);

    void delete(String identifier);
}
