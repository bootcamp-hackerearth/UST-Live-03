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
        stockDto.setIdentifier("ST1");
        Mockito.when(stockRepository.findByIdentifier("ST1"))
                .thenReturn(null);
        Stock stock = new Stock();
        Mockito.when(modelMapper.map(Mockito.any(StockDto.class), Mockito.eq(Stock.class)))
                .thenReturn(stock);
        Mockito.when(stockRepository.save(Mockito.any(Stock.class)))
                .thenReturn(stock);
        stockDto.setSuccess(true);
        StockDto response = stockService.save(stockDto);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("ST1");
        Stock existingStock = new Stock();
        Mockito.when(stockRepository.findByIdentifier("ST1"))
                .thenReturn(existingStock);
        StockDto response = stockService.save(stockDto);
        Assertions.assertEquals("ST1", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Stock stock = new Stock();
        stock.setIdentifier("ST1");
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("ST1");
        Mockito.when(stockRepository.findByIdentifier("ST1"))
                .thenReturn(stock);
        Mockito.when(modelMapper.map(stock, StockDto.class))
                .thenReturn(stockDto);
        StockDto response = stockService.findByIdentifier("ST1");
        Assertions.assertEquals("ST1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("ST1");
        Stock existingStock = new Stock();
        existingStock.setIdentifier("ST1");
        Mockito.when(stockRepository.findByIdentifier("ST1"))
                .thenReturn(existingStock);
        Mockito.when(stockRepository.save(existingStock))
                .thenReturn(existingStock);
        StockDto response = stockService.update(stockDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("ST1");
        Mockito.when(stockRepository.findByIdentifier("ST1"))
                .thenReturn(null);
        StockDto response = stockService.update(stockDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(stockRepository)
                .deleteByIdentifier("ST1");
        stockService.delete("ST1");
        Mockito.verify(stockRepository)
                .deleteByIdentifier("ST1");
    }

    @Test
    void findAllTest() {
        Stock stock = new Stock();
        stock.setIdentifier("ST1");
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("ST1");
        List<Stock> stockList = List.of(stock);
        List<StockDto> stockDtoList = List.of(stockDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Stock> stockPage = new PageImpl<>(stockList);
        Mockito.when(stockRepository.findAll(pageable))
                .thenReturn(stockPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(stockList),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(stockDtoList);
        WsDto<StockDto> response = stockService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("ST1", response.getDtoList().get(0).getIdentifier());
    }
}