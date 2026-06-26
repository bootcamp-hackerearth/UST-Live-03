package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Product;
import com.ust.pos.modell.ProductRepository;
import com.ust.pos.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        Product product = new Product();
        when(productRepository.findByIdentifier("P1")).thenReturn(null);
        when(modelMapper.map(dto, Product.class)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        ProductDto response = productService.save(dto);
        Assertions.assertEquals("P1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        verify(productRepository).save(product);
    }

    @Test
    void saveDuplicateTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        when(productRepository.findByIdentifier("P1")).thenReturn(new Product());
        ProductDto response = productService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("product with identifier - P1 already exists", response.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void findByIdentifierTest() {
        Product product = new Product();
        product.setIdentifier("P1");
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        when(productRepository.findByIdentifierAndDeletedFalse("P1")).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);
        ProductDto response = productService.findByIdentifier("P1");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("P1", response.getIdentifier());
    }

    @Test
    void updateSuccessTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        Product existing = new Product();
        existing.setIdentifier("P1");
        when(productRepository.findByIdentifierAndDeletedFalse("P1")).thenReturn(existing);
        when(productRepository.save(existing)).thenReturn(existing);
        ProductDto response = productService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        verify(productRepository).save(existing);
    }

    @Test
    void updateNotFoundTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        when(productRepository.findByIdentifierAndDeletedFalse("P1")).thenReturn(null);
        ProductDto response = productService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Product with identifier - P1 not found", response.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Product product = new Product();
        product.setIdentifier("P1");
        when(productRepository.findByIdentifierAndDeletedFalse("P1")).thenReturn(product);
        productService.delete("P1");
        verify(productRepository).save(product);
    }

    @Test
    void findAllTest() {
        Product product = new Product();
        product.setIdentifier("P1");
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        List<Product> products = List.of(product);
        List<ProductDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(products, pageable, products.size());
        when(productRepository.findALlByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(products), any(Type.class))).thenReturn(dtos);
        WsDto<ProductDto> response = productService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPage());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
        verify(productRepository).findALlByDeletedFalse(pageable);
    }

    @Test
    void findAllActiveTest() {
        Product product = new Product();
        product.setIdentifier("P1");
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        when(productRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of(product));
        when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);
        List<ProductDto> response = productService.findAllActive();
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("P1", response.get(0).getIdentifier()
        );
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Product product = new Product();
        product.setIdentifier("P1");
        product.setStatus(true);
        ProductDto dto = new ProductDto();
        when(productRepository.findByIdentifierAndDeletedFalse("P1")).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);
        productService.toggleStatus("P1");
        Assertions.assertFalse(product.getStatus());
        verify(productRepository).save(product);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Product product = new Product();
        product.setIdentifier("P1");
        product.setStatus(false);
        ProductDto dto = new ProductDto();
        when(productRepository.findByIdentifierAndDeletedFalse("P1")).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);
        productService.toggleStatus("P1");
        Assertions.assertTrue(product.getStatus());
        verify(productRepository).save(product);
    }

    @Test
    void toggleStatusNullToTrueTest() {
        Product product = new Product();
        product.setIdentifier("P1");
        product.setStatus(null);
        ProductDto dto = new ProductDto();
        when(productRepository.findByIdentifierAndDeletedFalse("P1")).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);
        productService.toggleStatus("P1");
        Assertions.assertTrue(product.getStatus());
        verify(productRepository).save(product);
    }

    @Test
    void toggleStatusNotFoundTest() {
        when(productRepository.findByIdentifierAndDeletedFalse("P1")).thenReturn(null);
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> productService.toggleStatus("P1"));
        Assertions.assertEquals("Product not found with identifier: P1", ex.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void findAllActiveEmptyListTest() {
        when(productRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of());
        List<ProductDto> response = productService.findAllActive();
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isEmpty());
    }
}