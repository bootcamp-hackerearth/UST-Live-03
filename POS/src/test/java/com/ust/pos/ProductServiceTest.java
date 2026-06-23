package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
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

    private Product productEntity;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        productEntity = new Product();
        productEntity.setId(1L);
        productEntity.setIdentifier("PROD-001");
        productEntity.setStatus(true);
        productEntity.setDeleted(false);

        productDto = new ProductDto();
        productDto.setIdentifier("PROD-001");
    }

    @Test
    void testFindByIdentifier() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(productEntity);

        ProductDto result = productService.findByIdentifier("PROD-001");

        assertNotNull(result);
        assertEquals("PROD-001", result.getIdentifier());
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
    void testSave_WhenProductExistsAndNotDeleted() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(productEntity);

        ProductDto result = productService.save(productDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_WhenProductWasPreviouslyDeleted() {
        productEntity.setDeleted(true);
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(productEntity);

        ProductDto result = productService.save(productDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testSave_Success() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(null);
        when(productRepository.save(any(Product.class))).thenReturn(productEntity);

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
    void testUpdate_WhenProductIsDeleted() {
        productEntity.setDeleted(true);
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(productEntity);

        ProductDto result = productService.update(productDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testUpdate_Success() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(productEntity);
        when(productRepository.save(any(Product.class))).thenReturn(productEntity);

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
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(productEntity);
        when(productRepository.save(any(Product.class))).thenReturn(productEntity);

        productService.delete("PROD-001");

        verify(productRepository, times(1)).save(productEntity);
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> entityList = Collections.singletonList(productEntity);
        Page<Product> page = new PageImpl<>(entityList, pageable, 1);

        when(productRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<ProductDto> result = productService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(0, result.getPage());
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
        productEntity.setStatus(true);
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(productEntity);
        when(productRepository.save(any(Product.class))).thenReturn(productEntity);

        ProductDto result = productService.toggleStatus("PROD-001");

        assertNotNull(result);
        assertFalse(result.isStatus());
    }

    @Test
    void testFindIfTrue() {
        List<Product> activeProducts = Collections.singletonList(productEntity);
        when(productRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(activeProducts);

        List<ProductDto> result = productService.findIfTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}