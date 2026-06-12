package com.ust.pos.product.service;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Product;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductDto save(ProductDto productDto);

    ProductDto update(ProductDto productDto);

    void delete(String identifier);

    WsDto<ProductDto> findAll(Pageable pageable);

    ProductDto findByIdentifier(String identifier);

    List<Product> findActiveProducts();

    ProductDto toggleStatus(String identifier);
}
