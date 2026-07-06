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
import org.springframework.data.jpa.domain.Specification;

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
        product.setIdentifier("PROD1");
        product.setStatus(true);
        product.setDeleted(false);

        productDto = new ProductDto();
        productDto.setIdentifier("PROD1");
    }

    @Test
    void testUpdate() {

        when(productRepository.findByIdentifier("PROD1"))
                .thenReturn(product);

        doNothing().when(modelMapper)
                .map(productDto, product);

        ProductDto result = productService.update(productDto);

        assertNotNull(result);

        verify(productRepository).save(product);
    }

    @Test
    void testFindAll() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> page =
                new PageImpl<>(Collections.singletonList(product));

        when(productRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(productDto));

        WsDto<ProductDto> result =
                productService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void testFindAllWithSpecification() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Product> specification =
                mock(Specification.class);

        Page<Product> page =
                new PageImpl<>(Collections.singletonList(product));

        when(productRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(productDto));

        WsDto<ProductDto> result =
                productService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());

        verify(productRepository)
                .findAll(specification, pageable);
    }

    @Test
    void testSave_NewProduct() {

        when(productRepository.findByIdentifier("PROD1"))
                .thenReturn(null);

        when(modelMapper.map(productDto, Product.class))
                .thenReturn(product);

        ProductDto result = productService.save(productDto);

        assertNotNull(result);

        verify(productRepository).save(product);
    }

    @Test
    void testSave_AlreadyExists() {

        when(productRepository.findByIdentifier("PROD1"))
                .thenReturn(product);

        ProductDto result = productService.save(productDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_SoftDeleted() {

        product.setDeleted(true);

        when(productRepository.findByIdentifier("PROD1"))
                .thenReturn(product);

        ProductDto result = productService.save(productDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("soft deleted"));
    }

    @Test
    void testDelete() {

        when(productRepository.findByIdentifier("PROD1"))
                .thenReturn(product);

        productService.delete("PROD1");

        assertTrue(product.isDeleted());
        assertFalse(product.isStatus());

        verify(productRepository).save(product);
    }

    @Test
    void testFindByIdentifier() {

        when(productRepository.findByIdentifier("PROD1"))
                .thenReturn(product);

        when(modelMapper.map(product, ProductDto.class))
                .thenReturn(productDto);

        ProductDto result =
                productService.findByIdentifier("PROD1");

        assertNotNull(result);
        assertEquals("PROD1", result.getIdentifier());
    }

    @Test
    void testChangeToggleStatus() {

        when(productRepository.findByIdentifier("PROD1"))
                .thenReturn(product);

        when(modelMapper.map(product, ProductDto.class))
                .thenReturn(productDto);

        ProductDto result =
                productService.changeToggleStatus("PROD1", false);

        assertNotNull(result);
        assertFalse(product.isStatus());

        verify(productRepository).save(product);
    }

    @Test
    void testChangeToggleStatus_ProductNotFound() {

        when(productRepository.findByIdentifier("PROD1"))
                .thenReturn(null);

        when(modelMapper.map(null, ProductDto.class))
                .thenReturn(null);

        ProductDto result =
                productService.changeToggleStatus("PROD1", false);

        assertNull(result);
    }

    @Test
    void testFindActiveStatus() {

        Product inactiveProduct = new Product();
        inactiveProduct.setStatus(false);

        when(productRepository.findAll())
                .thenReturn(List.of(product, inactiveProduct));

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(productDto));

        List<ProductDto> result =
                productService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}