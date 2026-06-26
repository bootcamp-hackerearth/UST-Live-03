package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @Spy
    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setIdentifier("P1");
        product.setStatus(true);
        product.setDeleted(false);

        productDto = new ProductDto();
        productDto.setIdentifier("P1");
    }

    // ✅ UPDATE
    @Test
    void testUpdate() {
        when(productRepository.findByIdentifier("P1")).thenReturn(product);

        doNothing().when(modelMapper).map(productDto, product);
        doNothing().when(productService).setAuditFields(product, false);

        ProductDto result = productService.update(productDto);

        assertNotNull(result);
        verify(productRepository).save(product);
    }

    // ✅ FIND ALL (Pagination)
    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(Collections.singletonList(product));

        when(productRepository.findByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(productDto));

        WsDto<ProductDto> result = productService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    // ✅ SAVE - NEW PRODUCT
    @Test
    void testSave_NewProduct() {
        when(productRepository.findByIdentifier("P1")).thenReturn(null);
        when(modelMapper.map(productDto, Product.class)).thenReturn(product);

        doNothing().when(productService).setAuditFields(product, true);

        ProductDto result = productService.save(productDto);

        assertNotNull(result);
        verify(productRepository).save(product);
    }

    // ✅ SAVE - ALREADY EXISTS
    @Test
    void testSave_AlreadyExists() {
        when(productRepository.findByIdentifier("P1")).thenReturn(product);

        ProductDto result = productService.save(productDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    // ✅ SAVE - SOFT DELETED
    @Test
    void testSave_SoftDeleted() {
        product.setDeleted(true);

        when(productRepository.findByIdentifier("P1")).thenReturn(product);

        ProductDto result = productService.save(productDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("soft deleted"));
    }

    // ✅ DELETE
    @Test
    void testDelete() {
        when(productRepository.findByIdentifier("P1")).thenReturn(product);

        doNothing().when(productService).softDelete(product);
        doNothing().when(productService).setAuditFields(product, false);

        productService.delete("P1");

        verify(productRepository).save(product);
    }

    // ✅ FIND BY IDENTIFIER
    @Test
    void testFindByIdentifier() {
        when(productRepository.findByIdentifier("P1")).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        ProductDto result = productService.findByIdentifier("P1");

        assertNotNull(result);
    }

    // ✅ CHANGE TOGGLE STATUS
    @Test
    void testChangeToggleStatus() {
        when(productRepository.findByIdentifier("P1")).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        ProductDto result = productService.changeToggleStatus("P1", false);

        assertNotNull(result);
        assertFalse(product.isStatus());
        verify(productRepository).save(product);
    }

    // ✅ FIND ACTIVE STATUS
    @Test
    void testFindActiveStatus() {
        product.setStatus(true);

        Product inactive = new Product();
        inactive.setStatus(false);

        List<Product> products = List.of(product, inactive);

        when(productRepository.findAll()).thenReturn(products);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(productDto));

        List<ProductDto> result = productService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
