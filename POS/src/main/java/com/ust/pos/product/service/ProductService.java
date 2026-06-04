package com.ust.pos.product.service;

import com.ust.pos.dto.ProductDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    ProductDto save(ProductDto productDto);

    List<ProductDto> findAll(Pageable pageable);

    ProductDto update(ProductDto productDto);

    ProductDto findById(Long id);

    void delete(Long id);

    ProductDto changeProductStatus(String identifier, boolean status);

    List<ProductDto> findAllActiveProduct();
}
