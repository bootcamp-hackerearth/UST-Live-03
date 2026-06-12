package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
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
        stockDto.setIdentifier("STOCK_001");

        Mockito.when(stockRepository.findByIdentifier("STOCK_001")).thenReturn(null);
        Stock stock = new Stock();
        Mockito.when(modelMapper.map(stockDto, Stock.class)).thenReturn(stock);
        Mockito.when(stockRepository.save(stock)).thenReturn(stock);
        StockDto response = stockService.save(stockDto);
        Assertions.assertEquals("STOCK_001", response.getIdentifier());
        Assertions.assertEquals(true, response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("STOCK_001");
        Stock stock = new Stock();
        
        Mockito.when(stockRepository.findByIdentifier("STOCK_001")).thenReturn(stock);
        StockDto response = stockService.save(stockDto);
        Assertions.assertEquals("STOCK_001", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");
        Assertions.assertEquals(false, response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Stock stock = new Stock();
        stock.setIdentifier("STOCK_001");
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("STOCK_001");
        
        Mockito.when(stockRepository.findByIdentifier("STOCK_001")).thenReturn(stock);
        Mockito.when(modelMapper.map(stock, StockDto.class)).thenReturn(stockDto);
        StockDto response = stockService.findByIdentifier("STOCK_001");
        Assertions.assertEquals("STOCK_001", response.getIdentifier());
    }

    @Test
    void updateTest() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("STOCK_001");
        Stock existingStock = new Stock();
        existingStock.setIdentifier("STOCK_001");
        
        Mockito.when(stockRepository.findByIdentifier("STOCK_001")).thenReturn(existingStock);
        Mockito.when(stockRepository.save(existingStock)).thenReturn(existingStock);
        StockDto response = stockService.update(stockDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("STOCK_001");
        
        Mockito.when(stockRepository.findByIdentifier("STOCK_001")).thenReturn(null);
        StockDto response = stockService.update(stockDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(stockRepository).deleteByIdentifier("STOCK_001");
        stockService.delete("STOCK_001");
        Mockito.verify(stockRepository).deleteByIdentifier("STOCK_001");
    }

    @Test
    void findAllTest() {
        Stock stock = new Stock();
        stock.setIdentifier("STOCK_001");

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("STOCK_001");

        List<Stock> stocks = List.of(stock);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Stock> stockPage = new PageImpl<>(stocks, pageable, stocks.size());

        Mockito.when(stockRepository.findAll(pageable)).thenReturn(stockPage);
        Mockito.when(modelMapper.map(Mockito.eq(stocks),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(List.of(stockDto));
        WsDto<StockDto> response = stockService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getDtoList());
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("STOCK_001", response.getDtoList().get(0).getIdentifier());

        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void toggleStatusSuccessTest() {
        Stock stock = new Stock();
        stock.setIdentifier("STOCK_001");
        stock.setStatus(false); 
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("STOCK_001");
        stockDto.setStatus(true);
        
        Mockito.when(stockRepository.findByIdentifier("STOCK_001")).thenReturn(stock);
        Mockito.when(stockRepository.save(stock)).thenReturn(stock);
        Mockito.when(modelMapper.map(stock, StockDto.class)).thenReturn(stockDto);
        StockDto response = stockService.toggleStatus("STOCK_001", true);
        Assertions.assertEquals("STOCK_001", response.getIdentifier());
        Assertions.assertTrue(response.isStatus()); 
    }

    @Test
    void changeStockStatusFailureTest() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("STOCK_001");
        
        Mockito.when(stockRepository.findByIdentifier("STOCK_001")).thenReturn(null);
        StockDto response = stockService.toggleStatus("STOCK_001", true);
        Assertions.assertNull(response);
        Mockito.verify(stockRepository, Mockito.never()).save(Mockito.any());
    }
}