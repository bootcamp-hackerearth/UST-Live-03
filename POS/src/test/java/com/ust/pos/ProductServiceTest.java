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

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductServiceImpl productService;

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
    void saveTest() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("  Admin  ");

        Mockito.when(productRepository.findByIdentifier("Admin")).thenReturn(null);
        Product product = new Product();
        Mockito.when(modelMapper.map(productDto, Product.class)).thenReturn(product);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        ProductDto response = productService.save(productDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void saveTestFailure() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Admin");

        Product existingProduct = new Product();
        existingProduct.setIdentifier("Admin");
        existingProduct.setDeleted(false);

        Mockito.when(productRepository.findByIdentifier("Admin")).thenReturn(existingProduct);

        ProductDto response = productService.save(productDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void saveTestFailurePreviouslyDeleted() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Admin");

        Product existingProduct = new Product();
        existingProduct.setIdentifier("Admin");
        existingProduct.setDeleted(true);

        Mockito.when(productRepository.findByIdentifier("Admin")).thenReturn(existingProduct);

        ProductDto response = productService.save(productDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void updateTest() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Admin");

        Product existingProduct = new Product();
        existingProduct.setIdentifier("Admin");

        Mockito.when(productRepository.findByIdentifier("Admin")).thenReturn(existingProduct);
        Mockito.when(productRepository.save(existingProduct)).thenReturn(existingProduct);

        ProductDto response = productService.update(productDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Admin");

        Mockito.when(productRepository.findByIdentifier("Admin")).thenReturn(null);

        ProductDto response = productService.update(productDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Product product = new Product();
        product.setIdentifier("Admin");

        Mockito.when(productRepository.findByIdentifier("Admin")).thenReturn(product);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        boolean response = productService.delete("Admin");

        Assertions.assertEquals(true, response);
    }

    @Test
    void deleteTestFailure() {
        Mockito.when(productRepository.findByIdentifier("Admin")).thenReturn(null);

        boolean response = productService.delete("Admin");

        Assertions.assertEquals(false, response);
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Product product = new Product();
        List<Product> products = List.of(product);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

        ProductDto productDto = new ProductDto();
        List<ProductDto> productDtos = List.of(productDto);

        Mockito.when(productRepository.findByDeletedFalse(pageable)).thenReturn(productPage);
        Mockito.when(modelMapper.map(Mockito.eq(products), Mockito.any(java.lang.reflect.Type.class))).thenReturn(productDtos);

        WsDto<ProductDto> response = productService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
    }

    @Test
    void findByStatusTest() {
        Product product = new Product();
        List<Product> products = List.of(product);
        ProductDto productDto = new ProductDto();
        List<ProductDto> productDtos = List.of(productDto);

        Mockito.when(productRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(products);
        Mockito.when(modelMapper.map(Mockito.eq(products), Mockito.any(java.lang.reflect.Type.class))).thenReturn(productDtos);

        List<ProductDto> response = productService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleTestActive() {
        Product product = new Product();
        product.setStatus(false);
        ProductDto productDto = new ProductDto();
        productDto.setStatus(true);

        Mockito.when(productRepository.findByIdentifier("Admin")).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        ProductDto response = productService.toggleStatus("Admin");

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void toggleTestInactive() {
        Product product = new Product();
        product.setStatus(true);
        ProductDto productDto = new ProductDto();
        productDto.setStatus(false);

        Mockito.when(productRepository.findByIdentifier("Admin")).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        ProductDto response = productService.toggleStatus("Admin");

        Assertions.assertFalse(response.isStatus());
    }
}