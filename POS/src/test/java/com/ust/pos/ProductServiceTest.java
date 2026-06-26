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
    void saveTestSuccess() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product product = new Product();

        Mockito.when(productRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Product.class))
                .thenReturn(product);

        Mockito.when(productRepository.save(product))
                .thenReturn(product);

        ProductDto response = productService.save(dto);

        Assertions.assertEquals("P1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(productRepository).save(product);
    }

    @Test
    void saveTestFailureWhenProductExists() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Mockito.when(productRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(new Product());

        ProductDto response = productService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(productRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateTestSuccess() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product existingProduct = new Product();

        Mockito.when(productRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(existingProduct);

        ProductDto response = productService.update(dto);

        Assertions.assertEquals("P1", response.getIdentifier());

        Mockito.verify(modelMapper)
                .map(dto, existingProduct);

        Mockito.verify(productRepository)
                .save(existingProduct);
    }

    @Test
    void updateTestFailureWhenNotFound() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Mockito.when(productRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(null);

        ProductDto response = productService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(productRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {
        Product product = new Product();
        product.setIdentifier("P1");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Mockito.when(productRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(product);

        Mockito.when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);

        ProductDto response = productService.findByIdentifier("P1");

        Assertions.assertEquals("P1", response.getIdentifier());
    }

    @Test
    void findByIdentifierNullTest() {
        Mockito.when(productRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(null, ProductDto.class))
                .thenReturn(null);

        ProductDto response = productService.findByIdentifier("P1");

        Assertions.assertNull(response);
    }

    @Test
    void findAllTest() {
        List<Product> products = List.of(new Product());
        List<ProductDto> dtos = List.of(new ProductDto());

        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();

        Mockito.when(productRepository.findByDeletedFalse())
                .thenReturn(products);

        Mockito.when(modelMapper.map(products, listType))
                .thenReturn(dtos);

        List<ProductDto> response = productService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void deleteTest() {
        Product product = new Product();
        product.setIdentifier("P1");
        product.setDeleted(false);

        Mockito.when(productRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(product);

        productService.delete("P1");

        Assertions.assertTrue(product.isDeleted());

        Mockito.verify(productRepository)
                .save(product);
    }

    @Test
    void deleteTestWhenProductNotFound() {
        Mockito.when(productRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(null);

        productService.delete("P1");

        Mockito.verify(productRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void toggleStatusTest() {
        Product product = new Product();
        product.setIdentifier("P1");
        product.setStatus(false);

        Mockito.when(productRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(product);

        productService.toggleStatus("P1");

        Assertions.assertTrue(product.isStatus());

        Mockito.verify(productRepository)
                .save(product);
    }

    @Test
    void toggleStatusNotFoundTest() {
        Mockito.when(productRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(null);

        productService.toggleStatus("P1");

        Mockito.verify(productRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findAllWithPaginationShouldReturnProductDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        Product product = new Product();
        product.setIdentifier("P1");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Page<Product> page = new PageImpl<>(List.of(product));

        Mockito.when(productRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);

        Page<ProductDto> response = productService.findAll(pageable, null);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getContent().size());
        Assertions.assertEquals("P1", response.getContent().get(0).getIdentifier());

        Mockito.verify(productRepository)
                .findByDeletedFalse(pageable);
    }

    @Test
    void findAllWithSearchShouldReturnProductDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        Product product = new Product();
        product.setIdentifier("P1");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Page<Product> page = new PageImpl<>(List.of(product));

        Mockito.when(productRepository.findByIdentifierContainingIgnoreCaseAndDeletedFalse("P1", pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);

        Page<ProductDto> response = productService.findAll(pageable, "P1");

        Assertions.assertEquals(1, response.getContent().size());
        Assertions.assertEquals("P1", response.getContent().get(0).getIdentifier());

        Mockito.verify(productRepository)
                .findByIdentifierContainingIgnoreCaseAndDeletedFalse("P1", pageable);
    }
}