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
        productDto.setIdentifier("S1");
        Mockito.when(productRepository.findByIdentifier("S1")).thenReturn(null);
        Product product = new Product();
        Mockito.when(modelMapper.map(productDto, Product.class)).thenReturn(product);
        Mockito.when(productRepository.save(product)).thenReturn(product);
        ProductDto response = productService.save(productDto);
        Assertions.assertEquals("S1", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("S1");
        Product existingProduct = new Product();
        Mockito.when(productRepository.findByIdentifier("S1")).thenReturn(existingProduct);
        ProductDto response = productService.save(productDto);
        Assertions.assertEquals("S1", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Product product = new Product();
        product.setIdentifier("S1");
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("S1");
        Mockito.when(productRepository.findByIdentifier("S1")).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);
        ProductDto response = productService.findByIdentifier("S1");
        Assertions.assertEquals("S1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("S1");
        Product existingProduct = new Product();
        existingProduct.setIdentifier("S1");
        Mockito.when(productRepository.findByIdentifier("S1"))
                .thenReturn(existingProduct);
        Mockito.when(productRepository.save(existingProduct))
                .thenReturn(existingProduct);
        ProductDto response = productService.update(productDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("S1");
        Mockito.when(productRepository.findByIdentifier("S1"))
                .thenReturn(null);
        ProductDto response = productService.update(productDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(productRepository)
                .deleteByIdentifier("S1");
        productService.delete("S1");
        Mockito.verify(productRepository).deleteByIdentifier("S1");
    }

    @Test
    void findAllTest() {
        Product product = new Product();
        product.setIdentifier("P1");
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("P1");
        List<Product> productList = List.of(product);
        List<ProductDto> productDtoList = List.of(productDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(productList);
        Mockito.when(productRepository.findAll(pageable))
                .thenReturn(productPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(productList),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(productDtoList);
        WsDto<ProductDto> response = productService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("P1", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void updateStatusTest() {
        Product product = new Product();
        product.setIdentifier("S1");
        Mockito.when(productRepository.findByIdentifier("S1"))
                .thenReturn(product);
        Mockito.when(productRepository.save(product))
                .thenReturn(product);
        productService.updateStatus("S1", true);
        Mockito.verify(productRepository).save(product);
    }

    @Test
    void findAllActiveTest() {
        Product product = new Product();
        product.setIdentifier("S1");
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("S1");
        List<Product> shelves = List.of(product);
        List<ProductDto> productDtos = List.of(productDto);
        Mockito.when(productRepository.findByStatus(true)).thenReturn(shelves);
        Mockito.when(modelMapper.map(
                Mockito.eq(shelves),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(productDtos);
        List<ProductDto> response = productService.findAllActive();
        Assertions.assertEquals(1, response.size());
    }
}