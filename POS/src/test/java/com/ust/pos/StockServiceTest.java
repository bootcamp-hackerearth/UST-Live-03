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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    void save_success() {
        StockDto dto = new StockDto();
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        dto.setAvailableQuantity(10);
        Stock stock = new Stock();
        Stock saved = new Stock();
        StockDto mapped = new StockDto();
        mapped.setIdentifier("P1_W1");
        when(stockRepository.findByIdentifier("P1_W1")).thenReturn(null);
        when(modelMapper.map(any(StockDto.class), eq(Stock.class))).thenReturn(stock);
        when(stockRepository.save(any(Stock.class))).thenReturn(saved);
        when(modelMapper.map(eq(saved), eq(StockDto.class))).thenReturn(mapped);
        StockDto response = stockService.save(dto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Stock saved successfully", response.getMessage());
    }

    @Test
    void save_failure_alreadyExists() {
        StockDto dto = new StockDto();
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        when(stockRepository.findByIdentifier("P1_W1")).thenReturn(new Stock());
        StockDto response = stockService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Product already exists in this warehouse", response.getMessage());
    }

    @Test
    void update_success() {
        StockDto dto = new StockDto();
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        dto.setAvailableQuantity(5);
        dto.setReorderLevel(10);
        dto.setStatus(true);
        Stock existing = new Stock();
        StockDto mapped = new StockDto();
        mapped.setIdentifier("P1_W1");
        when(stockRepository.findByIdentifier("P1_W1")).thenReturn(existing);
        when(stockRepository.save(existing)).thenReturn(existing);
        when(modelMapper.map(eq(existing), eq(StockDto.class))).thenReturn(mapped);
        StockDto response = stockService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Stock updated successfully", response.getMessage());
    }

    @Test
    void update_failure_notFound() {
        StockDto dto = new StockDto();
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        when(stockRepository.findByIdentifier("P1_W1")).thenReturn(null);
        StockDto response = stockService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Stock not found", response.getMessage());
    }

    @Test
    void find_success() {
        Stock stock = new Stock();
        StockDto dto = new StockDto();
        dto.setIdentifier("P1_W1");
        when(stockRepository.findByIdentifier("P1_W1")).thenReturn(stock);
        when(modelMapper.map(eq(stock), eq(StockDto.class))).thenReturn(dto);
        StockDto response = stockService.findByIdentifier("P1_W1");
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void find_notFound() {
        when(stockRepository.findByIdentifier("P1_W1")).thenReturn(null);
        StockDto response = stockService.findByIdentifier("P1_W1");
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Stock not found", response.getMessage());
    }

    @Test
    void findAllTest() {
        Stock stock = new Stock();
        List<Stock> list = List.of(stock);
        List<StockDto> dtoList = List.of(new StockDto());
        Page<Stock> page = new PageImpl<>(list);
        Pageable pageable = PageRequest.of(0, 5);
        when(stockRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class))).thenReturn(dtoList);
        List<StockDto> result = stockService.findAll(pageable);
        Assertions.assertEquals(1, result.size());
    }

    @Test
    void deleteTest() {
        stockService.delete("P1_W1");
        verify(stockRepository).deleteByIdentifier("P1_W1");
    }

    @Test
    void toggle_success() {
        Stock stock = new Stock();
        stock.setStatus(true);
        StockDto dto = new StockDto();
        dto.setSuccess(true);
        when(stockRepository.findByIdentifier("P1_W1")).thenReturn(stock);
        when(stockRepository.save(stock)).thenReturn(stock);
        when(modelMapper.map(eq(stock), eq(StockDto.class))).thenReturn(dto);
        StockDto response = stockService.toggleStatus("P1_W1");
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void toggle_notFound() {
        when(stockRepository.findByIdentifier("P1_W1")).thenReturn(null);
        StockDto response = stockService.toggleStatus("P1_W1");
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Stock not found", response.getMessage());
    }
}