package com.ust.pos.product.service;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    ProductDto update(ProductDto productDto);

    WsDto<ProductDto> findAll(Pageable pageable);

    ProductDto save(ProductDto productDto);

    void delete(String identifier);

    ProductDto findByIdentifier(String identifier);

    ProductDto changeToggleStatus(String identifier, boolean status);

    List<ProductDto> findActiveStatus();

}
