package com.ust.pos.product.service;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ProductService {

    ProductDto save(ProductDto productDto);

    WsDto<ProductDto> findAll(Pageable pageable);

    WsDto<ProductDto> findAll(Specification<Product> spec, Pageable pageable);

    ProductDto update(ProductDto productDto);

    ProductDto findById(Long id);

    void delete(String identifier);

    ProductDto changeProductStatus(String identifier, boolean status);

    List<ProductDto> findAllActiveProduct();
}