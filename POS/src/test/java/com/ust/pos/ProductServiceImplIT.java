package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
        dto.setIdentifier("PRD001");
        dto.setStatus(true);
        ProductDto response = productService.save(dto);
        Product saved = productRepository.findByIdentifier("PRD001");
        assertNotNull(saved);
        assertEquals("PRD001", saved.getIdentifier());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {

        Product product = new Product();
        product.setIdentifier("PRD001");
        product.setDeleted(false);

        productRepository.save(product);

        ProductDto dto = new ProductDto();
        dto.setIdentifier("PRD001");
        ProductDto response = productService.save(dto);
        assertFalse(response.isSuccess());
        assertEquals("Product with identifier - PRD001 already exists", response.getMessage());
    }

    @Test
    void update_shouldUpdateStatus() {

        Product product = new Product();
        product.setIdentifier("PRD001");
        product.setStatus(true);
        product.setDeleted(false);

        productRepository.save(product);
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PRD001");
        dto.setStatus(false);

        ProductDto response = productService.update(dto);
        assertTrue(response.isSuccess());
        Product updated = productRepository.findByIdentifier("PRD001");
        assertFalse(updated.isStatus());
    }

    @Test
    void update_shouldFailWhenProductNotFound() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("INVALID");
        ProductDto response = productService.update(dto);
        assertFalse(response.isSuccess());
        assertEquals("Product with identifier - INVALID not found", response.getMessage());
    }

    @Test
    void findByIdentifier_shouldReturnProduct() {
        Product product = new Product();
        product.setIdentifier("PRD001");
        productRepository.save(product);
        ProductDto result = productService.findByIdentifier("PRD001");
        assertEquals("PRD001", result.getIdentifier());
    }

    @Test
    void delete_shouldSoftDelete() {

        Product product = new Product();
        product.setIdentifier("PRD001");
        product.setDeleted(false);
        productRepository.save(product);
        productService.delete("PRD001");
        Product deleted = productRepository.findByIdentifier("PRD001");
        assertTrue(deleted.isDeleted());
    }

    @Test
    void toggleStatus_shouldUpdateStatus() {

        Product product = new Product();
        product.setIdentifier("PRD001");
        product.setStatus(false);
        productRepository.save(product);
        ProductDto response = productService.toggleStatus("PRD001", true);
        assertTrue(response.isStatus());
        Product updated = productRepository.findByIdentifier("PRD001");
        assertTrue(updated.isStatus());
    }

    @Test
    void findActiveProducts_shouldReturnOnlyActiveProducts() {

        Product activeProduct = new Product();
        activeProduct.setIdentifier("PRD001");
        activeProduct.setStatus(true);

        Product inactiveProduct = new Product();
        inactiveProduct.setIdentifier("PRD002");
        inactiveProduct.setStatus(false);
        productRepository.save(activeProduct);
        productRepository.save(inactiveProduct);
        List<ProductDto> activeProducts = productService.findActiveProducts();
        assertEquals(1, activeProducts.size());
        assertEquals("PRD001", activeProducts.get(0).getIdentifier());
    }
}