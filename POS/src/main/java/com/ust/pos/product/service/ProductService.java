package com.ust.pos.product.service;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductDto save(ProductDto productDto);

    WsDto<ProductDto> findAll(Pageable pageable);

    List<ProductDto> findAllActive();

    ProductDto findByIdentifier(String identifier);

    ProductDto update(ProductDto productDto);

    ProductDto toggleStatus(String identifier);

    boolean delete(String identifier);

}
