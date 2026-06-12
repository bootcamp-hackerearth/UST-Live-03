package com.ust.pos.product.service;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ProductService {

    ProductDto save(ProductDto productDto);
    ProductDto update(ProductDto productDto);
    boolean delete(String identifier);
    WsDto<ProductDto> findAll(Pageable pageable);
    ProductDto findByIdentifier(String identifier);
    List<ProductDto> findIfTrue();
    ProductDto toggleStatus(String identifier);

}