package com.ust.pos;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.ProductDto;
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
    void saveTest() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Lays Chili");
        Mockito.when(productRepository.findByIdentifier("Lays Chili")).thenReturn(null);
        Product product = new Product();
        Mockito.when(modelMapper.map(productDto, Product.class)).thenReturn(product);
        Mockito.when(productRepository.save(product)).thenReturn(product);
        ProductDto response = productService.save(productDto);
        Assertions.assertEquals("Lays Chili", response.getIdentifier());
        Assertions.assertEquals(true, response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Lays Chili");
        Product product = new Product();
        Mockito.when(productRepository.findByIdentifier("Lays Chili")).thenReturn(product);
        ProductDto response = productService.save(productDto);
        Assertions.assertEquals("Lays Chili", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");
        Assertions.assertEquals(false, response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Product product = new Product();
        product.setIdentifier("Lays Chili");
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Lays Chili");
        Mockito.when(productRepository.findByIdentifier("Lays Chili")).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);
        ProductDto response = productService.findByIdentifier("Lays Chili");
        Assertions.assertEquals("Lays Chili", response.getIdentifier());
    }

    @Test
    void updateTest() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Lays Chili");
        Product existingProduct = new Product();
        existingProduct.setIdentifier("Lays Chili");
        Mockito.when(productRepository.findByIdentifier("Lays Chili")).thenReturn(existingProduct);
        Mockito.when(productRepository.save(existingProduct)).thenReturn(existingProduct);
        ProductDto response = productService.update(productDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Lays Chili");
        Mockito.when(productRepository.findByIdentifier("Lays Chili")).thenReturn(null);
        ProductDto response = productService.update(productDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(productRepository).deleteByIdentifier("Lays Chili");
        productService.deleteByIdentifier("Lays Chili");
        Mockito.verify(productRepository).deleteByIdentifier("Lays Chili");
    }

    @Test
    void findAllWithPageableTest() {
        Product product = new Product();
        product.setIdentifier("Lays Chili");
        ProductDto dto = new ProductDto();
        dto.setIdentifier("Lays Chili");
        List<Product> products = List.of(product);
        List<ProductDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Product> productPage = new PageImpl<>(products);
        Mockito.when(productRepository.findAll(pageable)).thenReturn(productPage);
        Mockito.when(modelMapper.map(Mockito.eq(products), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<ProductDto> response = productService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Lays Chili", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Product product = new Product();
        product.setIdentifier("Lays Chili");
        ProductDto dto = new ProductDto();
        dto.setIdentifier("Lays Chili");
        List<Product> products = List.of(product);
        List<ProductDto> dtos = List.of(dto);
        Mockito.when(productRepository.findAll()).thenReturn(products);
        Mockito.when(modelMapper.map(Mockito.eq(products), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<ProductDto> response = productService.findAll(null);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Lays Chili", response.getDtoList().get(0).getIdentifier());
    }
}