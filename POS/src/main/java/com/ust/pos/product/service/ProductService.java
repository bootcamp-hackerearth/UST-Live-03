package com.ust.pos.product.service;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductDto save(ProductDto userDto);

    ProductDto update(ProductDto userDto);

    void delete(String identifier);

    WsDto<ProductDto> findAll(Pageable pageable);

    ProductDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);
}
