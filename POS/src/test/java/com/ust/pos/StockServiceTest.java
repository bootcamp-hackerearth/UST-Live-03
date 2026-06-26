package com.ust.pos;

import com.ust.pos.dto.PaginationResponseDto;
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
        stockDto.setIdentifier("STK001");
        Stock stock = new Stock();
        Mockito.when(stockRepository.findByIdentifier("STK001")).thenReturn(null);
        Mockito.when(modelMapper.map(stockDto, Stock.class)).thenReturn(stock);
        Mockito.when(stockRepository.save(stock)).thenReturn(stock);
        StockDto response = stockService.save(stockDto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("STK001", response.getIdentifier());
        Assertions.assertTrue(stock.isStockStatus());
    }

    @Test
    void saveDuplicateTest() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("STK001");
        Stock stock = new Stock();
        Mockito.when(stockRepository.findByIdentifier("STK001")).thenReturn(stock);
        StockDto response = stockService.save(stockDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveSoftDeletedTest() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("STK001");
        Stock stock = new Stock();
        stock.setDeleted(true);
        Mockito.when(stockRepository.findByIdentifier("STK001")).thenReturn(stock);
        StockDto response = stockService.save(stockDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void findByIdentifierTest() {
        Stock stock = new Stock();
        stock.setIdentifier("STK001");
        StockDto dto = new StockDto();
        dto.setIdentifier("STK001");
        Mockito.when(stockRepository.findByIdentifier("STK001")).thenReturn(stock);
        Mockito.when(modelMapper.map(stock, StockDto.class)).thenReturn(dto);
        StockDto response = stockService.findByIdentifier("STK001");
        Assertions.assertEquals("STK001", response.getIdentifier());
    }

    @Test
    void updateTest() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("STK001");
        Stock stock = new Stock();
        stock.setIdentifier("STK001");
        Mockito.when(stockRepository.findByIdentifier("STK001")).thenReturn(stock);
        Mockito.when(stockRepository.save(stock)).thenReturn(stock);
        StockDto response = stockService.update(stockDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateFailureTest() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("STK001");
        Mockito.when(stockRepository.findByIdentifier("STK001")).thenReturn(null);
        StockDto response = stockService.update(stockDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Stock stock = new Stock();
        stock.setIdentifier("STK001");
        Mockito.when(stockRepository.findByIdentifier("STK001")).thenReturn(stock);
        stockService.delete("STK001");
        Assertions.assertTrue(stock.isDeleted());
        Mockito.verify(stockRepository).save(stock);
    }

    @Test
    void deleteNotFoundTest() {
        Mockito.when(stockRepository.findByIdentifier("STK001")).thenReturn(null);
        Assertions.assertThrows(RuntimeException.class, () -> stockService.delete("STK001"));
    }

    @Test
    void findAllWithPageableTest() {
        Stock stock = new Stock();
        stock.setIdentifier("STK001");
        StockDto dto = new StockDto();
        dto.setIdentifier("STK001");
        List<Stock> stocks = List.of(stock);
        List<StockDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Stock> page = new PageImpl<>(stocks);
        Mockito.when(stockRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(stocks), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<StockDto> response = stockService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
    }

    @Test
    void findAllWithoutPageableTest() {
        Stock stock = new Stock();
        stock.setIdentifier("STK001");
        StockDto dto = new StockDto();
        dto.setIdentifier("STK001");
        List<Stock> stocks = List.of(stock);
        List<StockDto> dtos = List.of(dto);
        Mockito.when(stockRepository.findAll()).thenReturn(stocks);
        Mockito.when(modelMapper.map(Mockito.eq(stocks), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<StockDto> response = stockService.findAll(null);
        Assertions.assertEquals(1, response.getDtoList().size());
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
        Mockito.when(modelMapper.map(stock, StockDto.class)).thenReturn(dto);
        StockDto response = stockService.toggleStatus("STK001", true);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("STK001", response.getIdentifier());
        Assertions.assertTrue(stock.isStatus());
        Mockito.verify(stockRepository).save(stock);
    }

    @Test
    void toggleStatusStockNotFoundTest() {
        Mockito.when(stockRepository.findByIdentifier("STK001")).thenReturn(null);
        Mockito.when(modelMapper.map(null, StockDto.class)).thenReturn(null);
        StockDto response = stockService.toggleStatus("STK001", true);
        Assertions.assertNull(response);
        Mockito.verify(stockRepository, Mockito.never()).save(Mockito.any());
    }
}