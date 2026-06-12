package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private StockServiceImpl stockService;
    private StockDto stockDto;
    private Stock stock;

    @BeforeEach
    void setUp() {
        stockDto = new StockDto();
        stock = new Stock();
        stockDto.setProduct("PROD-001");
        stockDto.setWarehouse("WH1");
        stockDto.setQuantity(20);
        stockDto.setMinimumStock(10);
    }

    @Test
    void testSave_StockAvailable() {
        when(stockRepository.findByIdentifier(null)).thenReturn(null);
        when(modelMapper.map(any(StockDto.class), eq(Stock.class))).thenReturn(stock);

        StockDto result = stockService.save(stockDto);
        assertEquals("Available", result.getStockStatus());
        assertEquals("PROD-001_WH1", result.getIdentifier());
        verify(stockRepository).save(stock);
    }

    @Test
    void testSave_LowStock() {
        stockDto.setQuantity(5);
        when(stockRepository.findByIdentifier(null)).thenReturn(null);
        when(modelMapper.map(any(StockDto.class), eq(Stock.class))).thenReturn(stock);
        StockDto result = stockService.save(stockDto);
        assertEquals("Low Stock", result.getStockStatus());
        verify(stockRepository).save(stock);
    }

    @Test
    void testSave_OutOfStock() {
        stockDto.setQuantity(0);
        when(stockRepository.findByIdentifier(null)).thenReturn(null);
        when(modelMapper.map(any(StockDto.class), eq(Stock.class))).thenReturn(stock);
        StockDto result = stockService.save(stockDto);
        assertEquals("Out of Stock", result.getStockStatus());
        verify(stockRepository).save(stock);
    }

    @Test
    void testSave_AlreadyExists() {
        Stock existingStock = new Stock();
        stockDto.setIdentifier("PROD-001");
        when(stockRepository.findByIdentifier("PROD-001")).thenReturn(existingStock);
        StockDto result = stockService.save(stockDto);
        assertFalse(result.isSuccess());
        assertEquals("Role with identifier - PROD-001 already exists", result.getMessage());
        verify(stockRepository, never()).save(any());
    }

    @Test
    void testUpdate_Available() {

        stockDto.setIdentifier("PROD-001_WH1");
        stockDto.setQuantity(20);
        stockDto.setMinimumStock(10);

        when(stockRepository.findByIdentifier("PROD-001_WH1"))
                .thenReturn(stock);

        StockDto result = stockService.update(stockDto);

        assertEquals("Available", result.getStockStatus());

        verify(modelMapper).map(stockDto, stock);
        verify(stockRepository).save(stock);
    }

    @Test
    void testUpdate_LowStock() {

        stockDto.setIdentifier("PROD-001_WH1");
        stockDto.setQuantity(5);
        stockDto.setMinimumStock(10);

        when(stockRepository.findByIdentifier("PROD-001_WH1"))
                .thenReturn(stock);

        StockDto result = stockService.update(stockDto);

        assertEquals("Low Stock", result.getStockStatus());

        verify(modelMapper).map(stockDto, stock);
        verify(stockRepository).save(stock);
    }
    @Test
    void testUpdate_OutOfStock() {

        stockDto.setIdentifier("PROD-001_WH1");
        stockDto.setQuantity(0);
        stockDto.setMinimumStock(10);

        when(stockRepository.findByIdentifier("PROD-001_WH1"))
                .thenReturn(stock);

        StockDto result = stockService.update(stockDto);

        assertEquals("Out of Stock", result.getStockStatus());

        verify(modelMapper).map(stockDto, stock);
        verify(stockRepository).save(stock);
    }

    @Test
    void testUpdate_NotFound() {

        stockDto.setIdentifier("PROD-001_WH1");

        when(stockRepository.findByIdentifier("PROD-001_WH1"))
                .thenReturn(null);

        StockDto result = stockService.update(stockDto);

        assertFalse(result.isSuccess());
        assertEquals(
                "product with identifier - PROD-001_WH1 not found",
                result.getMessage()
        );

        verify(stockRepository, never()).save(any());
    }




    @Test
    void testDelete() {
        stockService.delete("PROD-001_WH1");
        verify(stockRepository).deleteByIdentifier("PROD-001_WH1");
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Stock> stockPage = new PageImpl<>(Collections.singletonList(stock));
        when(stockRepository.findAll(pageable)).thenReturn(stockPage);
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(stockDto));
        WsDto<StockDto> result = stockService.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
        verify(stockRepository).findAll(pageable);
        verify(modelMapper).map(anyList(), any(Type.class));
    }

    @Test
    void testFindByIdentifier() {
        when(stockRepository.findByIdentifier("PROD-001_WH1")).thenReturn(stock);
        when(modelMapper.map(stock, StockDto.class)).thenReturn(stockDto);
        StockDto result = stockService.findByIdentifier("PROD-001_WH1");
        assertNotNull(result);
        verify(stockRepository).findByIdentifier("PROD-001_WH1");
    }

}