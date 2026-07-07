package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private static final String SKU = "SKU001";
    @Mock
    ProductRepository productRepository;
    @Mock
    PriceRepository priceRepository;
    @Mock
    ModelMapper modelMapper;
    @InjectMocks
    ProductServiceImpl service;

    private Product buildProduct() {
        Product p = new Product();
        p.setId(1L);
        p.setIdentifier(SKU);
        p.setStatus(true);
        return p;
    }

    @Test
    void findByIdentifierSuccessWithPrice() {

        Product product = buildProduct();

        ProductDto dto = new ProductDto();

        Price price = new Price();

        PriceDto priceDto = new PriceDto();

        when(productRepository.findByIdentifier(SKU)).thenReturn(product);

        when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);

        when(priceRepository.findByProductId(1L)).thenReturn(price);

        when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        ProductDto response = service.findByIdentifier(SKU);

        assertNotNull(response.getPrice());

    }

    @Test
    void findByIdentifierWithoutPrice() {

        Product product = buildProduct();

        ProductDto dto = new ProductDto();

        when(productRepository.findByIdentifier(SKU)).thenReturn(product);

        when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);

        when(priceRepository.findByProductId(1L)).thenReturn(null);

        ProductDto response = service.findByIdentifier(SKU);

        assertNull(response.getPrice());

    }

    @Test
    void findByIdentifierNotFound() {

        when(productRepository.findByIdentifier(SKU)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.findByIdentifier(SKU));

    }

    @Test
    void saveSuccess() {

        ProductDto dto = new ProductDto();

        dto.setIdentifier(" SKU001 ");

        Product product = new Product();

        when(productRepository.findByIdentifier("SKU001")).thenReturn(null);

        when(modelMapper.map(dto, Product.class)).thenReturn(product);

        ProductDto result = service.save(dto);

        verify(productRepository).save(product);

        assertEquals("SKU001", result.getIdentifier());

    }

    @Test
    void saveAlreadyExists() {

        ProductDto dto = new ProductDto();

        dto.setIdentifier(SKU);

        Product existing = buildProduct();

        when(productRepository.findByIdentifier(SKU)).thenReturn(existing);

        ProductDto result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals("Product with skuCode - SKU001 already exists", result.getMessage());

    }

    @Test
    void saveSoftDeleted() {

        ProductDto dto = new ProductDto();

        dto.setIdentifier(SKU);

        Product existing = buildProduct();

        existing.setDeleted(true);

        when(productRepository.findByIdentifier(SKU)).thenReturn(existing);

        ProductDto result = service.save(dto);

        assertFalse(result.isSuccess());

        assertTrue(result.getMessage().contains("soft deleted"));

    }

    @Test
    void updateSuccess() {

        ProductDto dto = new ProductDto();

        dto.setIdentifier(" SKU001 ");

        Product product = buildProduct();

        when(productRepository.findByIdentifier(SKU)).thenReturn(product);

        ProductDto result = service.update(dto);

        verify(modelMapper).map(dto, product);

        verify(productRepository).save(product);

        assertEquals(dto, result);

    }

    @Test
    void updateNotFound() {

        ProductDto dto = new ProductDto();

        dto.setIdentifier(SKU);

        when(productRepository.findByIdentifier(SKU)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.update(dto));

    }

    @Test
    void deleteSuccess() {

        Product product = buildProduct();

        when(productRepository.findByIdentifier(SKU)).thenReturn(product);

        boolean result = service.delete(SKU);

        assertTrue(result);

        verify(productRepository).save(product);

    }

    @Test
    void deleteNotFound() {

        when(productRepository.findByIdentifier(SKU)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(SKU));

    }

    @Test
    void findAllPageable() {

        Pageable pageable = PageRequest.of(0, 10);

        Product product = buildProduct();

        ProductDto dto = new ProductDto();

        Price price = new Price();

        PriceDto priceDto = new PriceDto();

        Page<Product> page = new PageImpl<>(List.of(product));

        when(productRepository.findByDeletedFalse(pageable)).thenReturn(page);

        when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);

        when(priceRepository.findByProductId(1L)).thenReturn(price);

        when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        WsDto<ProductDto> result = service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());

    }

    @Test
    void findAllEmpty() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> page = new PageImpl<>(Collections.emptyList());

        when(productRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<ProductDto> ws = service.findAll(pageable);

        assertEquals(0, ws.getDtoList().size());

    }

    @Test
    void findAllSpecification() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Product> spec = mock(Specification.class);

        Product product = buildProduct();

        ProductDto dto = new ProductDto();

        Page<Product> page = new PageImpl<>(List.of(product));

        when(productRepository.findAll(spec, pageable)).thenReturn(page);

        when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);

        when(priceRepository.findByProductId(1L)).thenReturn(null);

        WsDto<ProductDto> ws = service.findAll(spec, pageable, "lap");

        assertEquals("lap", ws.getKeyword());

        assertEquals(1, ws.getDtoList().size());

    }

    @Test
    void findAllSpecificationEmpty() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Product> spec = mock(Specification.class);

        Page<Product> page = new PageImpl<>(Collections.emptyList());

        when(productRepository.findAll(spec, pageable)).thenReturn(page);

        WsDto<ProductDto> ws = service.findAll(spec, pageable, "abc");

        assertEquals(0, ws.getDtoList().size());

    }

    @Test
    void toggleStatusTrueToFalse() {

        Product product = buildProduct();

        product.setStatus(true);

        ProductDto dto = new ProductDto();

        when(productRepository.findByIdentifier(SKU)).thenReturn(product);

        when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);

        service.toggleStatus(SKU);

        assertFalse(product.isStatus());

        verify(productRepository).save(product);

    }

    @Test
    void toggleStatusFalseToTrue() {

        Product product = buildProduct();

        product.setStatus(false);

        when(productRepository.findByIdentifier(SKU)).thenReturn(product);

        when(modelMapper.map(product, ProductDto.class)).thenReturn(new ProductDto());

        service.toggleStatus(SKU);

        assertTrue(product.isStatus());

    }

    @Test
    void toggleStatusNotFound() {

        when(productRepository.findByIdentifier(SKU)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.toggleStatus(SKU));

    }

    @Test
    void findIfTrue() {

        List<Product> entities = List.of(buildProduct());

        List<ProductDto> dtos = List.of(new ProductDto());

        when(productRepository.findByStatusIsTrue()).thenReturn(entities);

        when(modelMapper.map(eq(entities), any(Type.class))).thenReturn(dtos);

        List<ProductDto> result = service.findIfTrue();

        assertEquals(1, result.size());

    }

    @Test
    void findIfTrueEmpty() {

        when(productRepository.findByStatusIsTrue()).thenReturn(Collections.emptyList());

        when(modelMapper.map(eq(Collections.emptyList()), any(Type.class))).thenReturn(Collections.emptyList());

        List<ProductDto> result = service.findIfTrue();

        assertTrue(result.isEmpty());

    }

}