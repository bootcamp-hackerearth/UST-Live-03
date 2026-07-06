package com.ust.pos;

import com.ust.pos.dto.PaginationResponseDto;
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
        productDto.setIdentifier("Lays Chili");
        Product product = new Product();
        Mockito.when(productRepository.findByIdentifier("Lays Chili")).thenReturn(null);
        Mockito.when(modelMapper.map(productDto, Product.class)).thenReturn(product);
        ProductDto response = productService.save(productDto);
        Assertions.assertEquals("Lays Chili", response.getIdentifier());
        Mockito.verify(productRepository).save(product);
    }

    @Test
    void saveTestFailure() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Lays Chili");
        Product existing = new Product();
        Mockito.when(productRepository.findByIdentifier("Lays Chili")).thenReturn(existing);
        ProductDto response = productService.save(productDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveSoftDeletedProductTest() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Lays Chili");
        Product existing = new Product();
        existing.setDeleted(true);
        Mockito.when(productRepository.findByIdentifier("Lays Chili")).thenReturn(existing);
        ProductDto response = productService.save(productDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void findByIdentifierTest() {
        Product product = new Product();
        product.setIdentifier("Lays Chili");
        ProductDto dto = new ProductDto();
        dto.setIdentifier("Lays Chili");
        Mockito.when(productRepository.findByIdentifier("Lays Chili")).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);
        ProductDto response = productService.findByIdentifier("Lays Chili");
        Assertions.assertEquals("Lays Chili", response.getIdentifier());
    }

    @Test
    void updateTest() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Lays Chili");
        Product existing = new Product();
        existing.setIdentifier("Lays Chili");
        Mockito.when(productRepository.findByIdentifier("Lays Chili")).thenReturn(existing);
        ProductDto response = productService.update(productDto);
        Assertions.assertNotNull(response);
        Mockito.verify(productRepository).save(existing);
    }

    @Test
    void updateTestFailure() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Lays Chili");
        Mockito.when(productRepository.findByIdentifier("Lays Chili")).thenReturn(null);
        ProductDto response = productService.update(productDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Product product = new Product();
        product.setIdentifier("Lays Chili");
        Mockito.when(productRepository.findByIdentifier("Lays Chili")).thenReturn(product);
        productService.deleteByIdentifier("Lays Chili");
        Assertions.assertTrue(product.isDeleted());
        Mockito.verify(productRepository).save(product);
    }

    @Test
    void deleteNotFoundTest() {
        Mockito.when(productRepository.findByIdentifier("Lays Chili")).thenReturn(null);
        Assertions.assertThrows(RuntimeException.class, () -> productService.deleteByIdentifier("Lays Chili"));
    }

    @Test
    void findAllWithPageableTest() {
        Product product = new Product();
        product.setIdentifier("Lays Chili");
        ProductDto dto = new ProductDto();
        dto.setIdentifier("Lays Chili");
        List<Product> products = List.of(product);
        List<ProductDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Product> productPage = new PageImpl<>(products);
        Mockito.when(productRepository.findByDeletedFalse(pageable)).thenReturn(productPage);
        Mockito.when(modelMapper.map(Mockito.eq(products), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<ProductDto> response = productService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Lays Chili", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Product product = new Product();
        product.setIdentifier("Lays Chili");
        ProductDto dto = new ProductDto();
        dto.setIdentifier("Lays Chili");
        List<Product> products = List.of(product);
        List<ProductDto> dtos = List.of(dto);
        Mockito.when(productRepository.findAll()).thenReturn(products);
        Mockito.when(modelMapper.map(Mockito.eq(products), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<ProductDto> response = productService.findAll(null);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Lays Chili", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void toggleStatusSuccessTest() {
        Product product = new Product();
        product.setIdentifier("PRD001");
        product.setStatus(false);
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PRD001");
        dto.setStatus(true);
        Mockito.when(productRepository.findByIdentifier("PRD001")).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);
        ProductDto response = productService.toggleStatus("PRD001", true);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Status updated successfully", response.getMessage());
        Assertions.assertTrue(product.isStatus());
        Mockito.verify(productRepository).save(product);
    }

    @Test
    void toggleStatusProductNotFoundTest() {
        Mockito.when(productRepository.findByIdentifier("PRD001")).thenReturn(null);
        ProductDto response = productService.toggleStatus("PRD001", true);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Product not found", response.getMessage());
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllWithSpecificationTest() {
        Product product = new Product();
        product.setIdentifier("PROD001");
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD001");
        List<Product> products = List.of(product);
        List<ProductDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Product> page = new PageImpl<>(products, pageable, 1);
        Specification<Product> specification = Mockito.mock(Specification.class);
        Mockito.when(productRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(products), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<ProductDto> response = productService.findAll(specification, pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("PROD001", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(5, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
        Mockito.verify(productRepository).findAll(specification, pageable);
    }

    @Test
    void findAllWithSpecificationNoDataTest() {
        Pageable pageable = PageRequest.of(0, 5);
        Specification<Product> specification = Mockito.mock(Specification.class);
        Page<Product> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        Mockito.when(productRepository.findAll(specification, pageable)).thenReturn(emptyPage);
        Mockito.when(modelMapper.map(Mockito.eq(List.of()), Mockito.any(Type.class))).thenReturn(List.of());
        PaginationResponseDto<ProductDto> response = productService.findAll(specification, pageable);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.getDtoList().isEmpty());
        Assertions.assertEquals(0, response.getTotalRecords());
        Assertions.assertEquals(0, response.getTotalPages());
        Assertions.assertEquals(5, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
        Mockito.verify(productRepository).findAll(specification, pageable);
    }
}