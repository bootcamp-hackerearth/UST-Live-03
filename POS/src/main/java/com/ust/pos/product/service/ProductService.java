package com.ust.pos.product.service;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductDto save(ProductDto productDto);

    WsDto<ProductDto> findAll(Pageable pageable);

    ProductDto findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    ProductDto update(ProductDto productDto);

}