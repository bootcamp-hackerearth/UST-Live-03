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
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void saveTestSuccess() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD-1");

        Mockito.when(productRepository.findByIdentifier("PROD-1"))
                .thenReturn(null);

        Mockito.doNothing()
                .when(modelMapper)
                .map(Mockito.eq(productDto), Mockito.any(Product.class));

        ProductDto response = productService.save(productDto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Product Added Successfully", response.getMessage());
    }

    @Test
    void saveTestFailure_ProductAlreadyExists() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD-1");

        Product existing = new Product();
        existing.setIdentifier("PROD-1");

        Mockito.when(productRepository.findByIdentifier("PROD-1"))
                .thenReturn(existing);

        ProductDto response = productService.save(productDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("This product Already Exist!", response.getMessage());
    }

    @Test
    void findAllTest() {
        Product product = new Product();
        product.setIdentifier("PROD-1");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD-1");

        Page<Product> page = new PageImpl<>(List.of(product));

        Mockito.when(productRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        Mockito.when(modelMapper.map(page.getContent(), listType))
                .thenReturn(List.of(dto));

        Pageable pageable = PageRequest.of(0, 50, Sort.unsorted());
        List<ProductDto> response = productService.findAll(pageable).getDtoList();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("PROD-1", response.get(0).getIdentifier());
    }

    @Test
    void updateTestSuccess() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD-1");

        Product product = new Product();
        product.setIdentifier("PROD-1");

        Mockito.when(productRepository.findByIdentifier("PROD-1"))
                .thenReturn(product);
        Mockito.when(productRepository.save(product))
                .thenReturn(product);

        ProductDto response = productService.update(dto);

        Assertions.assertEquals("Product Updated Successfully", response.getMessage());
    }

    @Test
    void updateTestFailure_ProductNotFound() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD-404");

        Mockito.when(productRepository.findByIdentifier("PROD-404"))
                .thenReturn(null);

        ProductDto response = productService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Product not found!", response.getMessage());
    }

    @Test
    void updateTestFailure_DuplicateIdentifier() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("NEW-PROD");

        Product existing = new Product();
        existing.setIdentifier("OLD-PROD");

        Mockito.when(productRepository.findByIdentifier("NEW-PROD"))
                .thenReturn(existing);
        Mockito.when(productRepository.existsByIdentifier("NEW-PROD"))
                .thenReturn(true);

        ProductDto response = productService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("This product already exist!", response.getMessage());
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findByIdTest() {
        Product product = new Product();
        product.setIdentifier("PROD-1");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD-1");

        Mockito.when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));
        Mockito.when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);

        ProductDto response = productService.findById(1L);

        Assertions.assertEquals("PROD-1", response.getIdentifier());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(productRepository).deleteById(1L);

        productService.delete(1L);

        Mockito.verify(productRepository, Mockito.times(1))
                .deleteById(1L);
    }

    @Test
    void changeProductStatusSuccessTest() {
        Product product = new Product();
        product.setIdentifier("PROD-1");
        product.setStatus(false);

        ProductDto dto = new ProductDto();
        dto.setStatus(true);

        Mockito.when(productRepository.findByIdentifier("PROD-1"))
                .thenReturn(product);
        Mockito.when(productRepository.save(product))
                .thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);

        ProductDto response = productService.changeProductStatus("PROD-1", true);

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void changeProductStatusFailure_ProductNull() {
        Mockito.when(productRepository.findByIdentifier("PROD-1"))
                .thenReturn(null);

        ProductDto response = productService.changeProductStatus("PROD-1", true);

        Assertions.assertNull(response);
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllActiveProductTest() {
        Product product = new Product();
        product.setIdentifier("PROD-1");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD-1");

        List<Product> activeProducts = List.of(product);
        List<ProductDto> activeDtos = List.of(dto);

        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();

        Mockito.when(productRepository.findByStatusTrue(true))
                .thenReturn(activeProducts);
        Mockito.when(modelMapper.map(activeProducts, listType))
                .thenReturn(activeDtos);

        List<ProductDto> response = productService.findAllActiveProduct();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("PROD-1", response.get(0).getIdentifier());
    }
}