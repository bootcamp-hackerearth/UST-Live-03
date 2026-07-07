package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

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

        ProductDto response = productService.save(dto);

        Product saved =
                productRepository.findByIdentifier("PROD001");

        assertTrue(response.isSuccess());
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
    void save_shouldFailWhenProductIsSoftDeleted() {

        Product product = new Product();
        product.setIdentifier("PROD001");
        product.setDeleted(true);

        productRepository.save(product);

        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD001");

        ProductDto response = productService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Product with identifier PROD001 has been soft deleted. (Rollback by changing status)",
                response.getMessage()
        );
    }

    @Test
    void update_shouldUpdateProduct() {

        Product product = new Product();
        product.setIdentifier("PROD001");

        productRepository.save(product);

        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD001");
        // Set any fields that exist in Product/ProductDto
        dto.setDescription("Updated Product");

        ProductDto response = productService.update(dto);

        assertTrue(response.isSuccess());

        Product updated =
                productRepository.findByIdentifier("PROD001");

        assertEquals("Updated Product", updated.getDescription());
    }

    @Test
    void update_shouldFailWhenProductNotFound() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD999");

        ProductDto response = productService.update(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Product with identifier - PROD999 not found",
                response.getMessage()
        );
    }

    @Test
    void findByIdentifier_shouldReturnProduct() {

        Product product = new Product();
        product.setIdentifier("PROD001");

        productRepository.save(product);

        ProductDto result =
                productService.findByIdentifier("PROD001");

        assertNotNull(result);
        assertEquals("PROD001", result.getIdentifier());
    }

    @Test
    void deleteByIdentifier_shouldSoftDelete() {

        Product product = new Product();
        product.setIdentifier("PROD001");
        product.setDeleted(false);

        productRepository.save(product);

        productService.deleteByIdentifier("PROD001");

        Product deleted =
                productRepository.findByIdentifier("PROD001");

        assertTrue(deleted.isDeleted());
    }

    @Test
    void findAll_shouldReturnProducts() {

        Product product1 = new Product();
        product1.setIdentifier("PROD001");
        product1.setDeleted(false);

        Product product2 = new Product();
        product2.setIdentifier("PROD002");
        product2.setDeleted(false);

        productRepository.save(product1);
        productRepository.save(product2);

        Pageable pageable = PageRequest.of(0, 10);

        WsDto<ProductDto> result =
                productService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalRecords());
        assertEquals(2, result.getDtoList().size());
    }
}