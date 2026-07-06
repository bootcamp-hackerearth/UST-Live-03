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

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void findByIdentifierTestSuccess() {
        Product product = new Product();
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD01");

        Mockito.when(productRepository.findByIdentifier("PROD01")).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        ProductDto response = productService.findByIdentifier("PROD01");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("PROD01", response.getIdentifier());
    }

    @Test
    void findByIdentifierTestNotFoundException() {
        Mockito.when(productRepository.findByIdentifier("PROD01")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.findByIdentifier("PROD01");
        });
    }

    @Test
    void saveTestSuccess() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier(" PROD01 "); // Testing trim logic as well

        Mockito.when(productRepository.findByIdentifier("PROD01")).thenReturn(null);
        Product product = new Product();
        Mockito.when(modelMapper.map(productDto, Product.class)).thenReturn(product);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        ProductDto response = productService.save(productDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("PROD01", response.getIdentifier());
    }

    @Test
    void saveTestFailureAlreadyExists() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD01");

        Product existingProduct = new Product();
        existingProduct.setIdentifier("PROD01");
        existingProduct.setDeleted(false);

        Mockito.when(productRepository.findByIdentifier("PROD01")).thenReturn(existingProduct);

        ProductDto response = productService.save(productDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Product with identifier - PROD01 already exists", response.getMessage());
    }

    @Test
    void saveTestFailurePreviouslyDeleted() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD01");

        Product existingProduct = new Product();
        existingProduct.setIdentifier("PROD01");
        existingProduct.setDeleted(true);

        Mockito.when(productRepository.findByIdentifier("PROD01")).thenReturn(existingProduct);

        ProductDto response = productService.save(productDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Node with identifier PROD01 was previously deleted. Please contact backend team to restore.", response.getMessage());
    }

    @Test
    void updateTestSuccess() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD01");

        Product existingProduct = new Product();
        existingProduct.setIdentifier("PROD01");

        Mockito.when(productRepository.findByIdentifier("PROD01")).thenReturn(existingProduct);
        Mockito.when(productRepository.save(existingProduct)).thenReturn(existingProduct);

        ProductDto response = productService.update(productDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("PROD01", response.getIdentifier());
    }

    @Test
    void updateTestFailure() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD01");

        Mockito.when(productRepository.findByIdentifier("PROD01")).thenReturn(null);

        ProductDto response = productService.update(productDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Product with identifier - PROD01 not found", response.getMessage());
    }

    @Test
    void deleteTestSuccess() {
        Product product = new Product();

        Mockito.when(productRepository.findByIdentifier("PROD01")).thenReturn(product);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        boolean response = productService.delete("PROD01");

        Assertions.assertTrue(response);
    }

    @Test
    void deleteTestFailure() {
        Mockito.when(productRepository.findByIdentifier("PROD01")).thenReturn(null);

        boolean response = productService.delete("PROD01");

        Assertions.assertFalse(response);
    }

    @Test
    void findAllPageableTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Product product = new Product();
        List<Product> productList = List.of(product);
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());

        ProductDto productDto = new ProductDto();
        List<ProductDto> productDtos = List.of(productDto);

        Mockito.when(productRepository.findByDeletedFalse(pageable)).thenReturn(productPage);
        Mockito.when(modelMapper.map(Mockito.eq(productList), Mockito.any(Type.class))).thenReturn(productDtos);

        WsDto<ProductDto> response = productService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void toggleStatusTest() {
        Product product = new Product();
        product.setStatus(false);
        ProductDto productDto = new ProductDto();
        productDto.setStatus(true);

        Mockito.when(productRepository.findByIdentifier("PROD01")).thenReturn(product);
        Mockito.when(productRepository.save(product)).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        ProductDto response = productService.toggleStatus("PROD01");

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void findIfTrueTest() {
        Product product = new Product();
        List<Product> productList = List.of(product);
        ProductDto productDto = new ProductDto();
        List<ProductDto> productDtos = List.of(productDto);

        Mockito.when(productRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(productList);
        Mockito.when(modelMapper.map(Mockito.eq(productList), Mockito.any(Type.class))).thenReturn(productDtos);

        List<ProductDto> response = productService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Specification<Product> specification = Mockito.mock(Specification.class);
        Product product = new Product();
        List<Product> productList = List.of(product);
        Page<Product> page = new PageImpl<>(productList, pageable, productList.size());

        ProductDto productDto = new ProductDto();
        List<ProductDto> productDtos = List.of(productDto);

        Mockito.when(productRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(productList), Mockito.any(Type.class))).thenReturn(productDtos);

        WsDto<ProductDto> response = productService.findAll(specification, pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }
}