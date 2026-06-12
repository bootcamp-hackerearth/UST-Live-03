package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    @DisplayName("Save Stock - Success Case")
    void saveTest_Success() {
        StockDto dto = new StockDto();
        dto.setIdentifier("STK-100");
        Stock stock = new Stock();

        Mockito.when(stockRepository.findByIdentifier("STK-100")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Stock.class)).thenReturn(stock);

        StockDto result = stockService.save(dto);

        Assertions.assertNotNull(result);
        Mockito.verify(stockRepository).save(stock);
    }

    @Test
    @DisplayName("Save Stock - Failure: Already Exists")
    void saveTest_AlreadyExists() {
        StockDto dto = new StockDto();
        dto.setIdentifier("STK-100");
        Mockito.when(stockRepository.findByIdentifier("STK-100")).thenReturn(new Stock());

        StockDto result = stockService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Stock with identifier - STK-100 already exists", result.getMessage());
        Mockito.verify(stockRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("Update Stock - Success Case")
    void updateTest_Success() {
        StockDto dto = new StockDto();
        dto.setIdentifier("STK-100");
        Stock existingStock = new Stock();

        Mockito.when(stockRepository.findByIdentifier("STK-100")).thenReturn(existingStock);

        StockDto result = stockService.update(dto);

        Assertions.assertNotNull(result);
        Mockito.verify(modelMapper).map(dto, existingStock);
        Mockito.verify(stockRepository).save(existingStock);
    }

    @Test
    @DisplayName("Update Stock - Failure: Not Found")
    void updateTest_NotFound() {
        StockDto dto = new StockDto();
        dto.setIdentifier("STK-100");
        Mockito.when(stockRepository.findByIdentifier("STK-100")).thenReturn(null);

        StockDto result = stockService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Stock with identifier - STK-100 not found", result.getMessage());
        Mockito.verify(stockRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("Find All - Paginated Success")
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Stock> stocks = List.of(new Stock());
        Page<Stock> stockPage = new PageImpl<>(stocks);
        List<StockDto> dtos = List.of(new StockDto());

        Mockito.when(stockRepository.findAll(pageable)).thenReturn(stockPage);
        Mockito.when(modelMapper.map(eq(stocks), any(Type.class))).thenReturn(dtos);

        List<StockDto> result = stockService.findAll(pageable);

        Assertions.assertEquals(1, result.size());
        Mockito.verify(stockRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Find By Identifier - Success Case")
    void findByIdentifierTest() {
        Stock stock = new Stock();
        StockDto dto = new StockDto();
        Mockito.when(stockRepository.findByIdentifier("STK-100")).thenReturn(stock);
        Mockito.when(modelMapper.map(stock, StockDto.class)).thenReturn(dto);

        StockDto result = stockService.findByIdentifier("STK-100");
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Toggle Status - Success Case")
    void toggleStatusTest() {
        Stock stock = new Stock();
        stock.setStatus(true);
        StockDto dto = new StockDto();

        Mockito.when(stockRepository.findByIdentifier("STK-100")).thenReturn(stock);
        Mockito.when(modelMapper.map(stock, StockDto.class)).thenReturn(dto);

        StockDto result = stockService.toggleStatus("STK-100");

        Assertions.assertFalse(stock.isStatus());
        Mockito.verify(stockRepository).save(stock);
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Delete Stock - Success Case")
    void deleteTest() {
        boolean result = stockService.delete("STK-100");
        Assertions.assertTrue(result);
        Mockito.verify(stockRepository).deleteByIdentifier("STK-100");
    }
}