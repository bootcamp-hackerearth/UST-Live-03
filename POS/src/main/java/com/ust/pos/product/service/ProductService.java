package com.ust.pos.product.service;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;


public interface ProductService {
   ProductDto save(ProductDto productDto);

    ProductDto  update(ProductDto productDto);

    boolean delete(String identifier);

    PageDto<ProductDto > findAll(Pageable pageable);

    ProductDto  findByIdentifier(String identifier);

    void toggleStatus(String identifier);

    List<ProductDto> findActiveProducts();

    PageDto<ProductDto> findAll(Specification<Product> spec, Pageable pageable, String keyword);
}
