package com.ust.pos.product.service;

import com.ust.pos.dto.ProductDto;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProductService {
    ProductDto save(ProductDto userDto);

    ProductDto update(ProductDto userDto);

    void delete(String identifier);

    List<ProductDto> findAll();

    List<ProductDto> findAll(Pageable pageable);

    ProductDto findByIdentifier(String identifier);
}
