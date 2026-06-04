package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    @DisplayName("Save - Success with provided categories")
    void saveTest_Success_WithCategories() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P100");
        dto.setCategories(List.of("Electronics"));

        Mockito.when(productRepository.findByIdentifier("P100")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Product.class)).thenReturn(new Product());

        ProductDto result = productService.save(dto);

        Assertions.assertTrue(result.isSuccess());
        Mockito.verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Save - Success with null categories (Ternary Branch)")
    void saveTest_Success_NullCategories() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P100");
        dto.setCategories(null);

        Product productEntity = new Product();
        Mockito.when(productRepository.findByIdentifier("P100")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Product.class)).thenReturn(productEntity);

        ProductDto result = productService.save(dto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertNotNull(productEntity.getCategories());
    }

    @Test
    @DisplayName("Save - Fail if already exists")
    void saveTest_Error_AlreadyExists() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P100");
        Mockito.when(productRepository.findByIdentifier("P100")).thenReturn(new Product());

        ProductDto result = productService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Product already exists", result.getMessage());
        Mockito.verify(productRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("Update - Success with categories")
    void updateTest_Success_WithCategories() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P100");
        dto.setCategories(List.of("Home"));

        Product existing = new Product();
        Mockito.when(productRepository.findByIdentifier("P100")).thenReturn(existing);

        ProductDto result = productService.update(dto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals(List.of("Home"), existing.getCategories());
    }

    @Test
    @DisplayName("Update - Success with null categories (Ternary Branch)")
    void updateTest_Success_NullCategories() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P100");
        dto.setCategories(null);

        Product existing = new Product();
        Mockito.when(productRepository.findByIdentifier("P100")).thenReturn(existing);

        ProductDto result = productService.update(dto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertTrue(existing.getCategories().isEmpty());
    }

    @Test
    @DisplayName("Update - Fail if not found")
    void updateTest_NotFound() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P100");
        Mockito.when(productRepository.findByIdentifier("P100")).thenReturn(null);

        ProductDto result = productService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Product not found", result.getMessage());
    }

    @Test
    @DisplayName("Toggle Status - Flip boolean")
    void toggleStatus_Test() {
        Product product = new Product();
        product.setStatus(true);
        Mockito.when(productRepository.findByIdentifier("P100")).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(new ProductDto());

        productService.toggleStatus("P100");

        Assertions.assertFalse(product.isStatus());
        Mockito.verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Find All - Paginated")
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = List.of(new Product());
        Page<Product> productPage = new PageImpl<>(products);
        List<ProductDto> dtoList = List.of(new ProductDto());

        Mockito.when(productRepository.findAll(pageable)).thenReturn(productPage);

        Mockito.when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(dtoList);

        List<ProductDto> result = productService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Mockito.verify(productRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifier_Success() {
        Product product = new Product();
        product.setCategories(List.of("Cat1"));
        Mockito.when(productRepository.findByIdentifier("P100")).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(new ProductDto());

        ProductDto result = productService.findByIdentifier("P100");
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Find By Identifier - Null")
    void findByIdentifier_Null() {
        Mockito.when(productRepository.findByIdentifier("P100")).thenReturn(null);
        Assertions.assertNull(productService.findByIdentifier("P100"));
    }

    @Test
    @DisplayName("Find All Active")
    void findAllActiveTest() {
        List<Product> products = List.of(new Product());
        Mockito.when(productRepository.findAllByStatus(true)).thenReturn(products);
        Mockito.when(modelMapper.map(eq(products), any(Type.class))).thenReturn(List.of(new ProductDto()));

        List<ProductDto> result = productService.findAllActive();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Delete - Success")
    void deleteTest() {
        boolean result = productService.delete("P100");
        Assertions.assertTrue(result);
        Mockito.verify(productRepository).deleteByIdentifier("P100");
    }
}