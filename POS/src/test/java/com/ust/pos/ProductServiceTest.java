package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductDto productDto;
    private Product product;

    @BeforeEach
    void setUp() {
        productDto = new ProductDto();
        productDto.setIdentifier("PROD-001");
        productDto.setName("Sample Product");
        productDto.setCategories(List.of("Electronics"));

        product = new Product();
        product.setIdentifier("PROD-001");
        product.setName("Sample Product");
        product.setCategories(List.of("Electronics"));
        product.setStatus(true);
        product.setDeleted(false);
    }

    @Test
    @DisplayName("Save Product - Success")
    void save_Success() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(null);
        when(modelMapper.map(productDto, Product.class)).thenReturn(product);

        ProductDto result = productService.save(productDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Product created successfully", result.getMessage());
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Save Product - Failure: Product Already Exists")
    void save_Failure_AlreadyExists() {
        product.setDeleted(false);
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);

        ProductDto result = productService.save(productDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Save Product - Failure: Product Was Soft-Deleted")
    void save_Failure_PreviouslyDeleted() {
        product.setDeleted(true);
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);

        ProductDto result = productService.save(productDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("was previously deleted"));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Find All Products - Paginated Success")
    void findAll_PaginatedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findByDeletedFalse(pageable)).thenReturn(productPage);
        when(modelMapper.map(eq(productPage.getContent()), any(Type.class))).thenReturn(List.of(productDto));

        WsDto<ProductDto> result = productService.findAll(pageable);

        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertFalse(result.getDtoList().isEmpty());
    }

    @Test
    @DisplayName("Find All Active Products - Success")
    void findAllActive_Success() {
        List<Product> activeProducts = List.of(product);
        when(productRepository.findAllByStatusAndDeletedFalse(true)).thenReturn(activeProducts);
        when(modelMapper.map(eq(activeProducts), any(Type.class))).thenReturn(List.of(productDto));

        List<ProductDto> result = productService.findAllActive();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifier_Success() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        ProductDto result = productService.findByIdentifier("PROD-001");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(product.getCategories(), result.getCategories());
    }

    @Test
    @DisplayName("Find By Identifier - Failure: Not Found")
    void findByIdentifier_NotFound() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(null);

        ProductDto result = productService.findByIdentifier("PROD-001");

        Assertions.assertNull(result);
    }

    @Test
    @DisplayName("Update Product - Success")
    void update_Success() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);

        ProductDto result = productService.update(productDto);

        Assertions.assertTrue(result.isSuccess());
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Update Product - Failure: Product Not Found")
    void update_Failure_NotFound() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(null);

        ProductDto result = productService.update(productDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Product not found", result.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Toggle Status - Success")
    void toggleStatus_Success() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        ProductDto result = productService.toggleStatus("PROD-001");

        Assertions.assertFalse(product.isStatus());
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Delete Product - Success")
    void delete_Success() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);

        boolean result = productService.delete("PROD-001");

        Assertions.assertTrue(result);
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Delete Product - Failure: Product Not Found")
    void delete_Failure_NotFound() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(null);

        boolean result = productService.delete("PROD-001");

        Assertions.assertFalse(result);
        verify(productRepository, never()).save(any(Product.class));
    }
}