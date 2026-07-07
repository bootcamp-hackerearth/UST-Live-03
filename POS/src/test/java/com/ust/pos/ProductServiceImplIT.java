package com.ust.pos;

import com.ust.pos.product.service.ProductService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ProductServiceImplIT {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void cleanUp() {
        productRepository.deleteAll();
    }

    @Test
    void save_shouldCreateProduct() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD001");
        dto.setStatus(true);
        ProductDto response = productService.save(dto);
        Product saved = productRepository.findByIdentifier("PROD001");
        assertTrue(response.isSuccess());
        assertEquals("Product created successfully", response.getMessage());
        assertNotNull(saved);
        assertEquals("PROD001", saved.getIdentifier());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {
        Product product = new Product();
        product.setIdentifier("PROD001");
        product.setDeleted(false);
        productRepository.save(product);
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD001");
        ProductDto response = productService.save(dto);
        assertFalse(response.isSuccess());
        assertEquals(
                "Product with identifier - PROD001 already exists",
                response.getMessage()
        );
    }

    @Test
    void save_shouldFailWhenPreviouslyDeleted() {
        Product product = new Product();
        product.setIdentifier("PROD001");
        product.setDeleted(true);
        productRepository.save(product);
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD001");
        ProductDto response = productService.save(dto);
        assertFalse(response.isSuccess());
        assertEquals(
                "Product with identifier - PROD001 was previously deleted. Please contact backend team to restore.",
                response.getMessage()
        );
    }

    @Test
    void update_shouldModifyProductDetails() {
        Product product = new Product();
        product.setIdentifier("PROD001");
        product.setDeleted(false);
        product.setName("Old Laptop Name");
        productRepository.save(product);
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD001");
        dto.setName("New Laptop Name");
        productService.update(dto);
        Product updated = productRepository.findByIdentifier("PROD001");
        assertEquals("New Laptop Name", updated.getName());
    }

    @Test
    void update_shouldFailWhenNotFound() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD_MISSING");
        ProductDto response = productService.update(dto);
        assertFalse(response.isSuccess());
        assertEquals(
                "Product with identifier - PROD_MISSING not found",
                response.getMessage()
        );
    }

    @Test
    void findByIdentifier_shouldReturnProduct() {
        Product product = new Product();
        product.setIdentifier("PROD001");
        productRepository.save(product);
        ProductDto result = productService.findByIdentifier("PROD001");
        assertNotNull(result);
        assertEquals("PROD001", result.getIdentifier());
    }

    @Test
    void findByIdentifier_shouldThrowWhenNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> productService.findByIdentifier("PROD_MISSING"));
    }

    @Test
    void toggleStatus_shouldToggleValue() {
        Product product = new Product();
        product.setIdentifier("PROD001");
        product.setStatus(true);
        productRepository.save(product);
        productService.toggleStatus("PROD001");
        Product updated = productRepository.findByIdentifier("PROD001");
        assertFalse(updated.isStatus());
    }

    @Test
    void delete_shouldSoftDelete() {
        Product product = new Product();
        product.setIdentifier("PROD001");
        product.setDeleted(false);
        productRepository.save(product);
        boolean result = productService.delete("PROD001");
        Product deleted = productRepository.findByIdentifier("PROD001");
        assertTrue(result);
        assertTrue(deleted.isDeleted());
    }

    @Test
    void delete_shouldReturnFalseWhenNotFound() {
        boolean result = productService.delete("PROD_MISSING");
        assertFalse(result);
    }

    @Test
    void findAll_pageable_shouldReturnOnlyNonDeletedProducts() {
        Product active = new Product();
        active.setIdentifier("PROD001");
        active.setDeleted(false);
        productRepository.save(active);
        Product deleted = new Product();
        deleted.setIdentifier("PROD002");
        deleted.setDeleted(true);
        productRepository.save(deleted);
        Pageable pageable = PageRequest.of(0, 10);
        WsDto<ProductDto> result = productService.findAll(pageable);
        assertEquals(1, result.getTotalRecords());
        assertEquals("PROD001", result.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findAll_specification_shouldReturnFilteredResults() {
        Product product = new Product();
        product.setIdentifier("PROD001");
        product.setDeleted(false);
        productRepository.save(product);
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Product> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        WsDto<ProductDto> result = productService.findAll(spec, pageable, "PROD001");
        assertEquals(1, result.getTotalRecords());
        assertEquals("PROD001", result.getKeyword());
    }

    @Test
    void findIfTrue_shouldReturnActiveAndNonDeletedProducts() {
        Product validProduct = new Product();
        validProduct.setIdentifier("PROD001");
        validProduct.setStatus(true);
        validProduct.setDeleted(false);
        productRepository.save(validProduct);
        Product inactiveProduct = new Product();
        inactiveProduct.setIdentifier("PROD002");
        inactiveProduct.setStatus(false);
        inactiveProduct.setDeleted(false);
        productRepository.save(inactiveProduct);
        List<ProductDto> result = productService.findIfTrue();
        assertEquals(1, result.size());
        assertEquals("PROD001", result.get(0).getIdentifier());
    }
}