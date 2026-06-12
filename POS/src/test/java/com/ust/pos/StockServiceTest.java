package com.ust.pos;

import com.ust.pos.dto.StockDto;
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
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private StockServiceImpl stockService;

    private Stock stock;
    private StockDto stockDto;

    @BeforeEach
    void setUp() {
        stockDto = new StockDto();
        stockDto.setIdentifier("PROD-001");
        stockDto.setWarehouse("WH1");
        stockDto.setQuantity(50);
        stockDto.setMinimumStock(20);
        stockDto.setSuccess(true);

        stock = new Stock();
        stock.setIdentifier("PROD-001_WH1");
        stock.setQuantity(50);
        stock.setMinimumStock(20);
        stock.setStockStatus("Available");
    }

    @Test
    void save_existingStock() {
        when(stockRepository.findByIdentifier("PROD-001")).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));

        verify(stockRepository, never()).save(any());
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void save_available() {
        when(stockRepository.findByIdentifier("PROD-001")).thenReturn(null);
        when(modelMapper.map(any(StockDto.class), eq(Stock.class))).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertEquals("Available", result.getStockStatus());
        assertEquals("PROD-001_WH1", result.getIdentifier());

        verify(stockRepository).save(stock);
    }

    @Test
    void save_lowStock() {
        stockDto.setQuantity(10);

        when(stockRepository.findByIdentifier("PROD-001")).thenReturn(null);
        when(modelMapper.map(any(StockDto.class), eq(Stock.class))).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertEquals("Low Stock", result.getStockStatus());
        assertEquals("PROD-001_WH1", result.getIdentifier());

        verify(stockRepository).save(stock);
    }

    @Test
    void save_outOfStock() {
        stockDto.setQuantity(0);

        when(stockRepository.findByIdentifier("PROD-001")).thenReturn(null);
        when(modelMapper.map(any(StockDto.class), eq(Stock.class))).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertEquals("Out of Stock", result.getStockStatus());
        verify(stockRepository).save(stock);
    }

    @Test
    void save_boundary_equalMinimumStock() {
        stockDto.setQuantity(20);

        when(stockRepository.findByIdentifier("PROD-001")).thenReturn(null);
        when(modelMapper.map(any(StockDto.class), eq(Stock.class))).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertEquals("Low Stock", result.getStockStatus());
        verify(stockRepository).save(stock);
    }

    @Test
    void update_notFound() {
        when(stockRepository.findByIdentifier("PROD-001")).thenReturn(null);

        StockDto result = stockService.update(stockDto);

        assertFalse(result.isSuccess());

        verify(stockRepository, never()).save(any());
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void update_available() {
        when(stockRepository.findByIdentifier("PROD-001")).thenReturn(stock);

        StockDto result = stockService.update(stockDto);

        assertEquals("Available", result.getStockStatus());

        verify(modelMapper).map(stockDto, stock);
        verify(stockRepository).save(stock);
    }

    @Test
    void update_lowStock() {
        stockDto.setQuantity(10);

        when(stockRepository.findByIdentifier("PROD-001")).thenReturn(stock);

        StockDto result = stockService.update(stockDto);

        assertEquals("Low Stock", result.getStockStatus());

        verify(modelMapper).map(stockDto, stock);
        verify(stockRepository).save(stock);
    }

    @Test
    void update_outOfStock() {
        stockDto.setQuantity(0);

        when(stockRepository.findByIdentifier("PROD-001")).thenReturn(stock);

        StockDto result = stockService.update(stockDto);

        assertEquals("Out of Stock", result.getStockStatus());

        verify(modelMapper).map(stockDto, stock);
        verify(stockRepository).save(stock);
    }

    @Test
    void update_boundary_equalMinimumStock() {
        stockDto.setQuantity(20);

        when(stockRepository.findByIdentifier("PROD-001")).thenReturn(stock);

        StockDto result = stockService.update(stockDto);

        assertEquals("Low Stock", result.getStockStatus());

        verify(modelMapper).map(stockDto, stock);
        verify(stockRepository).save(stock);
    }

    @Test
    void delete_test() {
        stockService.delete("PROD-001_WH1");

        verify(stockRepository).deleteByIdentifier("PROD-001_WH1");
    }

    @Test
    void findAll_test() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Stock> page = new PageImpl<>(List.of(stock));

        when(stockRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(stockDto));

        List<StockDto> result = stockService.findAll(pageable);

        assertEquals(1, result.size());
        verify(stockRepository).findAll(pageable);
    }

    @Test
    void findAll_emptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Stock> page = new PageImpl<>(List.of(), pageable, 0);

        when(stockRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of());

        List<StockDto> result = stockService.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findById_found() {
        when(stockRepository.findByIdentifier("PROD-001_WH1")).thenReturn(stock);
        when(modelMapper.map(stock, StockDto.class)).thenReturn(stockDto);

        StockDto result = stockService.findByIdentifier("PROD-001_WH1");

        assertNotNull(result);
    }

    @Test
    void findById_notFound() {
        when(stockRepository.findByIdentifier("PROD-001_WH1")).thenReturn(null);
        when(modelMapper.map(null, StockDto.class)).thenReturn(null);

        StockDto result = stockService.findByIdentifier("PROD-001_WH1");

        assertNull(result);
    }
}
