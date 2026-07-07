package com.ust.pos;

import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

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
        productDto.setSuccess(true);

        Product product = new Product();

        Mockito.when(productRepository.findByIdentifier("Product1")).thenReturn(null);

        Mockito.when(modelMapper.map(productDto, Product.class)).thenReturn(product);

        Mockito.when(productRepository.save(product)).thenReturn(product);

        ProductDto response = productService.save(productDto);

        Assertions.assertEquals("Product1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(productRepository).save(product);
    }

    @Test
    void saveShouldSetDefaultValuesTest() {

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Product1");

        Product product = new Product();

        Mockito.when(productRepository.findByIdentifier("Product1")).thenReturn(null);

        Mockito.when(modelMapper.map(productDto, Product.class)).thenReturn(product);

        productService.save(productDto);

        Assertions.assertFalse(product.getDeleted());
        Assertions.assertTrue(product.getStatus());

        Mockito.verify(productRepository).save(product);
    }

    @Test
    void saveTestFailure() {

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Product1");

        Product existingProduct = new Product();

        Mockito.when(productRepository.findByIdentifier("Product1")).thenReturn(existingProduct);

        ProductDto response = productService.save(productDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Product with identifier - Product1 already exists", response.getMessage());

        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveTestSoftDeletedFailure() {

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Product1");

        Product existingProduct = new Product();
        existingProduct.setDeleted(true);

        Mockito.when(productRepository.findByIdentifier("Product1")).thenReturn(existingProduct);

        ProductDto response = productService.save(productDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Product with identifier - Product1 has been soft deleted. Restore it by changing status.", response.getMessage());

        Mockito.verify(productRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {

        Product product = new Product();
        product.setIdentifier("Product1");

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Product1");

        Mockito.when(productRepository.findByIdentifier("Product1")).thenReturn(product);

        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        ProductDto response = productService.findByIdentifier("Product1");

        Assertions.assertNotNull(response);

        Assertions.assertEquals("Product1", response.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {

        Mockito.when(productRepository.findByIdentifier("Product1")).thenReturn(null);

        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.findByIdentifier("Product1"));

        Assertions.assertEquals("Product with identifier 'Product1' not found", exception.getMessage());
    }

    @Test
    void updateTest() {

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Product1");
        productDto.setSuccess(true);

        Product existingProduct = new Product();
        existingProduct.setIdentifier("Product1");

        Mockito.when(productRepository.findByIdentifier("Product1")).thenReturn(existingProduct);

        Mockito.when(productRepository.save(existingProduct)).thenReturn(existingProduct);

        ProductDto response = productService.update(productDto);

        Mockito.verify(modelMapper).map(productDto, existingProduct);

        Mockito.verify(productRepository).save(existingProduct);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateShouldMapAndSaveTest() {

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Product1");

        Product existingProduct = new Product();
        existingProduct.setIdentifier("Product1");

        Mockito.when(productRepository.findByIdentifier("Product1")).thenReturn(existingProduct);

        productService.update(productDto);

        Mockito.verify(modelMapper).map(productDto, existingProduct);

        Mockito.verify(productRepository).save(existingProduct);
    }

    @Test
    void updateTestFailure() {

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Product1");

        Mockito.when(productRepository.findByIdentifier("Product1")).thenReturn(null);

        ProductDto response = productService.update(productDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Product with identifier - Product1 not found", response.getMessage());
    }

    @Test
    void deleteTest() {

        Product product = new Product();
        product.setIdentifier("Product1");
        product.setDeleted(false);

        Mockito.when(productRepository.findByIdentifier("Product1")).thenReturn(product);

        Mockito.when(productRepository.save(product)).thenReturn(product);

        boolean result = productService.delete("Product1");

        Assertions.assertTrue(result);
        Assertions.assertTrue(product.getDeleted());

        Mockito.verify(productRepository).save(product);
    }

    @Test
    void deleteTestFailure() {

        Mockito.when(productRepository.findByIdentifier("Product1")).thenReturn(null);

        boolean result = productService.delete("Product1");

        Assertions.assertFalse(result);

        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void toggleStatusTest() {

        Product product = new Product();
        product.setIdentifier("Product1");
        product.setStatus(true);

        Mockito.when(productRepository.findByIdentifier("Product1")).thenReturn(product);

        Mockito.when(productRepository.save(product)).thenReturn(product);

        productService.toggleStatus("Product1");

        Assertions.assertFalse(product.getStatus());

        Mockito.verify(productRepository).save(product);
    }

    @Test
    void toggleStatusFalseToTrueTest() {

        Product product = new Product();
        product.setIdentifier("Product1");
        product.setStatus(false);

        Mockito.when(productRepository.findByIdentifier("Product1")).thenReturn(product);

        Mockito.when(productRepository.save(product)).thenReturn(product);

        productService.toggleStatus("Product1");

        Assertions.assertTrue(product.getStatus());

        Mockito.verify(productRepository).save(product);
    }

    @Test
    void toggleStatusTestFailure() {

        Mockito.when(productRepository.findByIdentifier("Product1")).thenReturn(null);

        productService.toggleStatus("Product1");

        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllTest() {

        Product product = new Product();
        product.setIdentifier("Product1");

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Product1");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

        Mockito.when(productRepository.findByDeletedFalse(pageable)).thenReturn(productPage);

        Type listType = new TypeToken<List<ProductDto>>() {
                }.getType();

        Mockito.when(modelMapper.map(productPage.getContent(), listType)).thenReturn(List.of(productDto));

        PageDto<ProductDto> response = productService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findAllWithSpecificationTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Product> spec = Mockito.mock(Specification.class);

        Product product = new Product();
        product.setIdentifier("Product1");

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Product1");

        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

        Mockito.when(productRepository.findAll(spec, pageable)).thenReturn(productPage);

        Type listType = new TypeToken<List<ProductDto>>() {
                }.getType();

        Mockito.when(modelMapper.map(productPage.getContent(), listType)).thenReturn(List.of(productDto));

        PageDto<ProductDto> response = productService.findAll(spec, pageable, "product");

        Assertions.assertNotNull(response);

        Assertions.assertEquals(1, response.getDtoList().size());

        Assertions.assertEquals("Product1", response.getDtoList().get(0).getIdentifier());

        Assertions.assertEquals("product", response.getKeyword());

        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void findActiveProductsTest() {

        Product product = new Product();
        product.setIdentifier("Product1");
        product.setStatus(true);

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Product1");

        List<Product> products = List.of(product);

        Type listType = new TypeToken<List<ProductDto>>() {
                }.getType();

        Mockito.when(productRepository.findByStatusTrue()).thenReturn(products);

        Mockito.when(modelMapper.map(products, listType)).thenReturn(List.of(productDto));

        List<ProductDto> response = productService.findActiveProducts();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());

        Assertions.assertEquals("Product1", response.get(0).getIdentifier());
    }

    @Test
    void findActiveProductsEmptyTest() {

        Type listType = new TypeToken<List<ProductDto>>() {
                }.getType();

        Mockito.when(productRepository.findByStatusTrue()).thenReturn(List.of());

        Mockito.when(modelMapper.map(List.of(), listType)).thenReturn(List.of());

        List<ProductDto> response = productService.findActiveProducts();

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isEmpty());
    }
}