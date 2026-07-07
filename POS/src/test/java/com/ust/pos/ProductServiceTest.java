package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

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
        verify(productRepository).save(product);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD1");

        Product existingProduct = new Product();
        existingProduct.setDeleted(false);

        Mockito.when(productRepository.findByIdentifier("PROD1")).thenReturn(existingProduct);

        ProductDto response = productService.save(productDto);

        Assertions.assertEquals("PROD1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Product with identifier - PROD1 already exists", response.getMessage());
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveFailureAlreadyDeletedTest() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD1");

        Product existingProduct = new Product();
        existingProduct.setDeleted(true);

        Mockito.when(productRepository.findByIdentifier("PROD1")).thenReturn(existingProduct);

        ProductDto response = productService.save(productDto);

        Assertions.assertEquals("PROD1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Product with identifier - PROD1 was deleted , Please Contact the Administrator to add.", response.getMessage());
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
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
        verify(modelMapper).map(productDto, existingProduct);
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
        Assertions.assertEquals("Product with identifier - PROD1 not found", response.getMessage());
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteSuccessTest() {
        Product product = new Product();
        Mockito.when(productRepository.findByIdentifier("PROD1")).thenReturn(product);

        productService.delete("PROD1");

        verify(productRepository).findByIdentifier("PROD1");
    }

    @Test
    void findAllSuccessTest() {
        Product product = new Product();
        List<Product> productList = List.of(product);

        ProductDto dto = new ProductDto();
        List<ProductDto> productDtos = List.of(dto);

        Page<Product> page = new PageImpl<>(productList, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(productRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(productList), Mockito.any(Type.class))).thenReturn(productDtos);

        WsDto<ProductDto> result = productService.findAll(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
    }

    @Test
    void findByIdentifierSuccessTest() {
        Product product = new Product();
        ProductDto productDto = new ProductDto();

        Mockito.when(productRepository.findByIdentifierAndIsDeletedFalse("PROD1")).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        ProductDto response = productService.findByIdentifier("PROD1");

        Assertions.assertNotNull(response);
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(productRepository.findByIdentifierAndIsDeletedFalse("PROD1")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.findByIdentifier("PROD1");
        });
    }

    @Test
    void searchByIdentifierOrNameSuccessTest() {
        Product product = new Product();
        List<Product> productList = List.of(product);

        ProductDto dto = new ProductDto();
        List<ProductDto> productDtos = List.of(dto);

        Page<Product> page = new PageImpl<>(productList);
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(productRepository.searchByIdentifierOrName("laptop", pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(productList), Mockito.any(Type.class))).thenReturn(productDtos);

        WsDto<ProductDto> result = productService.searchByIdentifierOrName("laptop", pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
        verify(productRepository).searchByIdentifierOrName("laptop", pageable);
    }

    @Test
    void findAllSpecificationSuccessTest() {
        Product product = new Product();
        List<Product> productList = List.of(product);

        ProductDto dto = new ProductDto();
        List<ProductDto> productDtos = List.of(dto);

        Page<Product> page = new PageImpl<>(productList, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Product> specification = Mockito.mock(Specification.class);

        Mockito.when(productRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(productList), Mockito.any(Type.class))).thenReturn(productDtos);

        WsDto<ProductDto> result = productService.findAll(specification, pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
    }
}