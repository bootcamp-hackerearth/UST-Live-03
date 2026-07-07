package com.ust.pos.product.service;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ProductService {

    ProductDto save(ProductDto productDto);

    ProductDto update(ProductDto productDto);

    ProductDto findByIdentifier(String identifier);

    WsDto<ProductDto> findAll(Pageable pageable);

    void delete(String identifier);

    ProductDto toggleStatus(String identifier);

    List<ProductDto> findActiveProducts();

    List<ProductDto> searchProduct(String query);

    WsDto<ProductDto> findAll(Specification<Product> example, Pageable pageable);
}