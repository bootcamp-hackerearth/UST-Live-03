package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.impl.ProductServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductServiceImpl service;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 2);

        Page<Product> page = new PageImpl<>(List.of(new Product()), pageable, 1);

        when(productRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new ProductDto()));

        WsDto<ProductDto> result = service.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void findByIdentifierTest() {
        Product product = new Product();
        ProductDto dto = new ProductDto();

        when(productRepository.findByIdentifier("P1")).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);

        ProductDto result = service.findByIdentifier("P1");

        assertNotNull(result);
    }

    @Test
    void saveSuccessTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        when(productRepository.findByIdentifier("P1")).thenReturn(null);
        when(modelMapper.map(dto, Product.class)).thenReturn(new Product());

        ProductDto result = service.save(dto);

        assertTrue(result.isSuccess());
        verify(productRepository).save(any());
    }

    @Test
    void saveDuplicateActiveTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product existing = new Product();
        existing.setDeleted(false);

        when(productRepository.findByIdentifier("P1")).thenReturn(existing);

        ProductDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(productRepository, never()).save(any());
    }

    @Test
    void saveDuplicateDeletedTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product existing = new Product();
        existing.setDeleted(true);

        when(productRepository.findByIdentifier("P1")).thenReturn(existing);

        ProductDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("deleted"));
    }

    @Test
    void updateSuccessTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product existing = new Product();

        when(productRepository.findByIdentifier("P1")).thenReturn(existing);

        ProductDto result = service.update(dto);

        assertTrue(result.isSuccess());
        verify(productRepository).save(existing);
    }

    @Test
    void updateNotFoundTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        when(productRepository.findByIdentifier("P1")).thenReturn(null);

        ProductDto result = service.update(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Product product = new Product();
        product.setDeleted(false);

        when(productRepository.findByIdentifier("P1")).thenReturn(product);

        service.delete("P1");

        assertTrue(product.isDeleted());
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Product product = new Product();
        product.setStatus(true);

        when(productRepository.findByIdentifier("P1")).thenReturn(product);

        service.toggleStatus("P1");

        assertFalse(product.isStatus());
        verify(productRepository).save(product);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Product product = new Product();
        product.setStatus(false);

        when(productRepository.findByIdentifier("P1")).thenReturn(product);

        service.toggleStatus("P1");

        assertTrue(product.isStatus());
        verify(productRepository).save(product);
    }

    @Test
    void toggleStatusNotFoundTest() {
        when(productRepository.findByIdentifier("P1")).thenReturn(null);

        service.toggleStatus("P1");

        verify(productRepository, never()).save(any());
    }
}