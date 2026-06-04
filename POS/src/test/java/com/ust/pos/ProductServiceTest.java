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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.verify;

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

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD1");

        Product product = new Product();

        Mockito.when(productRepository.findByIdentifier("PROD1")).thenReturn(null);
        Mockito.when(modelMapper.map(productDto, Product.class)).thenReturn(product);

        ProductDto response = productService.save(productDto);

        Assertions.assertEquals("PROD1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(productRepository).save(product);
    }

    @Test
    void saveFailureTest() {

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD1");

        Mockito.when(productRepository.findByIdentifier("PROD1")).thenReturn(new Product());

        ProductDto response = productService.save(productDto);

        Assertions.assertEquals("PROD1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateSuccessTest() {

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD1");

        Product existingProduct = new Product();
        existingProduct.setIdentifier("PROD1");

        Mockito.when(productRepository.findByIdentifier("PROD1")).thenReturn(existingProduct);

        ProductDto response = productService.update(productDto);

        Assertions.assertEquals("PROD1", response.getIdentifier());
        verify(productRepository).save(existingProduct);
    }

    @Test
    void updateFailureTest() {

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD1");

        Mockito.when(productRepository.findByIdentifier("PROD1")).thenReturn(null);

        ProductDto response = productService.update(productDto);

        Assertions.assertEquals("PROD1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteSuccessTest() {

        productService.delete("PROD1");

        verify(productRepository).deleteByIdentifier("PROD1");
    }

    @Test
    void findByIdentifierSuccessTest() {

        Product product = new Product();
        product.setIdentifier("PROD1");

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD1");

        Mockito.when(productRepository.findByIdentifier("PROD1")).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        ProductDto response = productService.findByIdentifier("PROD1");

        Assertions.assertEquals("PROD1", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {

        Mockito.when(productRepository.findByIdentifier("PROD1")).thenReturn(null);

        ProductDto response = productService.findByIdentifier("PROD1");

        Assertions.assertNull(response);
    }

    @Test
    void findAllSuccessTest() {

        Product p1 = new Product();
        p1.setIdentifier("PROD1");

        Product p2 = new Product();
        p2.setIdentifier("PROD2");

        List<Product> products = List.of(p1, p2);

        ProductDto d1 = new ProductDto();
        d1.setIdentifier("PROD1");

        ProductDto d2 = new ProductDto();
        d2.setIdentifier("PROD2");

        List<ProductDto> productDtos = List.of(d1, d2);

        Page<Product> page = new PageImpl<>(products);
        Pageable pageable = PageRequest.of(0, 20);

        Mockito.when(productRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(products), Mockito.any(Type.class))).thenReturn(productDtos);

        List<ProductDto> result = productService.findAll(pageable);

        Assertions.assertEquals(2, result.size());
    }
}