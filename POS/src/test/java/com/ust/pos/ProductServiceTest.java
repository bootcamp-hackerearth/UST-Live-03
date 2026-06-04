package com.ust.pos;

import com.ust.pos.dto.ProductDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock(lenient = true)
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PRD01");

        Product product = new Product();
        product.setIdentifier("PRD01");

        when(productRepository.findByIdentifier("PRD01")).thenReturn(null);
        when(modelMapper.map(dto, Product.class)).thenReturn(product);

        ProductDto response = productService.save(dto);

        assertEquals("PRD01", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        verify(productRepository).save(product);
    }

    @Test
    void saveFailureTest() {
        Product product = new Product();
        product.setIdentifier("PRD01");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("PRD01");

        when(productRepository.findByIdentifier("PRD01")).thenReturn(product);

        ProductDto response = productService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        Product product = new Product();
        product.setIdentifier("PRD01");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("PRD01");

        when(productRepository.findByIdentifier("PRD01")).thenReturn(product);

        ProductDto response = productService.update(dto);

        assertEquals("PRD01", response.getIdentifier());
        verify(modelMapper).map(dto, product);
        verify(productRepository).save(product);
    }

    @Test
    void updateFailureTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PRD01");

        when(productRepository.findByIdentifier("PRD01")).thenReturn(null);

        ProductDto response = productService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        productService.delete("PRD01");
        verify(productRepository).deleteByIdentifier("PRD01");
    }

    @Test
    void findByIdentifierSuccessTest() {
        Product product = new Product();
        product.setIdentifier("PRD01");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("PRD01");

        when(productRepository.findByIdentifier("PRD01")).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);

        ProductDto response = productService.findByIdentifier("PRD01");

        assertNotNull(response);
        assertEquals("PRD01", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(productRepository.findByIdentifier("PRD01")).thenReturn(null);

        ProductDto response = productService.findByIdentifier("PRD01");

        Assertions.assertNull(response);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Product> products = List.of(new Product(), new Product());
        Page<Product> page = new PageImpl<>(products);

        List<ProductDto> dtoList = List.of(new ProductDto(), new ProductDto());

        when(productRepository.findAll(pageable)).thenReturn(page);

        when(modelMapper.map(
                eq(products),
                any(Type.class)
        )).thenReturn(dtoList);

        List<ProductDto> result = productService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(productRepository).findAll(pageable);
    }
}