package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.Assertions;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @InjectMocks
    private StockServiceImpl stockService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void save_availableTest() {

        StockDto dto = new StockDto();
        dto.setProduct("P1");
        dto.setWarehouse("W1");
        dto.setQuantity(10L);

        Stock stock = new Stock();

        when(stockRepository.findByIdentifier(anyString()))
                .thenReturn(null);
        when(modelMapper.map(dto, Stock.class)).thenReturn(stock);

        StockDto response = stockService.save(dto);

        Assertions.assertEquals("P1W1", response.getIdentifier());
        Assertions.assertEquals("AVAILABLE", response.getStockStatus());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(stockRepository).save(stock);
    }

    @Test
    void save_limitedTest() {

        StockDto dto = new StockDto();
        dto.setProduct("P1");
        dto.setWarehouse("W1");
        dto.setQuantity(5L);

        when(stockRepository.findByIdentifier(anyString()))
                .thenReturn(null);
        when(modelMapper.map(dto, Stock.class)).thenReturn(new Stock());

        StockDto response = stockService.save(dto);

        Assertions.assertEquals("LIMITED STOCK", response.getStockStatus());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void save_outOfStockTest() {

        StockDto dto = new StockDto();
        dto.setProduct("P1");
        dto.setWarehouse("W1");
        dto.setQuantity(0L);

        when(stockRepository.findByIdentifier(anyString()))
                .thenReturn(null);
        when(modelMapper.map(dto, Stock.class)).thenReturn(new Stock());

        StockDto response = stockService.save(dto);

        Assertions.assertEquals("OUT OF STOCK", response.getStockStatus());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void save_duplicateTest() {

        StockDto dto = new StockDto();
        dto.setProduct("P1");
        dto.setWarehouse("W1");
        dto.setQuantity(10L);

        when(stockRepository.findByIdentifier(anyString()))
                .thenReturn(new Stock());

        StockDto response = stockService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void update_availableTest() {

        StockDto dto = new StockDto();
        dto.setIdentifier("P1W1");
        dto.setQuantity(10L);

        Stock stock = new Stock();

        when(stockRepository.findByIdentifier(anyString()))
                .thenReturn(stock);

        StockDto response = stockService.update(dto);

        Assertions.assertEquals("AVAILABLE", response.getStockStatus());
        Assertions.assertTrue(response.isSuccess());

        verify(stockRepository).save(stock);
    }

    @Test
    void update_limitedTest() {

        StockDto dto = new StockDto();
        dto.setIdentifier("P1W1");
        dto.setQuantity(3L);

        when(stockRepository.findByIdentifier(anyString()))
                .thenReturn(new Stock());

        StockDto response = stockService.update(dto);

        Assertions.assertEquals("LIMITED STOCK", response.getStockStatus());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void update_outOfStockTest() {

        StockDto dto = new StockDto();
        dto.setIdentifier("P1W1");
        dto.setQuantity(0L);

        when(stockRepository.findByIdentifier(anyString()))
                .thenReturn(new Stock());

        StockDto response = stockService.update(dto);

        Assertions.assertEquals("OUT OF STOCK", response.getStockStatus());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void update_notFoundTest() {

        StockDto dto = new StockDto();
        dto.setIdentifier("P1W1");

        when(stockRepository.findByIdentifier(anyString()))
                .thenReturn(null);

        StockDto response = stockService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {

        stockService.delete("P1W1");

        verify(stockRepository).deleteByIdentifier("P1W1");
    }

    @Test
    void findByIdentifierSuccessTest() {

        Stock stock = new Stock();
        StockDto dto = new StockDto();

        when(stockRepository.findByIdentifier("P1"))
                .thenReturn(stock);
        when(modelMapper.map(stock, StockDto.class))
                .thenReturn(dto);

        StockDto response = stockService.findByIdentifier("P1");

        Assertions.assertNotNull(response);
    }

    @Test
    void findByIdentifierFailureTest() {

        when(stockRepository.findByIdentifier("P1"))
                .thenReturn(null);

        StockDto response = stockService.findByIdentifier("P1");

        Assertions.assertNull(response);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Stock> stocks = List.of(new Stock());
        Page<Stock> page = new PageImpl<>(stocks);

        List<StockDto> dtos = List.of(new StockDto());

        when(stockRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(
                eq(stocks),
                any(Type.class)
        )).thenReturn(dtos);

        List<StockDto> result = stockService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(stockRepository).findAll(pageable);
    }
}