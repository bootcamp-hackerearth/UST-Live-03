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
        productDto.setIdentifier("Admin");

        Product product = new Product();

        Mockito.when(productRepository.findByIdentifier("Admin")).thenReturn(null);
        Mockito.when(modelMapper.map(productDto, Product.class)).thenReturn(product);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        ProductDto response = productService.save(productDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertFalse(product.getIsDeleted());
    }

    @Test
    void saveTestFailureAlreadyExists() {

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Admin");

        Product existingProduct = new Product();
        existingProduct.setIsDeleted(false);

        Mockito.when(productRepository.findByIdentifier("Admin")).thenReturn(existingProduct);

        ProductDto response = productService.save(productDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Product with identifier - Admin already exists", response.getMessage());
    }

    @Test
    void saveTestFailureDeletedProduct() {

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Admin");

        Product existingProduct = new Product();
        existingProduct.setIsDeleted(true);

        Mockito.when(productRepository.findByIdentifier("Admin")).thenReturn(existingProduct);

        ProductDto response = productService.save(productDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("was deleted"));
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

        Mockito.when(productRepository.findByIdentifier("Admin")).thenReturn(existingProduct);
        Mockito.when(productRepository.save(existingProduct)).thenReturn(existingProduct);

        ProductDto response = productService.update(productDto);

        Assertions.assertEquals("Admin", response.getIdentifier());

        Mockito.verify(modelMapper).map(productDto, existingProduct);
        Mockito.verify(productRepository).save(existingProduct);
    }

    @Test
    void updateTestFailure() {

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Admin");

        Mockito.when(productRepository.findByIdentifier("Admin")).thenReturn(null);

        ProductDto response = productService.update(productDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Product with identifier - Admin not found", response.getMessage());
    }

    @Test
    void deleteTest() {

        Product product = new Product();
        product.setIdentifier("Admin");
        product.setStatus(true);
        product.setIsDeleted(false);

        Mockito.when(productRepository.findByIdentifier("Admin")).thenReturn(product);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        ProductDto response = productService.delete("Admin");

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Product deleted successfully", response.getMessage());
        Assertions.assertTrue(product.getIsDeleted());
        Assertions.assertFalse(product.getStatus());
    }

    @Test
    void deleteTestFailure() {

        Mockito.when(productRepository.findByIdentifier("Admin")).thenReturn(null);

        ProductDto response = productService.delete("Admin");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Product with identifier - Admin not found", response.getMessage());
    }

    @Test
    void findAllTest() {

        Product product = new Product();
        product.setIdentifier("Admin");

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Admin");

        List<Product> products = List.of(product);
        List<ProductDto> productDtos = List.of(productDto);

        Page<Product> page = new PageImpl<>(products);

        Mockito.when(productRepository.findByIsDeleted(
                Mockito.eq(false),
                Mockito.any(Pageable.class)
        )).thenReturn(page);

        Mockito.when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(Type.class)
        )).thenReturn(productDtos);

        PaginatedResponseDto<ProductDto> response = productService.findAll(PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.getItems().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
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

        Mockito.when(productRepository.findByStatusAndIsDeleted(true, false)).thenReturn(products);

        Mockito.when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(Type.class)
        )).thenReturn(productDtos);

        List<ProductDto> response = productService.findAllActive();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Admin", response.get(0).getIdentifier());
    }

    @Test
    void changeStatusTrueTest() {

        Product product = new Product();
        product.setIdentifier("Admin");
        product.setStatus(false);

        Mockito.when(productRepository.findByIdentifier("Admin")).thenReturn(product);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        productService.changeStatus("Admin", true);

        Assertions.assertTrue(product.getStatus());
        Mockito.verify(productRepository).save(product);
    }

    @Test
    void changeStatusFalseTest() {

        Product product = new Product();
        product.setIdentifier("Admin");
        product.setStatus(true);

        Mockito.when(productRepository.findByIdentifier("Admin")).thenReturn(product);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        productService.changeStatus("Admin", false);

        Assertions.assertFalse(product.getStatus());
        Mockito.verify(productRepository).save(product);
    }
}