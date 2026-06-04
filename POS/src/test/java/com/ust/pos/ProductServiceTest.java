package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.modell.Product;
import com.ust.pos.modell.ProductRepository;
import com.ust.pos.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.ArrayList;
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
    void saveSuccessTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        Mockito.when(productRepository.findByIdentifier("P1")).thenReturn(null);
        Product entity = new Product();
        Mockito.when(modelMapper.map(dto, Product.class)).thenReturn(entity);
        Mockito.when(productRepository.save(entity)).thenReturn(entity);
        ProductDto response = productService.save(dto);
        Assertions.assertEquals("P1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveFailureTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        Mockito.when(productRepository.findByIdentifier("P1")).thenReturn(new Product());
        ProductDto response = productService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Product product = new Product();
        product.setIdentifier("P1");
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        Mockito.when(productRepository.findByIdentifier("P1")).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);
        ProductDto response = productService.findByIdentifier("P1");
        Assertions.assertEquals("P1", response.getIdentifier());
    }

    @Test
    void updateSuccessTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        Product existing = new Product();
        existing.setIdentifier("P1");
        Mockito.when(productRepository.findByIdentifier("P1")).thenReturn(existing);
        Mockito.when(productRepository.save(existing)).thenReturn(existing);
        ProductDto response = productService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateFailureTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        Mockito.when(productRepository.findByIdentifier("P1")).thenReturn(null);
        ProductDto response = productService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        productService.delete("P1");
        Mockito.verify(productRepository).deleteByIdentifier("P1");
    }

    @Test
    void findAllTest() {
        Product product = new Product();
        product.setIdentifier("Admin");
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Admin");
        List<Product> products = List.of(product);
        List<ProductDto> productDtos = List.of(productDto);
        Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 2), products.size());
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Mockito.when(productRepository.findAll(pageable)).thenReturn(productPage);
        Mockito.when(modelMapper.map(Mockito.eq(products), Mockito.any(java.lang.reflect.Type.class))).thenReturn(productDtos);
        List<ProductDto> response = productService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllActiveTest() {
        Product product = new Product();
        product.setIdentifier("P1");
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        Mockito.when(productRepository.findByStatusTrue()).thenReturn(List.of(product));
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);
        List<ProductDto> response = productService.findAllActive();
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("P1", response.get(0).getIdentifier());
    }

    @Test
    void toggleStatusTrueTest() {
        Product product = new Product();
        product.setIdentifier("P1");
        product.setStatus(true);
        Mockito.when(productRepository.findByIdentifier("P1")).thenReturn(product);
        productService.toggleStatus("P1");
        Assertions.assertFalse(product.getStatus());
        Mockito.verify(productRepository).save(product);
    }

    @Test
    void toggleStatusFalseTest() {
        Product product = new Product();
        product.setIdentifier("P1");
        product.setStatus(false);
        Mockito.when(productRepository.findByIdentifier("P1")).thenReturn(product);
        productService.toggleStatus("P1");
        Assertions.assertTrue(product.getStatus());
        Mockito.verify(productRepository).save(product);
    }

    @Test
    void toggleStatusNullTest() {
        Product product = new Product();
        product.setIdentifier("P1");
        product.setStatus(null);
        Mockito.when(productRepository.findByIdentifier("P1")).thenReturn(product);
        productService.toggleStatus("P1");
        Assertions.assertTrue(product.getStatus());
        Mockito.verify(productRepository).save(product);
    }

    @Test
    void toggleStatusFailureTest() {
        Mockito.when(productRepository.findByIdentifier("P1")).thenReturn(null);
        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> {
            productService.toggleStatus("P1");
        });
        Assertions.assertTrue(ex.getMessage().contains("not found"));
    }
}