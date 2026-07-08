package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Example;
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

    @Autowired
    private EntityManager entityManager;

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
    void update_shouldInsertDuplicateRowInsteadOfUpdating() {
        Product product = new Product();
        product.setIdentifier("PROD001");
        product.setDeleted(false);
        product.setDescription("Old Description");
        product.setStatus(false);
        productRepository.save(product);

        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD001");
        dto.setDescription("New Description");
        dto.setStatus(true);

        productService.update(dto);

        assertThrows(
                IncorrectResultSizeDataAccessException.class,
                () -> productRepository.findByIdentifier("PROD001")
        );
    }

    @Test
    void update_shouldFailWhenNotFound() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD_MISSING");

        ProductDto response = productService.update(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Product with identifier - PROD_MISSING is not found",
                response.getMessage()
        );
    }

    @Test
    void findByIdentifier_shouldReturnProduct() {
        Product product = new Product();
        product.setIdentifier("PROD001");
        product.setDeleted(false);
        productRepository.save(product);

        ProductDto result = productService.findByIdentifier("PROD001");

        assertNotNull(result);
        assertEquals("PROD001", result.getIdentifier());
    }

    @Test
    void delete_shouldSoftDelete() {
        Product product = new Product();
        product.setIdentifier("PROD001");
        product.setDeleted(false);
        productRepository.save(product);

        productService.delete("PROD001");
        Boolean deletedFlag = (Boolean) entityManager
                .createNativeQuery("SELECT deleted FROM product WHERE identifier = :identifier")
                .setParameter("identifier", "PROD001")
                .getSingleResult();

        assertTrue(deletedFlag);
    }

    @Test
    void delete_shouldDoNothingWhenProductNotFound() {
        assertDoesNotThrow(() -> productService.delete("PROD_MISSING"));
    }

    @Test
    void findAll_shouldReturnOnlyNonDeletedProducts() {
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
    void findAll_shouldSearchProducts() {
        Product product = new Product();
        product.setIdentifier("PROD001");
        product.setDeleted(false);
        productRepository.save(product);

        Pageable pageable = PageRequest.of(0, 10);

        Product probe = new Product();
        probe.setIdentifier("PROD001");
        Example<Product> example = Example.of(probe);

        Page<ProductDto> result = productService.findAll(example, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("PROD001", result.getContent().get(0).getIdentifier());
    }

    @Test
    void findAll_shouldReturnAllNonDeletedProducts() {
        Product active = new Product();
        active.setIdentifier("PROD001");
        active.setDeleted(false);
        productRepository.save(active);

        Product deleted = new Product();
        deleted.setIdentifier("PROD002");
        deleted.setDeleted(true);
        productRepository.save(deleted);

        List<ProductDto> result = productService.findAll();

        assertEquals(1, result.size());
        assertEquals("PROD001", result.get(0).getIdentifier());
    }
}