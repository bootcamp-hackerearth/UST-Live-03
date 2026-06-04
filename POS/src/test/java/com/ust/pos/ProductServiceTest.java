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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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
        productDto.setIdentifier("P1");

        Mockito.when(productRepository.findByIdentifier("P1")).thenReturn(null);
        Product product = new Product();
        Mockito.when(modelMapper.map(productDto, Product.class)).thenReturn(product);
        Mockito.when(productRepository.save(product)).thenReturn(product);
        ProductDto response = productService.save(productDto);

        Assertions.assertEquals("P1", response.getIdentifier());
        Assertions.assertEquals(true, response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("P1");
        Product product = new Product();

        Mockito.when(productRepository.findByIdentifier("P1")).thenReturn(product);
        ProductDto response = productService.save(productDto);

        Assertions.assertEquals("P1", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");

        Assertions.assertEquals(false, response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Product product = new Product();
        product.setIdentifier("P1");

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("P1");

        Mockito.when(productRepository.findByIdentifier("P1")).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        ProductDto response = productService.findByIdentifier("P1");

        Assertions.assertEquals("P1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("P1");

        Product existingProduct = new Product();
        existingProduct.setIdentifier("P1");

        Mockito.when(productRepository.findByIdentifier("P1"))
                .thenReturn(existingProduct);
        Mockito.when(productRepository.save(existingProduct))
                .thenReturn(existingProduct);

        ProductDto response = productService.update(productDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("P1");

        Mockito.when(productRepository.findByIdentifier("P1"))
                .thenReturn(null);

        ProductDto response = productService.update(productDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(productRepository)
                .deleteByIdentifier("P1");

        productService.delete("P1");

        Mockito.verify(productRepository).deleteByIdentifier("P1");
    }

    @Test
    void findAllTest() {
        Product product = new Product();
        product.setIdentifier("P1");

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("P1");

        List<Product> products = List.of(product);
        List<ProductDto> productDtos = List.of(productDto);

        Mockito.when(productRepository.findAll()).thenReturn(products);
        Mockito.when(modelMapper.map(
                Mockito.eq(products),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(productDtos);

        List<ProductDto> response = productService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllWithPaginationShouldReturnProductDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Product> products = List.of(new Product());
        Page<Product> page = new PageImpl<>(products);

        List<ProductDto> productDtos = List.of(new ProductDto());

        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();

        Mockito.when(productRepository.findAll(pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(products, listType))
                .thenReturn(productDtos);

        List<ProductDto> response = productService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Mockito.verify(productRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(products, listType);
    }
}