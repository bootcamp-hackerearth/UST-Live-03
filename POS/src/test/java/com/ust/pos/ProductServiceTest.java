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

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void saveSuccessTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PRD001");
        Product product = new Product();
        product.setIdentifier("PRD001");

        Mockito.when(productRepository.findByIdentifier("PRD001")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Product.class)).thenReturn(product);

        ProductDto result = productService.save(dto);

        Assertions.assertEquals("PRD001", result.getIdentifier());

        Mockito.verify(productRepository).save(product);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        Product existing = new Product();
        existing.setIdentifier("PRD001");
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PRD001");

        Mockito.when(productRepository.findByIdentifier("PRD001")).thenReturn(existing);

        ProductDto result = productService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Product with identifier - PRD001 already exists", result.getMessage());

        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveFailureDeletedIdentifierTest() {
        Product existing = new Product();
        existing.setIdentifier("PRD001");
        existing.setDeleted(true);
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PRD001");

        Mockito.when(productRepository.findByIdentifier("PRD001")).thenReturn(existing);

        ProductDto result = productService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Product identifier - PRD001 not available", result.getMessage());

        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateSuccessTest() {
        Product existing = new Product();
        existing.setIdentifier("PRD002");
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PRD002");

        Mockito.when(productRepository.findByIdentifier("PRD002")).thenReturn(existing);

        ProductDto result = productService.update(dto);

        Assertions.assertEquals("PRD002", result.getIdentifier());

        Mockito.verify(modelMapper).map(dto, existing);
        Mockito.verify(productRepository).save(existing);
    }

    @Test
    void updateFailureProductNotFoundTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PRD999");

        Mockito.when(productRepository.findByIdentifier("PRD999")).thenReturn(null);

        ProductDto result = productService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Product with identifier - PRD999 is not found", result.getMessage());

        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteTest() {
        Product product = new Product();
        product.setIdentifier("PRD003");

        Mockito.when(productRepository.findByIdentifier("PRD003")).thenReturn(product);

        productService.delete("PRD003");

        Assertions.assertTrue(product.isDeleted());

        Mockito.verify(productRepository).findByIdentifier("PRD003");
    }

    @Test
    void findByIdentifierTest() {
        Product product = new Product();
        product.setIdentifier("PRD004");
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PRD004");

        Mockito.when(productRepository.findByIdentifier("PRD004")).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);

        ProductDto result = productService.findByIdentifier("PRD004");

        Assertions.assertEquals("PRD004", result.getIdentifier());
    }

    @Test
    void findAllTest() {
        Product product = new Product();
        product.setIdentifier("PROD1");
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD1");

        List<Product> products = List.of(product);
        List<ProductDto> productDtos = List.of(dto);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(products, pageable, products.size());

        Mockito.when(productRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(products), Mockito.any(Type.class))).thenReturn(productDtos);

        WsDto<ProductDto> result = productService.findAll(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());

        Mockito.verify(productRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void toggleStatusTest() {
        Product product = new Product();
        product.setIdentifier("PRD001");
        product.setStatus(true);

        Mockito.when(productRepository.findByIdentifier("PRD001")).thenReturn(product);

        productService.toggleStatus("PRD001");

        Assertions.assertFalse(product.getStatus());

        Mockito.verify(productRepository).save(product);
    }

    @Test
    void findAllActiveTest() {
        Product product = new Product();
        product.setIdentifier("PRD001");
        product.setStatus(true);
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PRD001");

        List<Product> products = List.of(product);
        List<ProductDto> dtos = List.of(dto);

        Mockito.when(productRepository.findByStatusTrueAndIsDeletedFalse()).thenReturn(products);
        Mockito.when(modelMapper.map(Mockito.eq(products), Mockito.any(Type.class))).thenReturn(dtos);

        List<ProductDto> result = productService.findAllActive();

        Assertions.assertEquals(1, result.size());

        Mockito.verify(productRepository).findByStatusTrueAndIsDeletedFalse();
    }

    @Test
    void toggleStatusWhenStatusIsNullTest() {
        Product product = new Product();
        product.setIdentifier("PRD005");
        product.setStatus(null);

        Mockito.when(productRepository.findByIdentifier("PRD005")).thenReturn(product);

        productService.toggleStatus("PRD005");

        Assertions.assertTrue(product.getStatus());

        Mockito.verify(productRepository).save(product);
    }

    @Test
    void toggleStatusWhenProductNotFoundTest() {
        Mockito.when(productRepository.findByIdentifier("UNKNOWN")).thenReturn(null);
        productService.toggleStatus("UNKNOWN");
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void constructorTest() {
        ProductServiceImpl service = new ProductServiceImpl(productRepository, modelMapper);
        Assertions.assertNotNull(service);
    }
}