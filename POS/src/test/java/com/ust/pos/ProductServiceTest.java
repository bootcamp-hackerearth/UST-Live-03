package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
        product = new Product();
        product.setIdentifier("P001");
        product.setStatus(true);

        productDto = new ProductDto();
        productDto.setIdentifier("P001");
        productDto.setStatus(true);
    }


    @Test
    void findByIdentifier_shouldReturnProductDto() {
        when(productRepository.findByIdentifier("P001")).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        ProductDto result = productService.findByIdentifier("P001");

        assertNotNull(result);
        assertEquals("P001", result.getIdentifier());
    }


    @Test
    void save_shouldSaveProduct_whenNotExists() {
        when(productRepository.findByIdentifier("P001")).thenReturn(null);
        when(modelMapper.map(productDto, Product.class)).thenReturn(product);

        ProductDto result = productService.save(productDto);

        verify(productRepository).save(product);
        assertEquals("P001", result.getIdentifier());
    }


    @Test
    void save_shouldFail_whenProductAlreadyExists() {
        when(productRepository.findByIdentifier("P001")).thenReturn(product);

        ProductDto result = productService.save(productDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(productRepository, never()).save(any());
    }


    @Test
    void update_shouldUpdateProduct_whenExists() {
        when(productRepository.findByIdentifier("P001")).thenReturn(product);

        ProductDto result = productService.update(productDto);

        verify(modelMapper).map(productDto, product);
        verify(productRepository).save(product);
        assertEquals("P001", result.getIdentifier());
    }

    @Test
    void update_shouldFail_whenProductNotFound() {
        when(productRepository.findByIdentifier("P001")).thenReturn(null);

        ProductDto result = productService.update(productDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(productRepository, never()).save(any());
    }


    @Test
    void delete_shouldDeleteProduct() {
        doNothing().when(productRepository).deleteByIdentifier("P001");

        productService.delete("P001");

        verify(productRepository).deleteByIdentifier("P001");
    }


    @Test
    void findAllTest() {
        Product product1 = new Product();
        product1.setIdentifier("Admin");

        ProductDto productDto1 = new ProductDto();
        productDto1.setIdentifier("Admin");

        List<Product> products = List.of(product1);
        List<ProductDto> productDtos = List.of(productDto1);

        Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 2), products.size());

        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));

        Mockito.when(productRepository.findAll(pageable)).thenReturn(productPage);
        Mockito.when(modelMapper.map(Mockito.eq(products), Mockito.any(java.lang.reflect.Type.class))).thenReturn(productDtos);

        List<ProductDto> response = productService.findAll(pageable).getDtoList();

        Assertions.assertEquals(1, response.size());
    }


    @Test
    void findIfTrue_shouldReturnActiveProducts() {
        List<Product> products = List.of(product);
        List<ProductDto> productDtos = List.of(productDto);

        when(productRepository.findByStatusIsTrue()).thenReturn(products);
        when(modelMapper.map(any(), any(Type.class))).thenReturn(productDtos);

        List<ProductDto> result = productService.findIfTrue();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isStatus());
    }

    @Test
    void toggleStatus_shouldToggleProductStatus() {
        when(productRepository.findByIdentifier("P001")).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        ProductDto result = productService.toggleStatus("P001");

        assertFalse(product.isStatus());
        verify(productRepository).save(product);
        assertNotNull(result);
    }
}