package com.ust.pos;

import com.ust.pos.dto.PaginatedResponseDto;
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
        productDto.setIdentifier("Admin");

        Mockito.when(productRepository.findByIdentifier("Admin")).thenReturn(null);
        Product product = new Product();
        Mockito.when(modelMapper.map(productDto, Product.class)).thenReturn(product);
        Mockito.when(productRepository.save(product)).thenReturn(product);
        ProductDto response = productService.save(productDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess());

    }

    @Test
    void saveTestFailure() {

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Admin");
        Product product = new Product();

        Mockito.when(productRepository.findByIdentifier("Admin")).thenReturn(product);
        ProductDto response = productService.save(productDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");

        Assertions.assertEquals(false, response.isSuccess());

    }

    @Test
    void findByIdentifierTest() {

        Product product = new Product();
        product.setIdentifier("Admin");

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Admin");

        Mockito.when(productRepository.findByIdentifier("Admin")).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        ProductDto response = productService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTest() {

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Admin");

        Product existingProduct = new Product();
        existingProduct.setIdentifier("Admin");

        Mockito.when(productRepository.findByIdentifier("Admin"))
                .thenReturn(existingProduct);
        Mockito.when(productRepository.save(existingProduct))
                .thenReturn(existingProduct);

        ProductDto response = productService.update(productDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Admin");

        Mockito.when(productRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        ProductDto response = productService.update(productDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {

        Mockito.doNothing().when(productRepository)
                .deleteByIdentifier("Admin");

        productService.delete("Admin");

        Mockito.verify(productRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findAllTest() {

        Product product = new Product();
        product.setIdentifier("Admin");

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Admin");

        List<Product> products = List.of(product);
        List<ProductDto> productDtos = List.of(productDto);

        Page<Product> productPage = new PageImpl<>(products);

        Mockito.when(productRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(productPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(products),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(productDtos);

        PaginatedResponseDto<ProductDto> response = productService.findAll(PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.getItems().size());
    }

    @Test
    void findAllActiveTest() {

        Product product = new Product();
        product.setIdentifier("Admin");
        product.setStatus(true);

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Admin");

        List<Product> products = List.of(product);
        List<ProductDto> productDtos = List.of(productDto);

        Mockito.when(productRepository.findByStatus(true)).thenReturn(products);
        Mockito.when(modelMapper.map(
                Mockito.eq(products),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(productDtos);

        List<ProductDto> response = productService.findAllActive();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void changeStatusTest() {

        Product product = new Product();
        product.setIdentifier("Admin");
        product.setStatus(false);

        Mockito.when(productRepository.findByIdentifier("Admin"))
                .thenReturn(product);

        Mockito.when(productRepository.save(product))
                .thenReturn(product);

        productService.changeStatus("Admin", true);

        Assertions.assertTrue(product.getStatus());

        Mockito.verify(productRepository).save(product);
    }
}