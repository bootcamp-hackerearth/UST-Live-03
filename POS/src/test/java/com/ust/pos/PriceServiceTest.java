package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.price.service.impl.PriceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private PriceServiceImpl priceService;

    private Price price;
    private PriceDto priceDto;

    @BeforeEach
    void setUp() {
        price = new Price();
        price.setId(1L);
        price.setIdentifier("PRC-001");
        price.setProductIdentifier("PROD-001");
        price.setStatus(true);
        price.setDeleted(false);

        priceDto = new PriceDto();
        priceDto.setIdentifier("PRC-001");
        priceDto.setProductIdentifier("PROD-001");
    }

    @Test
    void testFindByIdentifier_Success() {
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(price);

        PriceDto result = priceService.findByIdentifier("PRC-001");

        assertNotNull(result);
        assertEquals("PRC-001", result.getIdentifier());
    }

    @Test
    void testFindByIdentifier_ThrowsResourceNotFoundException() {
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> priceService.findByIdentifier("PRC-001"));
    }

    @Test
    void testFindByProductIdentifier() {
        when(priceRepository.findByProductIdentifier("PROD-001")).thenReturn(price);

        PriceDto result = priceService.findByProductIdentifier("PROD-001");

        assertNotNull(result);
        assertEquals("PROD-001", result.getProductIdentifier());
    }

    @Test
    void testSave_WhenDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> priceService.save(null));
    }

    @Test
    void testSave_WhenIdentifierIsNull() {
        priceDto.setIdentifier(null);
        assertThrows(IllegalArgumentException.class, () -> priceService.save(priceDto));
    }

    @Test
    void testSave_WhenProductIdentifierAlreadyExistsAndNotDeleted() {
        Price existingByProduct = new Price();
        existingByProduct.setDeleted(false);
        when(priceRepository.findByProductIdentifier("PROD-001")).thenReturn(existingByProduct);

        PriceDto result = priceService.save(priceDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("ProductIdentifier already exists"));
    }

    @Test
    void testSave_WhenIdentifierAlreadyExistsAndNotDeleted() {
        when(priceRepository.findByProductIdentifier("PROD-001")).thenReturn(null);
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(price);

        PriceDto result = priceService.save(priceDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_WhenIdentifierAlreadyExistsButDeleted() {
        price.setDeleted(true);
        when(priceRepository.findByProductIdentifier("PROD-001")).thenReturn(null);
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(price);

        PriceDto result = priceService.save(priceDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testSave_Success() {
        when(priceRepository.findByProductIdentifier("PROD-001")).thenReturn(null);
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(null);
        when(priceRepository.save(any(Price.class))).thenReturn(price);

        PriceDto result = priceService.save(priceDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Price created successfully", result.getMessage());
    }

    @Test
    void testUpdate_WhenPriceNotFound() {
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(null);

        PriceDto result = priceService.update(priceDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testUpdate_WhenPriceDeleted() {
        price.setDeleted(true);
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(price);

        PriceDto result = priceService.update(priceDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testUpdate_WithConflictingProductIdentifier() {
        priceDto.setProductIdentifier("NEW-PROD");
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(price);

        Price conflictPrice = new Price();
        conflictPrice.setDeleted(false);
        when(priceRepository.findByProductIdentifier("NEW-PROD")).thenReturn(conflictPrice);

        PriceDto result = priceService.update(priceDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("ProductIdentifier already exists"));
    }

    @Test
    void testUpdate_Success() {
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(price);
        when(priceRepository.save(any(Price.class))).thenReturn(price);

        PriceDto result = priceService.update(priceDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Price updated successfully", result.getMessage());
    }

    @Test
    void testDelete_WhenPriceNotFound() {
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(null);

        priceService.delete("PRC-001");

        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    void testDelete_Success() {
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(price);
        when(priceRepository.save(any(Price.class))).thenReturn(price);

        priceService.delete("PRC-001");

        verify(priceRepository, times(1)).save(any(Price.class));
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Price> page = new PageImpl<>(Collections.singletonList(price), pageable, 1);
        when(priceRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<PriceDto> result = priceService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertFalse(result.getDtoList().isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testFindAllWithSpecification() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Price> page = new PageImpl<>(Collections.singletonList(price), pageable, 1);
        Specification<Price> spec = mock(Specification.class);
        when(priceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        WsDto<PriceDto> result = priceService.findAll(spec, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void testToggleStatus_WhenPriceNotFound() {
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(null);

        PriceDto result = priceService.toggleStatus("PRC-001");

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testToggleStatus_Success() {
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(price);
        when(priceRepository.save(any(Price.class))).thenReturn(price);

        PriceDto result = priceService.toggleStatus("PRC-001");

        assertNotNull(result);
        assertFalse(result.isStatus());
    }

    @Test
    void testFindIfTrue() {
        when(priceRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(Collections.singletonList(price));

        List<PriceDto> result = priceService.findIfTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}