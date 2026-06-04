package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.modell.Stock;
import com.ust.pos.modell.StockRepository;
import com.ust.pos.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class StockServiceTest {

    @InjectMocks
    private StockServiceImpl stockService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByIdentifierSuccess() {
        Stock stock = new Stock();
        stock.setIdentifier("STK-P1-W1");
        stock.setProductIdentifier("P1");
        stock.setWarehouseIdentifier("W1");
        when(stockRepository.findByIdentifier("STK-P1-W1")).thenReturn(stock);
        StockDto result = stockService.findByIdentifier("  STK-P1-W1 ");
        Assertions.assertEquals("STK-P1-W1", result.getIdentifier());
        Assertions.assertEquals("P1", result.getProductIdentifier());
    }

    @Test
    void findByIdentifierNotFound() {
        when(stockRepository.findByIdentifier("X")).thenReturn(null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> stockService.findByIdentifier("X"));
    }

    @Test
    void findByIdSuccess() {
        Stock stock = new Stock();
        stock.setId(1L);
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));
        StockDto result = stockService.findById(1L);
        Assertions.assertEquals(1L, result.getId());
    }

    @Test
    void findByIdNotFound() {
        when(stockRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(RuntimeException.class, () -> stockService.findById(1L));
    }

    @Test
    void saveMissingProductIdentifier() {
        StockDto dto = new StockDto();
        dto.setWarehouseIdentifier("W1");
        Assertions.assertThrows(IllegalArgumentException.class, () -> stockService.save(dto));
    }

    @Test
    void saveMissingWarehouseIdentifier() {
        StockDto dto = new StockDto();
        dto.setProductIdentifier("P1");
        Assertions.assertThrows(IllegalArgumentException.class, () -> stockService.save(dto));
    }

    @Test
    void saveStatusTrue() {
        StockDto dto = new StockDto();
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        dto.setQuantity(10);
        dto.setMinimumStock(5);
        when(stockRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        StockDto result = stockService.save(dto);
        Assertions.assertTrue(result.getStatus());
        Assertions.assertEquals("STK-P1-W1", result.getIdentifier());
    }

    @Test
    void saveStatusFalse() {
        StockDto dto = new StockDto();
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        dto.setQuantity(5);
        dto.setMinimumStock(10);
        when(stockRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        StockDto result = stockService.save(dto);
        Assertions.assertFalse(result.getStatus());
    }

    @Test
    void updateStatusTrue() {
        Stock stock = new Stock();
        stock.setId(1L);
        StockDto dto = new StockDto();
        dto.setId(1L);
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        dto.setQuantity(20);
        dto.setMinimumStock(10);
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));
        when(stockRepository.save(any())).thenReturn(stock);
        StockDto result = stockService.update(dto);
        Assertions.assertTrue(result.getStatus());
        Assertions.assertEquals("STK-P1-W1", result.getIdentifier());
    }

    @Test
    void updateStatusFalse() {
        Stock stock = new Stock();
        stock.setId(1L);
        StockDto dto = new StockDto();
        dto.setId(1L);
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        dto.setQuantity(5);
        dto.setMinimumStock(10);
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));
        when(stockRepository.save(any())).thenReturn(stock);
        StockDto result = stockService.update(dto);
        Assertions.assertFalse(result.getStatus());
    }

    @Test
    void updateNotFound() {
        when(stockRepository.findById(1L)).thenReturn(Optional.empty());
        StockDto dto = new StockDto();
        dto.setId(1L);
        Assertions.assertThrows(RuntimeException.class, () -> stockService.update(dto));
    }

    @Test
    void deleteTest() {
        stockService.delete(1L);
        verify(stockRepository).deleteById(1L);
    }

    @Test
    void findAllTest() {
        Stock stock = new Stock();
        stock.setIdentifier("STK-P1-W1");
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("STK-P1-W1");
        List<Stock> stocks = List.of(stock);
        List<StockDto> stockDtos = List.of(stockDto);
        Page<Stock> stockPage = new PageImpl<>(stocks, PageRequest.of(0, 2), stocks.size());
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Mockito.when(stockRepository.findAll(pageable)).thenReturn(stockPage);
        Mockito.when(modelMapper.map(Mockito.eq(stocks), Mockito.any(java.lang.reflect.Type.class))).thenReturn(stockDtos);
        List<StockDto> response = stockService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
    }
}