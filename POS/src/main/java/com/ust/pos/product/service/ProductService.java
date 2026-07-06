package com.ust.pos.product.service;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ProductService {
    PaginationResponseDto<ProductDto> findAll(Pageable pageable);

    PaginationResponseDto<ProductDto> findAll(Specification<Product> example, Pageable pageable);

    List<ProductDto> findByStatusTrue();

    ProductDto save(ProductDto productDto);

    ProductDto findByIdentifier(String identifier);

    ProductDto update(ProductDto productDto);

    ProductDto updateStatus(String identifier, boolean status);

    void delete(String identifier);

    List<ProductDto> searchProduct(String query);
}
