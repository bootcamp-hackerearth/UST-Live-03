package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
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

    @Autowired
    private PriceRepository priceRepository;

    @BeforeEach
    void cleanUp() {
        priceRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void save_shouldCreateProduct() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("SKU001");
        dto.setStatus(true);

        ProductDto response = productService.save(dto);

        Product saved = productRepository.findByIdentifier("SKU001");

        assertTrue(response.isSuccess());
        assertNotNull(saved);
        assertEquals("SKU001", saved.getIdentifier());
    }

    @Test
    void save_shouldTrimIdentifier() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("  SKU001  ");

        productService.save(dto);

        Product saved = productRepository.findByIdentifier("SKU001");

        assertNotNull(saved);
        assertEquals("SKU001", saved.getIdentifier());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {
        Product product = new Product();
        product.setIdentifier("SKU001");
        product.setDeleted(false);
        productRepository.save(product);

        ProductDto dto = new ProductDto();
        dto.setIdentifier("SKU001");

        ProductDto response = productService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Product with skuCode - SKU001 already exists",
                response.getMessage()
        );
    }

    @Test
    void save_shouldFailWhenPreviouslyDeleted() {
        Product product = new Product();
        product.setIdentifier("SKU001");
        product.setDeleted(true);
        productRepository.save(product);

        ProductDto dto = new ProductDto();
        dto.setIdentifier("SKU001");

        ProductDto response = productService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Product with skuCode - SKU001 has been soft deleted.(Rollback by changing status",
                response.getMessage()
        );
    }

    @Test
    void update_shouldUpdateProduct() {
        Product product = new Product();
        product.setIdentifier("SKU001");
        product.setStatus(true);
        productRepository.save(product);

        ProductDto dto = new ProductDto();
        dto.setIdentifier("SKU001");
        dto.setStatus(false);

        productService.update(dto);

        Product updated = productRepository.findByIdentifier("SKU001");

        assertFalse(updated.isStatus());
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("SKU404");

        assertThrows(ResourceNotFoundException.class,
                () -> productService.update(dto));
    }

    @Test
    void findByIdentifier_shouldReturnProduct() {
        Product product = new Product();
        product.setIdentifier("SKU001");
        productRepository.save(product);

        ProductDto dto = productService.findByIdentifier("SKU001");

        assertEquals("SKU001", dto.getIdentifier());
    }

    @Test
    void findByIdentifier_shouldReturnPriceWhenExists() {
        Product product = new Product();
        product.setIdentifier("SKU001");
        productRepository.save(product);

        Price price = new Price();
        price.setProductId(product.getId());
        priceRepository.save(price);

        ProductDto dto = productService.findByIdentifier("SKU001");

        assertNotNull(dto.getPrice());
    }

    @Test
    void findByIdentifier_shouldReturnNullPriceWhenNoPriceExists() {
        Product product = new Product();
        product.setIdentifier("SKU001");
        productRepository.save(product);

        ProductDto dto = productService.findByIdentifier("SKU001");

        assertNull(dto.getPrice());
    }

    @Test
    void findByIdentifier_shouldThrowWhenMissing() {
        assertThrows(ResourceNotFoundException.class,
                () -> productService.findByIdentifier("SKU404"));
    }

    @Test
    void delete_shouldSoftDelete() {
        Product product = new Product();
        product.setIdentifier("SKU001");
        product.setDeleted(false);
        productRepository.save(product);

        boolean deleted = productService.delete("SKU001");

        Product saved = productRepository.findByIdentifier("SKU001");

        assertTrue(deleted);
        assertTrue(saved.isDeleted());
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> productService.delete("SKU404"));
    }

    @Test
    void toggleStatus_shouldToggleStatus() {
        Product product = new Product();
        product.setIdentifier("SKU001");
        product.setStatus(true);
        productRepository.save(product);

        ProductDto dto = productService.toggleStatus("SKU001");

        assertFalse(dto.isStatus());

        Product updated = productRepository.findByIdentifier("SKU001");
        assertFalse(updated.isStatus());
    }

    @Test
    void toggleStatus_shouldThrowWhenMissing() {
        assertThrows(ResourceNotFoundException.class,
                () -> productService.toggleStatus("SKU404"));
    }

    @Test
    void findAll_pageable_shouldReturnOnlyNonDeletedProducts() {
        Product active = new Product();
        active.setIdentifier("SKU001");
        active.setDeleted(false);
        productRepository.save(active);

        Product deleted = new Product();
        deleted.setIdentifier("SKU002");
        deleted.setDeleted(true);
        productRepository.save(deleted);

        Pageable pageable = PageRequest.of(0, 10);

        WsDto<ProductDto> result = productService.findAll(pageable);

        assertEquals(1, result.getTotalRecords());
        assertEquals("SKU001", result.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findAll_pageable_shouldIncludePriceWhenAvailable() {
        Product product = new Product();
        product.setIdentifier("SKU001");
        productRepository.save(product);

        Price price = new Price();
        price.setProductId(product.getId());
        priceRepository.save(price);

        Pageable pageable = PageRequest.of(0, 10);

        WsDto<ProductDto> result = productService.findAll(pageable);

        assertNotNull(result.getDtoList().get(0).getPrice());
    }

    @Test
    void findAll_specification_shouldReturnFilteredResults() {
        Product product = new Product();
        product.setIdentifier("SKU001");
        product.setDeleted(false);
        productRepository.save(product);

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Product> spec =
                (root, query, cb) -> cb.conjunction();

        WsDto<ProductDto> result =
                productService.findAll(spec, pageable, "SKU001");

        assertEquals(1, result.getTotalRecords());
        assertEquals("SKU001", result.getKeyword());
        assertEquals("SKU001", result.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findAll_specification_shouldIncludePrice() {
        Product product = new Product();
        product.setIdentifier("SKU001");
        productRepository.save(product);

        Price price = new Price();
        price.setProductId(product.getId());
        priceRepository.save(price);

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Product> spec =
                (root, query, cb) -> cb.conjunction();

        WsDto<ProductDto> result =
                productService.findAll(spec, pageable, "SKU001");

        assertNotNull(result.getDtoList().get(0).getPrice());
    }

    @Test
    void findIfTrue_shouldReturnOnlyActiveProducts() {
        Product active = new Product();
        active.setIdentifier("SKU001");
        active.setStatus(true);
        active.setDeleted(false);
        productRepository.save(active);

        Product inactive = new Product();
        inactive.setIdentifier("SKU002");
        inactive.setStatus(false);
        inactive.setDeleted(false);
        productRepository.save(inactive);

        List<ProductDto> result = productService.findIfTrue();

        assertEquals(1, result.size());
        assertEquals("SKU001", result.get(0).getIdentifier());
    }

    @Test
    void findIfTrue_shouldReturnEmptyWhenNoneActive() {
        Product inactive = new Product();
        inactive.setIdentifier("SKU001");
        inactive.setStatus(false);
        productRepository.save(inactive);

        List<ProductDto> result = productService.findIfTrue();

        assertTrue(result.isEmpty());
    }
}