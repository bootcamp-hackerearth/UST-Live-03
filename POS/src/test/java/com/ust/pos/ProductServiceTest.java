package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

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
        productDto.setBrand("BrandX");
        productDto.setModel("ModelY");
        productDto.setCategories(List.of("Cat1", "Cat2"));

        product = new Product();
        product.setIdentifier("PROD-001");
        product.setName("Sample Product");
        product.setBrand("BrandX");
        product.setModel("ModelY");
        product.setCategories(List.of("Cat1", "Cat2"));
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
        Assertions.assertTrue(result.getMessage().contains("successfully"));
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Save Product - Success with Null Categories")
    void save_Success_NullCategories() {
        productDto.setCategories(null);
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(null);
        when(modelMapper.map(productDto, Product.class)).thenReturn(product);

        ProductDto result = productService.save(productDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertNotNull(product.getCategories());
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Save Product - Failure: Already Exists")
    void save_Failure_AlreadyExists() {
        product.setDeleted(false);
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);

        ProductDto result = productService.save(productDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Save Product - Failure: Previously Deleted")
    void save_Failure_PreviouslyDeleted() {
        product.setDeleted(true);
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);

        ProductDto result = productService.save(productDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("previously deleted"));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Find All Products - Paginated Success")
    void findAll_PaginatedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

        when(productRepository.findByDeletedFalse(pageable)).thenReturn(productPage);
        when(modelMapper.map(eq(productPage.getContent()), any(Type.class))).thenReturn(List.of(productDto));

        WsDto<ProductDto> result = productService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    @DisplayName("Find All Products with Specification - Success")
    void findAll_WithSpecification_Success() {
        Specification<Product> spec = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

        when(productRepository.findAll(spec, pageable)).thenReturn(productPage);
        when(modelMapper.map(eq(productPage.getContent()), any(Type.class))).thenReturn(List.of(productDto));

        WsDto<ProductDto> result = productService.findAll(spec, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
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
    @DisplayName("Find By Identifier - Failure: Not Found Exception")
    void findByIdentifier_Failure_NotFound() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.findByIdentifier("PROD-001"));
    }

    @Test
    @DisplayName("Update Product - Success")
    void update_Success() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);

        ProductDto result = productService.update(productDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Sample Product", product.getName());
        Assertions.assertEquals("BrandX", product.getBrand());
        Assertions.assertEquals("ModelY", product.getModel());
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Update Product - Success with Null Categories fallback")
    void update_Success_NullCategories() {
        productDto.setCategories(null);
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);

        ProductDto result = productService.update(productDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertTrue(product.getCategories().isEmpty());
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Update Product - Failure: Not Found")
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
    @DisplayName("Delete Product - Failure: Not Found")
    void delete_Failure_NotFound() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(null);

        boolean result = productService.delete("PROD-001");

        Assertions.assertFalse(result);
        verify(productRepository, never()).save(any(Product.class));
    }
}