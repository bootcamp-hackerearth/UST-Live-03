package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setIdentifier("PROD-001");
        product.setStatus(true);
        product.setDeleted(false);

        productDto = new ProductDto();
        productDto.setIdentifier("PROD-001");
    }

    @Test
    void testFindByIdentifier_Success() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);

        ProductDto result = productService.findByIdentifier("PROD-001");

        assertNotNull(result);
        assertEquals("PROD-001", result.getIdentifier());
    }

    @Test
    void testFindByIdentifier_ThrowsResourceNotFoundException() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> productService.findByIdentifier("PROD-001"));
    }

    @Test
    void testSave_WhenDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> productService.save(null));
    }

    @Test
    void testSave_WhenIdentifierIsNull() {
        productDto.setIdentifier(null);
        assertThrows(IllegalArgumentException.class, () -> productService.save(productDto));
    }

    @Test
    void testSave_WhenProductAlreadyExistsAndNotDeleted() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);

        ProductDto result = productService.save(productDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_WhenProductAlreadyExistsButDeleted() {
        product.setDeleted(true);
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);

        ProductDto result = productService.save(productDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testSave_Success() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(null);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto result = productService.save(productDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Product created successfully", result.getMessage());
    }

    @Test
    void testUpdate_WhenProductNotFound() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(null);

        ProductDto result = productService.update(productDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testUpdate_WhenProductDeleted() {
        product.setDeleted(true);
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);

        ProductDto result = productService.update(productDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testUpdate_Success() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto result = productService.update(productDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Product updated successfully", result.getMessage());
    }

    @Test
    void testDelete_WhenProductNotFound() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(null);

        productService.delete("PROD-001");

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testDelete_Success() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.delete("PROD-001");

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(Collections.singletonList(product), pageable, 1);
        when(productRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<ProductDto> result = productService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertFalse(result.getDtoList().isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testFindAllWithSpecification() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(Collections.singletonList(product), pageable, 1);
        Specification<Product> spec = mock(Specification.class);
        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        WsDto<ProductDto> result = productService.findAll(spec, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void testToggleStatus_WhenProductNotFound() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(null);

        ProductDto result = productService.toggleStatus("PROD-001");

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testToggleStatus_Success() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto result = productService.toggleStatus("PROD-001");

        assertNotNull(result);
        assertFalse(result.isStatus());
    }

    @Test
    void testFindIfTrue() {
        when(productRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(Collections.singletonList(product));

        List<ProductDto> result = productService.findIfTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}