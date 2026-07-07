package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.price.service.impl.PriceServiceImpl;
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    private static final Long PRODUCT_ID = 1L;
    private static final Long PRICE_ID = 10L;
    private static final String PRODUCT_NAME = "Laptop";
    private static final String IDENTIFIER = "PROD001";
    @Mock
    private PriceRepository priceRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private PriceServiceImpl service;

    @Test
    void createPriceSuccess() {

        Product product = new Product();
        product.setId(PRODUCT_ID);
        product.setProductName(PRODUCT_NAME);
        product.setIdentifier(IDENTIFIER);

        PriceDto dto = new PriceDto();
        dto.setProductId(PRODUCT_ID);

        Price price = new Price();

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        when(priceRepository.findByProductId(PRODUCT_ID)).thenReturn(null);

        when(modelMapper.map(dto, Price.class)).thenReturn(price);

        PriceDto result = service.createPrice(dto);

        assertTrue(result.isSuccess());
        assertEquals("Price created successfully", result.getMessage());

        assertEquals(PRODUCT_NAME, result.getProductName());

        verify(priceRepository).save(any(Price.class));

    }


    @Test
    void createPriceAlreadyExists() {

        PriceDto dto = new PriceDto();
        dto.setProductId(PRODUCT_ID);

        Product product = new Product();
        product.setId(PRODUCT_ID);

        Price existing = new Price();
        existing.setIdentifier("PRICE001");

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        when(priceRepository.findByProductId(PRODUCT_ID)).thenReturn(existing);

        PriceDto result = service.createPrice(dto);

        assertFalse(result.isSuccess());

        assertEquals("Price for product - PRICE001 already exists", result.getMessage());

        verify(priceRepository, never()).save(any());

    }


    @Test
    void createPriceDeletedPriceExists() {

        PriceDto dto = new PriceDto();
        dto.setProductId(PRODUCT_ID);

        Product product = new Product();

        Price existing = new Price();
        existing.setDeleted(true);
        existing.setIdentifier("PRICE001");

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        when(priceRepository.findByProductId(PRODUCT_ID)).thenReturn(existing);

        PriceDto result = service.createPrice(dto);

        assertFalse(result.isSuccess());

        assertEquals("Price for product - PRICE001 has been soft deleted.(Rollback by changing status)", result.getMessage());

    }


    @Test
    void createPriceProductNotFound() {

        PriceDto dto = new PriceDto();
        dto.setProductId(PRODUCT_ID);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.createPrice(dto));

    }


    @Test
    void updatePriceSuccess() {

        PriceDto dto = new PriceDto();

        dto.setId(PRICE_ID);

        dto.setSellingPrice(BigDecimal.valueOf(100));

        dto.setCostPrice(BigDecimal.valueOf(50));

        Price price = new Price();

        price.setId(PRICE_ID);
        price.setProductId(PRODUCT_ID);

        Product product = new Product();

        product.setProductName(PRODUCT_NAME);
        product.setIdentifier(IDENTIFIER);

        when(priceRepository.findById(PRICE_ID)).thenReturn(Optional.of(price));

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        PriceDto result = service.updatePrice(dto);

        assertTrue(result.isSuccess());

        assertEquals("Price updated successfully", result.getMessage());

        verify(priceRepository).save(price);

        verify(modelMapper).map(price, dto);

    }


    @Test
    void updatePriceWithoutProduct() {

        PriceDto dto = new PriceDto();

        dto.setId(PRICE_ID);

        Price price = new Price();

        price.setId(PRICE_ID);

        price.setProductId(PRODUCT_ID);

        when(priceRepository.findById(PRICE_ID)).thenReturn(Optional.of(price));

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        PriceDto result = service.updatePrice(dto);

        assertTrue(result.isSuccess());

        verify(priceRepository).save(any());

    }


    @Test
    void updatePriceNotFound() {

        PriceDto dto = new PriceDto();

        dto.setId(PRICE_ID);

        when(priceRepository.findById(PRICE_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updatePrice(dto));

    }


    @Test
    void findAllSuccess() {

        Pageable pageable = PageRequest.of(0, 10);

        Price price = new Price();

        price.setProductId(PRODUCT_ID);

        Page<Price> page = new PageImpl<>(List.of(price));

        Product product = new Product();

        product.setProductName(PRODUCT_NAME);
        product.setIdentifier(IDENTIFIER);

        PriceDto dto = new PriceDto();

        when(priceRepository.findByDeletedFalse(pageable)).thenReturn(page);

        when(modelMapper.map(price, PriceDto.class)).thenReturn(dto);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        WsDto<PriceDto> result = service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());

        assertEquals(PRODUCT_NAME, result.getDtoList().get(0).getProductName());

    }


    @Test
    void findAllWithoutProduct() {

        Pageable pageable = PageRequest.of(0, 10);

        Price price = new Price();

        price.setProductId(PRODUCT_ID);

        Page<Price> page = new PageImpl<>(List.of(price));

        when(priceRepository.findByDeletedFalse(pageable)).thenReturn(page);

        when(modelMapper.map(price, PriceDto.class)).thenReturn(new PriceDto());

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        WsDto<PriceDto> result = service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());

    }


    @Test
    void findAllEmpty() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Price> page = new PageImpl<>(Collections.emptyList());

        when(priceRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<PriceDto> result = service.findAll(pageable);

        assertEquals(0, result.getDtoList().size());

    }


    @Test
    void deletePriceSuccess() {

        Price price = new Price();

        price.setId(PRICE_ID);

        when(priceRepository.findById(PRICE_ID)).thenReturn(Optional.of(price));

        boolean result = service.deletePrice(PRICE_ID);

        assertTrue(result);

        verify(priceRepository).save(price);

    }


    @Test
    void deletePriceNotFound() {

        when(priceRepository.findById(PRICE_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.deletePrice(PRICE_ID));

    }


    @Test
    void getPriceByIdSuccess() {

        Price price = new Price();

        price.setId(PRICE_ID);
        price.setProductId(PRODUCT_ID);

        Product product = new Product();

        product.setProductName(PRODUCT_NAME);
        product.setIdentifier(IDENTIFIER);

        when(priceRepository.findById(PRICE_ID)).thenReturn(Optional.of(price));

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        PriceDto result = service.getPriceById(PRICE_ID);

        assertTrue(result.isSuccess());

        assertEquals(PRODUCT_NAME, result.getProductName());

    }


    @Test
    void getPriceByIdWithoutProduct() {

        Price price = new Price();

        price.setProductId(PRODUCT_ID);

        when(priceRepository.findById(PRICE_ID)).thenReturn(Optional.of(price));

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        PriceDto result = service.getPriceById(PRICE_ID);

        assertTrue(result.isSuccess());

    }


    @Test
    void getPriceByIdNotFound() {

        when(priceRepository.findById(PRICE_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getPriceById(PRICE_ID));

    }


    @Test
    void findAllSpecificationSuccess() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Price> spec = mock(Specification.class);

        Price price = new Price();

        price.setProductId(PRODUCT_ID);

        Page<Price> page = new PageImpl<>(List.of(price));

        Product product = new Product();

        product.setProductName(PRODUCT_NAME);
        product.setIdentifier(IDENTIFIER);

        PriceDto dto = new PriceDto();

        when(priceRepository.findAll(spec, pageable)).thenReturn(page);

        when(modelMapper.map(price, PriceDto.class)).thenReturn(dto);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        WsDto<PriceDto> result = service.findAll(spec, pageable, "laptop");

        assertEquals("laptop", result.getKeyword());

        assertEquals(1, result.getDtoList().size());

    }


    @Test
    void findAllSpecificationEmpty() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Price> spec = mock(Specification.class);

        Page<Price> page = new PageImpl<>(Collections.emptyList());

        when(priceRepository.findAll(spec, pageable)).thenReturn(page);

        WsDto<PriceDto> result = service.findAll(spec, pageable, "abc");

        assertEquals(0, result.getDtoList().size());

        assertEquals("abc", result.getKeyword());

    }

}