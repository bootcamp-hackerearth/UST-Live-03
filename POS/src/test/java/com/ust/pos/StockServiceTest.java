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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @InjectMocks
    private StockServiceImpl stockService;

    @Mock
    private StockRepository stockRepository;

    @Mock(lenient = true)
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {
        StockDto dto = new StockDto();
        dto.setProduct("PROD1");
        dto.setWarehouse("WH1");

        Stock stock = new Stock();
        stock.setIdentifier("PROD1WH1");

        when(stockRepository.findByIdentifier("PROD1WH1")).thenReturn(null);
        when(modelMapper.map(dto, Stock.class)).thenReturn(stock);

        StockDto response = stockService.save(dto);

        assertEquals("PROD1WH1", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        verify(stockRepository).save(stock);
    }

    @Test
    void saveFailureTest() {
        Stock stock = new Stock();
        stock.setIdentifier("PROD1WH1");

        StockDto dto = new StockDto();
        dto.setProduct("PROD1");
        dto.setWarehouse("WH1");

        when(stockRepository.findByIdentifier("PROD1WH1")).thenReturn(stock);

        StockDto response = stockService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(stockRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        Stock stock = new Stock();
        stock.setIdentifier("PROD1WH1");

        StockDto dto = new StockDto();
        dto.setIdentifier("PROD1WH1");

        when(stockRepository.findByIdentifier("PROD1WH1")).thenReturn(stock);

        StockDto response = stockService.update(dto);

        assertEquals("PROD1WH1", response.getIdentifier());
        verify(modelMapper).map(dto, stock);
        verify(stockRepository).save(stock);
    }

    @Test
    void updateFailureTest() {
        StockDto dto = new StockDto();
        dto.setIdentifier("PROD1WH1");

        when(stockRepository.findByIdentifier("PROD1WH1")).thenReturn(null);

        StockDto response = stockService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(stockRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        stockService.delete("PROD1WH1");
        verify(stockRepository).deleteByIdentifier("PROD1WH1");
    }

    @Test
    void findByIdentifierSuccessTest() {
        Stock stock = new Stock();
        stock.setIdentifier("PROD1WH1");

        StockDto dto = new StockDto();
        dto.setIdentifier("PROD1WH1");

        when(stockRepository.findByIdentifier("PROD1WH1")).thenReturn(stock);
        when(modelMapper.map(stock, StockDto.class)).thenReturn(dto);

        StockDto response = stockService.findByIdentifier("PROD1WH1");

        assertNotNull(response);
        assertEquals("PROD1WH1", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(stockRepository.findByIdentifier("PROD1WH1")).thenReturn(null);

        StockDto response = stockService.findByIdentifier("PROD1WH1");

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