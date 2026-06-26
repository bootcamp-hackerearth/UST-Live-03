package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
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
import java.time.LocalDateTime;
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
        ProductDto dto = new ProductDto();
        dto.setIdentifier("S1");
        Mockito.when(productRepository.findByIdentifier("S1")).thenReturn(null);
        Product product = new Product();
        Mockito.when(modelMapper.map(dto, Product.class)).thenReturn(product);
        Mockito.when(productRepository.save(product)).thenReturn(product);
        ProductDto response = productService.save(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestAlreadyExists() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("S1");
        Product existing = new Product();
        existing.setDeleted(false);
        Mockito.when(productRepository.findByIdentifier("S1")).thenReturn(existing);
        ProductDto response = productService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveTestDeletedExists() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("S1");
        Product existing = new Product();
        existing.setDeleted(true);
        Mockito.when(productRepository.findByIdentifier("S1")).thenReturn(existing);
        ProductDto response = productService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("S1");
        Product existing = new Product();
        existing.setIdentifier("S1");
        existing.setCreatedBy("user");
        existing.setCreatedOn(LocalDateTime.now());
        Mockito.when(productRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(productRepository.save(existing)).thenReturn(existing);
        ProductDto response = productService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("S1");
        Mockito.when(productRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(null);
        ProductDto response = productService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Product product = new Product();
        Mockito.when(productRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(product);
        Mockito.when(productRepository.save(product)).thenReturn(product);
        productService.delete("S1");
        Mockito.verify(productRepository).save(product);
    }

    @Test
    void deleteNullTest() {
        Mockito.when(productRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(null);
        productService.delete("S1");
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllTest() {
        Product product = new Product();
        ProductDto dto = new ProductDto();
        List<Product> list = List.of(product);
        List<ProductDto> dtoList = List.of(dto);
        Pageable pageable = PageRequest.of(0, 1);
        Page<Product> page = new PageImpl<>(list);
        Mockito.when(productRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(Type.class))).thenReturn(dtoList);
        WsDto<ProductDto> response = productService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void findByIdentifierTest() {
        Product product = new Product();
        ProductDto dto = new ProductDto();
        Mockito.when(productRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);
        ProductDto response = productService.findByIdentifier("S1");
        Assertions.assertNotNull(response);
    }

    @Test
    void updateStatusTest() {
        Product product = new Product();
        Mockito.when(productRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(product);
        Mockito.when(productRepository.save(product)).thenReturn(product);
        productService.updateStatus("S1", true);
        Mockito.verify(productRepository).save(product);
    }

    @Test
    void updateStatusNullTest() {
        Mockito.when(productRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(null);
        productService.updateStatus("S1", true);
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllActiveTest() {
        Product product = new Product();
        ProductDto dto = new ProductDto();
        List<Product> list = List.of(product);
        List<ProductDto> dtoList = List.of(dto);
        Mockito.when(productRepository.findByStatusAndDeletedFalse(true)).thenReturn(list);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(Type.class))).thenReturn(dtoList);
        List<ProductDto> response = productService.findAllActive();
        Assertions.assertEquals(1, response.size());
    }
}