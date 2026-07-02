package com.ust.pos.product.service;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    WsDto<ProductDto> findAll(Specification<Product> example, Pageable pageable, String keyword);

}