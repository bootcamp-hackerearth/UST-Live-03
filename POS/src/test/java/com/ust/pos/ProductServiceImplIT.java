package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

        productService.save(dto);

        Product saved = productRepository.findByIdentifier("PROD001");

        assertNotNull(saved);
        assertEquals("PROD001", saved.getIdentifier());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {
        Product product = new Product();
        product.setIdentifier("PROD001");
        product.setDelete(false);
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
    void update_shouldModifyProductDetails() {
        Product product = new Product();
        product.setIdentifier("PROD001");
        product.setDelete(false);
        product.setDescription("Old Description");
        product.setStatus(false);
        productRepository.save(product);

        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD001");
        dto.setDescription("New Description");
        dto.setStatus(true);

        ProductDto response = productService.update(dto);

        Product updated = productRepository.findByIdentifier("PROD001");

        assertTrue(response.isSuccess());
        assertEquals("New Description", updated.getDescription());
        assertTrue(updated.getStatus());
    }

    @Test
    void update_shouldFailWhenNotFound() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD_MISSING");

        ProductDto response = productService.update(dto);

        assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifier_shouldReturnProduct() {
        Product product = new Product();
        product.setIdentifier("PROD001");
        product.setDelete(false);
        productRepository.save(product);

        ProductDto result = productService.findByIdentifier("PROD001");

        assertNotNull(result);
        assertEquals("PROD001", result.getIdentifier());
    }

    @Test
    void toggleStatus_shouldToggleValue() {
        Product product = new Product();
        product.setIdentifier("PROD001");
        product.setDelete(false);
        product.setStatus(true);
        productRepository.save(product);

        productService.toggleStatus("PROD001");

        Product updated = productRepository.findByIdentifier("PROD001");

        assertFalse(updated.getStatus());
    }

    @Test
    void delete_shouldSoftDelete() {
        Product product = new Product();
        product.setIdentifier("PROD001");
        product.setDelete(false);
        productRepository.save(product);

        productService.delete("PROD001");

        Product deleted = productRepository.findByIdentifier("PROD001");

        assertTrue(deleted.isDelete());
    }

    @Test
    void delete_shouldDoNothingWhenProductNotFound() {
        assertDoesNotThrow(() -> productService.delete("PROD_MISSING"));
    }

    @Test
    void findAll_shouldReturnOnlyNonDeleteProducts() {
        Product active = new Product();
        active.setIdentifier("PROD001");
        active.setDelete(false);
        productRepository.save(active);

        Product deleted = new Product();
        deleted.setIdentifier("PROD002");
        deleted.setDelete(true);
        productRepository.save(deleted);

        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductDto> result = productService.findAll(pageable, null);

        assertEquals(1, result.getTotalElements());
        assertEquals("PROD001", result.getContent().get(0).getIdentifier());
    }

    @Test
    void findAll_shouldSearchProducts() {
        Product product = new Product();
        product.setIdentifier("PROD001");
        product.setDelete(false);
        productRepository.save(product);

        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductDto> result =
                productService.findAll(pageable, "PROD001");

        assertEquals(1, result.getTotalElements());
        assertEquals(
                "PROD001",
                result.getContent().get(0).getIdentifier()
        );
    }

    @Test
    void findAll_shouldReturnAllNonDeleteProducts() {
        Product active = new Product();
        active.setIdentifier("PROD001");
        active.setDelete(false);
        productRepository.save(active);

        Product deleted = new Product();
        deleted.setIdentifier("PROD002");
        deleted.setDelete(true);
        productRepository.save(deleted);

        List<ProductDto> result = productService.findAll();

        assertEquals(1, result.size());
        assertEquals("PROD001", result.get(0).getIdentifier());
    }
}