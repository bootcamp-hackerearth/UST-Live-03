package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
        dto.setIdentifier("  PROD001  "); // Intentional trailing space to test trim logic
        dto.setStatus(true);

        ProductDto response = productService.save(dto);

        assertNotNull(response);
        assertEquals("PROD001", response.getIdentifier());

        Product saved = productRepository.findByIdentifier("PROD001");
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
        assertEquals("Product with identifier - PROD001 already exists", response.getMessage());
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
        assertEquals("Node with identifier PROD001 was previously deleted. Please contact backend team to restore.", response.getMessage());
    }

    @Test
    void update_shouldUpdateProductDetails() {
        Product product = new Product();
        product.setIdentifier("PROD001");
        product.setStatus(true);
        product.setDeleted(false);
        productRepository.save(product);

        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD001");
        dto.setStatus(false);

        ProductDto response = productService.update(dto);
        assertTrue(response.isSuccess());

        Product updated = productRepository.findByIdentifier("PROD001");
        assertFalse(updated.isStatus());
    }

    @Test
    void update_shouldReturnErrorIfNotFound() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("NON-EXISTENT");

        ProductDto response = productService.update(dto);
        assertFalse(response.isSuccess());
        assertEquals("Product with identifier - NON-EXISTENT not found", response.getMessage());
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
    void findByIdentifier_shouldThrowExceptionWhenNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.findByIdentifier("NON-EXISTENT");
        });
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

        boolean isDeleted = productService.delete("PROD001");
        assertTrue(isDeleted);

        Product deleted = productRepository.findByIdentifier("PROD001");
        assertTrue(deleted.isDeleted());
    }

    @Test
    void delete_shouldReturnFalseWhenNotFound() {
        boolean isDeleted = productService.delete("NON-EXISTENT");
        assertFalse(isDeleted);
    }

    @Test
    void findAll_shouldReturnPaginatedData() {
        Product product1 = new Product();
        product1.setIdentifier("PROD001");
        product1.setDeleted(false);
        productRepository.save(product1);

        Pageable pageable = PageRequest.of(0, 10);
        WsDto<ProductDto> response = productService.findAll(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalRecords());
    }

    @Test
    void findIfTrue_shouldReturnActiveAndNonDeletedRecords() {
        Product activeProduct = new Product();
        activeProduct.setIdentifier("PROD001");
        activeProduct.setStatus(true);
        activeProduct.setDeleted(false);
        productRepository.save(activeProduct);

        Product inactiveProduct = new Product();
        inactiveProduct.setIdentifier("PROD002");
        inactiveProduct.setStatus(false);
        inactiveProduct.setDeleted(false);
        productRepository.save(inactiveProduct);

        List<ProductDto> activeList = productService.findIfTrue();
        assertEquals(1, activeList.size());
        assertEquals("PROD001", activeList.get(0).getIdentifier());
    }
}