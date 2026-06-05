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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;


@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @InjectMocks
    private StockServiceImpl stockService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Lays In-001");
        Mockito.when(stockRepository.findByIdentifier("Lays In-001")).thenReturn(null);
        Stock stock = new Stock();
        Mockito.when(modelMapper.map(stockDto, Stock.class)).thenReturn(stock);
        Mockito.when(stockRepository.save(stock)).thenReturn(stock);
        StockDto response = stockService.save(stockDto);
        Assertions.assertEquals("Lays In-001", response.getIdentifier());
        Assertions.assertEquals(true, response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Lays In-001");
        Stock stock = new Stock();
        Mockito.when(stockRepository.findByIdentifier("Lays In-001")).thenReturn(stock);
        StockDto response = stockService.save(stockDto);
        Assertions.assertEquals("Lays In-001", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");
        Assertions.assertEquals(false, response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Stock stock = new Stock();
        stock.setIdentifier("Lays In-001");
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Lays In-001");
        Mockito.when(stockRepository.findByIdentifier("Lays In-001")).thenReturn(stock);
        Mockito.when(modelMapper.map(stock, StockDto.class)).thenReturn(stockDto);
        StockDto response = stockService.findByIdentifier("Lays In-001");
        Assertions.assertEquals("Lays In-001", response.getIdentifier());
    }

    @Test
    void updateTest() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Lays In-001");
        Stock existingStock = new Stock();
        existingStock.setIdentifier("Lays In-001");
        Mockito.when(stockRepository.findByIdentifier("Lays In-001")).thenReturn(existingStock);
        Mockito.when(stockRepository.save(existingStock)).thenReturn(existingStock);
        StockDto response = stockService.update(stockDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Lays In-001");
        Mockito.when(stockRepository.findByIdentifier("Lays In-001")).thenReturn(null);
        StockDto response = stockService.update(stockDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(stockRepository).deleteByIdentifier("Lays In-001");
        stockService.delete("Lays In-001");
        Mockito.verify(stockRepository).deleteByIdentifier("Lays In-001");
    }

    @Test
    void findAllWithPageableTest() {
        Stock stock = new Stock();
        stock.setIdentifier("Lays IN-001");
        StockDto dto = new StockDto();
        dto.setIdentifier("Lays IN-001");
        List<Stock> stocks = List.of(stock);
        List<StockDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Stock> stockPage = new PageImpl<>(stocks);
        Mockito.when(stockRepository.findAll(pageable)).thenReturn(stockPage);
        Mockito.when(modelMapper.map(Mockito.eq(stocks), Mockito.any(Type.class)
        )).thenReturn(dtos);
        List<StockDto> response = stockService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Lays IN-001", response.get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Stock stock = new Stock();
        stock.setIdentifier("Lays IN-001");
        StockDto dto = new StockDto();
        dto.setIdentifier("Lays IN-001");
        List<Stock> stocks = List.of(stock);
        List<StockDto> dtos = List.of(dto);
        Mockito.when(stockRepository.findAll()).thenReturn(stocks);
        Mockito.when(modelMapper.map(Mockito.eq(stocks), Mockito.any(Type.class)
        )).thenReturn(dtos);
        List<StockDto> response = stockService.findAll(null);
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleStatusSuccessTest() {
        Stock stock = new Stock();
        stock.setIdentifier("STK001");
        stock.setStatus(false);
        StockDto dto = new StockDto();
        dto.setIdentifier("STK001");
        dto.setStatus(true);
        Mockito.when(stockRepository.findByIdentifier("STK001")).thenReturn(stock);
        Mockito.when(stockRepository.save(stock)).thenReturn(stock);
        Mockito.when(modelMapper.map(stock, StockDto.class)).thenReturn(dto);
        StockDto response = stockService.toggleStatus("STK001", true);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("STK001", response.getIdentifier());
        Assertions.assertTrue(stock.isStatus());
        Assertions.assertTrue(response.isStatus());
        Mockito.verify(stockRepository).findByIdentifier("STK001");
        Mockito.verify(stockRepository).save(stock);
        Mockito.verify(modelMapper).map(stock, StockDto.class);
    }

    @Test
    void toggleStatusStockNotFoundTest() {
        Mockito.when(stockRepository.findByIdentifier("STK001")).thenReturn(null);
        Mockito.when(modelMapper.map(null, StockDto.class)).thenReturn(null);
        StockDto response = stockService.toggleStatus("STK001", true);
        Assertions.assertNull(response);
        Mockito.verify(stockRepository).findByIdentifier("STK001");
        Mockito.verify(stockRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(modelMapper).map(null, StockDto.class);
    }
}