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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {

        productDto = new ProductDto();
        productDto.setIdentifier("PROD-001");
        productDto.setSuccess(true);

        product = new Product();
        product.setIdentifier("PROD-001");
        product.setStatus(true);
    }

    @Test
    void testSave_ProductAlreadyExists() {

        when(productRepository.findByIdentifier("PROD-001"))
                .thenReturn(product);

        ProductDto result = productService.save(productDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());

        assertEquals(
                "Product with identifier - PROD-001 already exists",
                result.getMessage()
        );

        verify(productRepository, times(1))
                .findByIdentifier("PROD-001");

        verify(productRepository, never())
                .save(any());

        verify(modelMapper, never())
                .map(any(), any());
    }

    @Test
    void testSave_NewProduct() {

        when(productRepository.findByIdentifier("PROD-001"))
                .thenReturn(null);

        when(modelMapper.map(productDto, Product.class))
                .thenReturn(product);

        ProductDto result = productService.save(productDto);

        assertNotNull(result);

        verify(productRepository, times(1))
                .findByIdentifier("PROD-001");

        verify(modelMapper, times(1))
                .map(productDto, Product.class);

        verify(productRepository, times(1))
                .save(product);
    }

    @Test
    void testUpdate_ProductNotFound() {

        when(productRepository.findByIdentifier("PROD-001"))
                .thenReturn(null);

        ProductDto result = productService.update(productDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());

        assertEquals(
                "product with identifier - PROD-001 not found",
                result.getMessage()
        );

        verify(productRepository, times(1))
                .findByIdentifier("PROD-001");

        verify(productRepository, never())
                .save(any());

        verify(modelMapper, never())
                .map(any(), any());
    }

    @Test
    void testUpdate_ProductFound() {

        when(productRepository.findByIdentifier("PROD-001"))
                .thenReturn(product);

        ProductDto result = productService.update(productDto);

        assertNotNull(result);

        verify(productRepository, times(1))
                .findByIdentifier("PROD-001");

        verify(modelMapper, times(1))
                .map(productDto, product);

        verify(productRepository, times(1))
                .save(product);
    }

    @Test
    void testDelete() {

        doNothing().when(productRepository)
                .deleteByIdentifier("PROD-001");

        productService.delete("PROD-001");

        verify(productRepository, times(1))
                .deleteByIdentifier("PROD-001");
    }

    @Test
    void testFindAll_WithData() {

        Pageable pageable = PageRequest.of(0, 50);

        List<Product> productList =
                Collections.singletonList(product);

        List<ProductDto> productDtoList =
                Collections.singletonList(productDto);

        Page<Product> productPage =
                new PageImpl<>(productList, pageable, productList.size());

        Type listType =
                new TypeToken<List<ProductDto>>() {
                }.getType();

        when(productRepository.findAll(pageable))
                .thenReturn(productPage);

        when(modelMapper.map(productPage.getContent(), listType))
                .thenReturn(productDtoList);

        WsDto<ProductDto> result =
                productService.findAll(pageable);

        assertNotNull(result);

        assertNotNull(result.getDtoList());
        assertEquals(1, result.getDtoList().size());

        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(50, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(productRepository, times(1))
                .findAll(pageable);

        verify(modelMapper, times(1))
                .map(productPage.getContent(), listType);
    }

    @Test
    void testFindAll_EmptyList() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> emptyPage =
                new PageImpl<>(Collections.emptyList(), pageable, 0);

        Type listType =
                new TypeToken<List<ProductDto>>() {
                }.getType();

        when(productRepository.findAll(pageable))
                .thenReturn(emptyPage);

        when(modelMapper.map(emptyPage.getContent(), listType))
                .thenReturn(Collections.emptyList());

        WsDto<ProductDto> result =
                productService.findAll(pageable);

        assertNotNull(result);
        assertNotNull(result.getDtoList());
        assertTrue(result.getDtoList().isEmpty());

        verify(productRepository, times(1))
                .findAll(pageable);

        verify(modelMapper, times(1))
                .map(emptyPage.getContent(), listType);
    }

    @Test
    void testFindByIdentifier_Found() {

        when(productRepository.findByIdentifier("PROD-001"))
                .thenReturn(product);

        when(modelMapper.map(product, ProductDto.class))
                .thenReturn(productDto);

        ProductDto result =
                productService.findByIdentifier("PROD-001");

        assertNotNull(result);
        assertEquals("PROD-001", result.getIdentifier());

        verify(productRepository, times(1))
                .findByIdentifier("PROD-001");

        verify(modelMapper, times(1))
                .map(product, ProductDto.class);
    }

    @Test
    void testFindByIdentifier_NotFound() {

        when(productRepository.findByIdentifier("PROD-001"))
                .thenReturn(null);

        when(modelMapper.map(null, ProductDto.class))
                .thenReturn(null);

        ProductDto result =
                productService.findByIdentifier("PROD-001");

        assertNull(result);

        verify(productRepository, times(1))
                .findByIdentifier("PROD-001");

        verify(modelMapper, times(1))
                .map(null, ProductDto.class);
    }

    @Test
    void testFindActiveProducts() {

        when(productRepository.findByStatus(true))
                .thenReturn(Collections.singletonList(product));

        List<Product> result =
                productService.findActiveProducts();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(productRepository, times(1))
                .findByStatus(true);
    }

    @Test
    void testToggleStatus_TrueToFalse() {

        product.setStatus(true);

        when(productRepository.findByIdentifier("PROD-001"))
                .thenReturn(product);

        productService.toggleStatus("PROD-001");

        assertFalse(product.isStatus());

        verify(productRepository, times(1))
                .save(product);
    }

    @Test
    void testToggleStatus_FalseToTrue() {

        product.setStatus(false);

        when(productRepository.findByIdentifier("PROD-001"))
                .thenReturn(product);

        productService.toggleStatus("PROD-001");

        assertTrue(product.isStatus());

        verify(productRepository, times(1))
                .save(product);
    }

    @Test
    void testToggleStatus_ProductNotFound() {

        when(productRepository.findByIdentifier("PROD-001"))
                .thenReturn(null);

        productService.toggleStatus("PROD-001");

        verify(productRepository, times(1))
                .findByIdentifier("PROD-001");

        verify(productRepository, never())
                .save(any());
    }
}