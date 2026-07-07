package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
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

    private Price priceEntity;
    private PriceDto priceDto;

    @BeforeEach
    void setUp() {
        priceEntity = new Price();
        priceEntity.setId(1L);
        priceEntity.setIdentifier("PRC-001");
        priceEntity.setProductIdentifier("PROD-X");
        priceEntity.setStatus(true);
        priceEntity.setDeleted(false);

        priceDto = new PriceDto();
        priceDto.setIdentifier("PRC-001");
        priceDto.setProductIdentifier("PROD-X");
    }

    @Test
    void testFindByIdentifier() {
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(priceEntity);

        PriceDto result = priceService.findByIdentifier("PRC-001");

        assertNotNull(result);
        assertEquals("PRC-001", result.getIdentifier());
    }

    @Test
    void testFindByProductIdentifier() {
        when(priceRepository.findByProductIdentifier("PROD-X")).thenReturn(priceEntity);

        PriceDto result = priceService.findByProductIdentifier("PROD-X");

        assertNotNull(result);
        assertEquals("PROD-X", result.getProductIdentifier());
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
        when(priceRepository.findByProductIdentifier("PROD-X")).thenReturn(priceEntity);

        PriceDto result = priceService.save(priceDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("ProductIdentifier already exists"));
    }

    @Test
    void testSave_WhenPriceIdentifierAlreadyExistsAndNotDeleted() {
        when(priceRepository.findByProductIdentifier("PROD-X")).thenReturn(null);
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(priceEntity);

        PriceDto result = priceService.save(priceDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_WhenPriceIdentifierWasPreviouslyDeleted() {
        priceEntity.setDeleted(true);
        when(priceRepository.findByProductIdentifier("PROD-X")).thenReturn(null);
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(priceEntity);

        PriceDto result = priceService.save(priceDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testSave_Success() {
        when(priceRepository.findByProductIdentifier("PROD-X")).thenReturn(null);
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(null);
        when(priceRepository.save(any(Price.class))).thenReturn(priceEntity);

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
    void testUpdate_WhenPricegetDeleted() {
        priceEntity.setDeleted(true);
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(priceEntity);

        PriceDto result = priceService.update(priceDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testUpdate_WithConflictingProductIdentifier() {
        priceDto.setProductIdentifier("PROD-NEW");
        Price conflictEntity = new Price();
        conflictEntity.setProductIdentifier("PROD-NEW");
        conflictEntity.setDeleted(false);

        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(priceEntity);
        when(priceRepository.findByProductIdentifier("PROD-NEW")).thenReturn(conflictEntity);

        PriceDto result = priceService.update(priceDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("ProductIdentifier already exists"));
    }

    @Test
    void testUpdate_Success() {
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(priceEntity);
        when(priceRepository.save(any(Price.class))).thenReturn(priceEntity);

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
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(priceEntity);
        when(priceRepository.save(any(Price.class))).thenReturn(priceEntity);

        priceService.delete("PRC-001");

        verify(priceRepository, times(1)).save(priceEntity);
        assertTrue(priceEntity.getDeleted());
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Price> entityList = Collections.singletonList(priceEntity);
        Page<Price> page = new PageImpl<>(entityList, pageable, 1);

        when(priceRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<PriceDto> result = priceService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(0, result.getPage());
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
        priceEntity.setStatus(true);
        when(priceRepository.findByIdentifier("PRC-001")).thenReturn(priceEntity);
        when(priceRepository.save(any(Price.class))).thenReturn(priceEntity);

        PriceDto result = priceService.toggleStatus("PRC-001");

        assertNotNull(result);
        assertFalse(result.getStatus());
    }

    @Test
    void testFindIfTrue() {
        List<Price> activePrices = Collections.singletonList(priceEntity);
        when(priceRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(activePrices);

        List<PriceDto> result = priceService.findIfTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}