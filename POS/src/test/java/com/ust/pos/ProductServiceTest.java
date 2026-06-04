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
        productDto.setIdentifier("Product1");

        Mockito.when(productRepository.findByIdentifier("Product1"))
                .thenReturn(null);

        Product product = new Product();
        Mockito.when(modelMapper.map(productDto, Product.class))
                .thenReturn(product);
        Mockito.when(productRepository.save(product))
                .thenReturn(product);

        ProductDto response = productService.save(productDto);

        Assertions.assertEquals("Product1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Product1");

        Mockito.when(productRepository.findByIdentifier("Product1"))
                .thenReturn(new Product());

        ProductDto response = productService.save(productDto);

        Assertions.assertEquals("Product1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Product product = new Product();
        product.setIdentifier("Product1");

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Product1");

        Mockito.when(productRepository.findByIdentifier("Product1"))
                .thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class))
                .thenReturn(productDto);

        ProductDto response = productService.findByIdentifier("Product1");

        Assertions.assertEquals("Product1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Product1");

        Product existingProduct = new Product();
        existingProduct.setIdentifier("Product1");

        Mockito.when(productRepository.findByIdentifier("Product1"))
                .thenReturn(existingProduct);
        Mockito.when(productRepository.save(existingProduct))
                .thenReturn(existingProduct);

        ProductDto response = productService.update(productDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Product1");

        Mockito.when(productRepository.findByIdentifier("Product1"))
                .thenReturn(null);

        ProductDto response = productService.update(productDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(productRepository)
                .deleteByIdentifier("Product1");

        productService.delete("Product1");

        Mockito.verify(productRepository)
                .deleteByIdentifier("Product1");
    }

    @Test
    void toggleStatusTest() {
        Product product = new Product();
        product.setIdentifier("Product1");
        product.setStatus(true);

        Mockito.when(productRepository.findByIdentifier("Product1"))
                .thenReturn(product);
        Mockito.when(productRepository.save(product))
                .thenReturn(product);

        productService.toggleStatus("Product1");

        Assertions.assertFalse(product.getStatus());
        Mockito.verify(productRepository).save(product);
    }

    @Test
    void toggleStatusTestFailure() {
        Mockito.when(productRepository.findByIdentifier("Product1"))
                .thenReturn(null);

        productService.toggleStatus("Product1");

        Mockito.verify(productRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findAllPaginationTest() {

        Product product = new Product();
        product.setIdentifier("Product1");

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Product1");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage =
                new PageImpl<>(List.of(product), pageable, 1);

        Mockito.when(productRepository.findAll(pageable))
                .thenReturn(productPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(productPage.getContent()),
                Mockito.any(Type.class)
        )).thenReturn(List.of(productDto));

        List<ProductDto> response = productService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
    }
}