package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Assertions;
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
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierSuccessTest() {
        Product product = new Product();
        product.setIdentifier("PROD001");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD001");

        when(productRepository.findByIdentifier("PROD001"))
                .thenReturn(product);
        when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);

        ProductDto result = productService.findByIdentifier("PROD001");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("PROD001", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(productRepository.findByIdentifier("PROD001"))
                .thenReturn(null);

        ProductDto result = productService.findByIdentifier("PROD001");

        Assertions.assertNull(result);
    }

    @Test
    void saveSuccessTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD001");

        Product product = new Product();

        when(productRepository.findByIdentifier("PROD001"))
                .thenReturn(null);
        when(modelMapper.map(dto, Product.class))
                .thenReturn(product);

        ProductDto result = productService.save(dto);

        Assertions.assertEquals("PROD001", result.getIdentifier());

        verify(productRepository).save(product);
    }

    @Test
    void saveAlreadyExistsTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD001");

        Product existingProduct = new Product();
        existingProduct.setDeleted(false);

        when(productRepository.findByIdentifier("PROD001"))
                .thenReturn(existingProduct);

        ProductDto result = productService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Product with identifier - PROD001 already exists",
                result.getMessage()
        );

        verify(productRepository, never()).save(any());
    }

    @Test
    void saveDeletedProductTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD001");

        Product existingProduct = new Product();
        existingProduct.setDeleted(true);

        when(productRepository.findByIdentifier("PROD001"))
                .thenReturn(existingProduct);

        ProductDto result = productService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Product with identifier - PROD001 was deleted , Please Contact the Administrator to add.",
                result.getMessage()
        );

        verify(productRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD001");

        Product existingProduct = new Product();
        existingProduct.setIdentifier("PROD001");

        when(productRepository.findByIdentifier("PROD001"))
                .thenReturn(existingProduct);

        ProductDto result = productService.update(dto);

        Assertions.assertEquals("PROD001", result.getIdentifier());

        verify(modelMapper).map(dto, existingProduct);
        verify(productRepository).save(existingProduct);
    }

    @Test
    void updateFailureTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD001");

        when(productRepository.findByIdentifier("PROD001"))
                .thenReturn(null);

        ProductDto result = productService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Product with identifier - PROD001 not found",
                result.getMessage()
        );

        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Product product = new Product();

        when(productRepository.findByIdentifier("PROD001"))
                .thenReturn(product);

        productService.delete("PROD001");

        verify(productRepository).findByIdentifier("PROD001");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Product product1 = new Product();
        Product product2 = new Product();

        List<Product> products = List.of(
                product1,
                product2
        );

        Page<Product> page = new PageImpl<>(
                products,
                pageable,
                2
        );

        List<ProductDto> dtoList = List.of(
                new ProductDto(),
                new ProductDto()
        );

        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();

        when(productRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(products, listType))
                .thenReturn(dtoList);

        WsDto<ProductDto> result = productService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getDtoList().size());
        Assertions.assertEquals(2, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }
}