package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.*;
import com.ust.pos.model.Product;
import com.ust.pos.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @InjectMocks
    private ProductServiceImpl productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD 106");

        Mockito.when(productRepository.findByIdentifier("PROD 106")).thenReturn(null);
        Product product = new Product();
        Mockito.when(modelMapper.map(productDto, Product.class)).thenReturn(product);
        Mockito.when(productRepository.save(product)).thenReturn(product);
        ProductDto response = productService.save(productDto);
        Assertions.assertEquals("PROD 106", response.getIdentifier());
        Assertions.assertEquals(true, response.isSuccess());
    }
    
    @Test
    void saveTestFailure() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD 106");
        Product product = new Product();
        Mockito.when(productRepository.findByIdentifier("PROD 106")).thenReturn(product);
        ProductDto response = productService.save(productDto);
        Assertions.assertEquals("PROD 106", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");
        Assertions.assertEquals(false, response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Product product = new Product();
        product.setIdentifier("PROD 106");
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD 106");
        Mockito.when(productRepository.findByIdentifier("PROD 106")).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);
        ProductDto response = productService.findByIdentifier("PROD 106");
        Assertions.assertEquals("PROD 106", response.getIdentifier());
    }

    @Test
    void updateTest() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD 106");
        Product existingProduct = new Product();
        existingProduct.setIdentifier("PROD 106");
        Mockito.when(productRepository.findByIdentifier("PROD 106")).thenReturn(existingProduct);
        Mockito.when(productRepository.save(existingProduct)).thenReturn(existingProduct);
        ProductDto response = productService.update(productDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD 106");
        Mockito.when(productRepository.findByIdentifier("PROD 106")).thenReturn(null);
        ProductDto response = productService.update(productDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(productRepository).deleteByIdentifier("PROD 106");
        productService.delete("PROD 106");
        Mockito.verify(productRepository).deleteByIdentifier("PROD 106");
    }

    @Test
    void findAllTest() {
        Product product = new Product();
        product.setIdentifier("PROD 106");
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD 106");

        List<Product> products = List.of(product);
        List<ProductDto> productDtos = List.of(productDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

        Mockito.when(productRepository.findAll(pageable)).thenReturn(productPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(products),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(productDtos);
        List<ProductDto> response = productService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("PROD 106", response.get(0).getIdentifier());
    }

    @Test
    void toggleStatusSuccessTest() {
        Product product = new Product();
        product.setIdentifier("PROD 106");
        product.setStatus(false);
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD 106");
        productDto.setStatus(true);

        Mockito.when(productRepository.findByIdentifier("PROD 106")).thenReturn(product);
        Mockito.when(productRepository.save(product)).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);
        ProductDto response = productService.toggleStatus("PROD 106", true);
        Assertions.assertEquals("PROD 106", response.getIdentifier());
        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void changeProductStatusFailureTest() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD 106");
        Mockito.when(productRepository.findByIdentifier("PROD 106")).thenReturn(null);
        ProductDto response = productService.toggleStatus("PROD 106", true);
        Assertions.assertNull(response);
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findActiveProductsTest() {
        Product product = new Product();
        product.setIdentifier("PROD_01");
        product.setStatus(true);
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD_01");
        productDto.setStatus(true);

        List<Product> activeProducts = List.of(product);
        List<ProductDto> activeProductDtos = List.of(productDto);

        Mockito.when(productRepository.findByStatusTrue()).thenReturn(activeProducts);
        Mockito.when(modelMapper.map(
                Mockito.eq(activeProducts),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(activeProductDtos);

        List<ProductDto> response = productService.findActiveProducts();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("PROD_01", response.get(0).getIdentifier());
        Assertions.assertTrue(response.get(0).isStatus());
    }
}