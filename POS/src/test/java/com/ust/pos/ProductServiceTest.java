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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
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
    void findByIdentifierTest() {
        Product product = new Product();
        ProductDto dto = new ProductDto();
        Mockito.when(productRepository.findByIdentifier("P1")).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);
        ProductDto response = productService.findByIdentifier("P1");
        Assertions.assertNotNull(response);
    }

    @Test
    void saveTest_Success() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        Product product = new Product();
        Mockito.when(productRepository.findByIdentifier("P1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Product.class)).thenReturn(product);
        ProductDto response = productService.save(dto);
        Assertions.assertEquals("P1", response.getIdentifier());
        Mockito.verify(productRepository).save(product);
    }

    @Test
    void saveTest_Duplicate() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        Mockito.when(productRepository.findByIdentifier("P1")).thenReturn(new Product());
        ProductDto response = productService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("already exists"));
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateTest_Success() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        Product existing = new Product();
        Mockito.when(productRepository.findByIdentifier("P1")).thenReturn(existing);
        ProductDto response = productService.update(dto);
        Assertions.assertNotNull(response);
        Mockito.verify(productRepository).save(existing);
    }

    @Test
    void updateTest_NotFound() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        Mockito.when(productRepository.findByIdentifier("P1")).thenReturn(null);
        ProductDto response = productService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("not found"));
    }

    @Test
    void deleteTest() {
        productService.delete("P1");
        Mockito.verify(productRepository).deleteByIdentifier("P1");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 5);
        Product product = new Product();
        product.setIdentifier("P1");

        List<Product> productList = List.of(product);
        Page<Product> productPage = new PageImpl<>(productList);

        Mockito.when(productRepository.findAll(pageable)).thenReturn(productPage);
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("P1");
        List<ProductDto> productDtoList = List.of(productDto);
        Mockito.when(modelMapper.map(Mockito.eq(productPage.getContent()), Mockito.any(Type.class))).thenReturn(productDtoList);
        WsDto<ProductDto> result = productService.findAll(pageable);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals("P1", result.getDtoList().get(0).getIdentifier());
        Mockito.verify(productRepository).findAll(pageable);
    }

    @Test
    void toggleStatus_Success() {
        Product product = new Product();
        product.setStatus(true);
        Mockito.when(productRepository.findByIdentifier("P1")).thenReturn(product);
        productService.toggleStatus("P1");
        Assertions.assertFalse(product.isStatus());
        Mockito.verify(productRepository).save(product);
    }

    @Test
    void toggleStatus_NotFound() {
        Mockito.when(productRepository.findByIdentifier("P1")).thenReturn(null);
        Assertions.assertThrows(NullPointerException.class, () -> productService.toggleStatus("P1"));
    }

    @Test
    void findActiveProductsTest() {
        List<Product> products = List.of(new Product());
        List<ProductDto> dtos = List.of(new ProductDto());
        Mockito.when(productRepository.findByStatusTrue()).thenReturn(products);
        Mockito.when(modelMapper.map(Mockito.eq(products), Mockito.any(java.lang.reflect.Type.class))).thenReturn(dtos);
        List<ProductDto> response = productService.findActiveProducts();
        Assertions.assertEquals(1, response.size());
    }

}

