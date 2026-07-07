package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.impl.StockServiceImpl;
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
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private StockServiceImpl stockService;

    private Stock stockEntity;
    private StockDto stockDto;
    private String expectedIdentifier;

    @BeforeEach
    void setUp() {
        expectedIdentifier = "STK_PROD01_WH01";

        stockEntity = new Stock();
        stockEntity.setId(1L);
        stockEntity.setIdentifier(expectedIdentifier);
        stockEntity.setProductIdentifier("PROD01");
        stockEntity.setWarehouseIdentifier("WH01");
        stockEntity.setStatus(true);
        stockEntity.setDeleted(false);

        stockDto = new StockDto();
        stockDto.setProductIdentifier("PROD01");
        stockDto.setWarehouseIdentifier("WH01");
    }

    @Test
    void testSave_WhenDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> stockService.save(null));
    }

    @Test
    void testSave_WhenProductIdentifierIsNull() {
        stockDto.setProductIdentifier(null);
        assertThrows(IllegalArgumentException.class, () -> stockService.save(stockDto));
    }

    @Test
    void testSave_WhenWarehouseIdentifierIsNull() {
        stockDto.setWarehouseIdentifier(null);
        assertThrows(IllegalArgumentException.class, () -> stockService.save(stockDto));
    }

    @Test
    void testSave_WhenStockExistsAndNotDeleted() {
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(stockEntity);

        StockDto result = stockService.save(stockDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        assertEquals(expectedIdentifier, stockDto.getIdentifier());
    }

    @Test
    void testSave_WhenStockWasPreviouslyDeleted() {
        stockEntity.setDeleted(true);
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(stockEntity);

        StockDto result = stockService.save(stockDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testSave_Success() {
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(null);
        when(stockRepository.save(any(Stock.class))).thenReturn(stockEntity);

        StockDto result = stockService.save(stockDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Stock created successfully", result.getMessage());
        assertEquals(expectedIdentifier, result.getIdentifier());
    }

    @Test
    void testUpdate_WhenStockNotFound() {
        stockDto.setIdentifier(expectedIdentifier);
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(null);

        StockDto result = stockService.update(stockDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testUpdate_WhenStockgetDeleted() {
        stockDto.setIdentifier(expectedIdentifier);
        stockEntity.setDeleted(true);
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(stockEntity);

        StockDto result = stockService.update(stockDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testUpdate_Success() {
        stockDto.setIdentifier(expectedIdentifier);
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(stockEntity);
        when(stockRepository.save(any(Stock.class))).thenReturn(stockEntity);

        StockDto result = stockService.update(stockDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Stock updated successfully", result.getMessage());
    }

    @Test
    void testDeleteByIdentifier_WhenStockNotFound() {
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(null);

        stockService.deleteByIdentifier(expectedIdentifier);

        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    void testDeleteByIdentifier_Success() {
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(stockEntity);
        when(stockRepository.save(any(Stock.class))).thenReturn(stockEntity);

        stockService.deleteByIdentifier(expectedIdentifier);

        verify(stockRepository, times(1)).save(stockEntity);
    }

    @Test
    void testFindByIdentifier() {
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(stockEntity);

        StockDto result = stockService.findByIdentifier(expectedIdentifier);

        assertNotNull(result);
        assertEquals(expectedIdentifier, result.getIdentifier());
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Stock> entityList = Collections.singletonList(stockEntity);
        Page<Stock> page = new PageImpl<>(entityList, pageable, 1);

        when(stockRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<StockDto> result = stockService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(0, result.getPage());
    }

    @Test
    void testToggleStatus_WhenStockNotFound() {
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(null);

        StockDto result = stockService.toggleStatus(expectedIdentifier);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testToggleStatus_Success() {
        stockEntity.setStatus(true);
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(stockEntity);
        when(stockRepository.save(any(Stock.class))).thenReturn(stockEntity);

        StockDto result = stockService.toggleStatus(expectedIdentifier);

        assertNotNull(result);
        assertFalse(result.isStatus());
    }

    @Test
    void testFindIfTrue() {
        List<Stock> activeStocks = Collections.singletonList(stockEntity);
        when(stockRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(activeStocks);

        List<StockDto> result = stockService.findIfTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}