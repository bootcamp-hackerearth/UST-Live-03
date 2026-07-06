package com.ust.pos.product.service;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ProductService {

    WsDto<ProductDto> findAll(Pageable pageable);

    ProductDto findByIdentifier(String productCode);

    ProductDto save(ProductDto productDto);

    ProductDto update(ProductDto productDto);

    void delete(String identifier);

    void toggleStatus(String identifier);

    WsDto<ProductDto> findAll(Specification<Product> example, Pageable pageable);
}
