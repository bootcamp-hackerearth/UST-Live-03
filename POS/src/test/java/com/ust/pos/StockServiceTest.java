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
import org.modelmapper.TypeToken;
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
        stockDto.setIdentifier("Admin");

        Mockito.when(stockRepository.findByIdentifier("Admin")).thenReturn(null);
        Stock stock = new Stock();
        Mockito.when(modelMapper.map(stockDto, Stock.class)).thenReturn(stock);
        Mockito.when(stockRepository.save(stock)).thenReturn(stock);
        StockDto response = stockService.save(stockDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertEquals(true, response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("S1");
        Stock stock = new Stock();

        Mockito.when(stockRepository.findByIdentifier("S1")).thenReturn(stock);
        StockDto response = stockService.save(stockDto);

        Assertions.assertEquals("S1", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");

        Assertions.assertEquals(false, response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Stock stock = new Stock();
        stock.setIdentifier("S1");

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("S1");

        Mockito.when(stockRepository.findByIdentifier("S1")).thenReturn(stock);
        Mockito.when(modelMapper.map(stock, StockDto.class)).thenReturn(stockDto);

        StockDto response = stockService.findByIdentifier("S1");

        Assertions.assertEquals("S1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("S1");

        Stock existingStock = new Stock();
        existingStock.setIdentifier("S1");

        Mockito.when(stockRepository.findByIdentifier("S1"))
                .thenReturn(existingStock);
        Mockito.when(stockRepository.save(existingStock))
                .thenReturn(existingStock);

        StockDto response = stockService.update(stockDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("S1");

        Mockito.when(stockRepository.findByIdentifier("S1"))
                .thenReturn(null);

        StockDto response = stockService.update(stockDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(stockRepository)
                .deleteByIdentifier("S1");

        stockService.delete("S1");

        Mockito.verify(stockRepository).deleteByIdentifier("S1");
    }

    @Test
    void findAllTest() {
        Stock stock = new Stock();
        stock.setIdentifier("S1");

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("S1");

        List<Stock> stocks = List.of(stock);
        List<StockDto> stockDtos = List.of(stockDto);

        Mockito.when(stockRepository.findAll()).thenReturn(stocks);
        Mockito.when(modelMapper.map(
                Mockito.eq(stocks),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(stockDtos);

        List<StockDto> response = stockService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleStatusTest() {
        Stock stock = new Stock();
        stock.setIdentifier("S1");
        stock.setStatus(true);

        Mockito.when(stockRepository.findByIdentifier("S1"))
                .thenReturn(stock);

        stockService.toggleStatus("S1");

        Assertions.assertFalse(stock.isStatus());
        Mockito.verify(stockRepository).save(stock);
    }

    @Test
    void toggleStatusNullTest() {
        Mockito.when(stockRepository.findByIdentifier("S1"))
                .thenReturn(null);

        stockService.toggleStatus("S1");

        Mockito.verify(stockRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findAllWithPaginationShouldReturnStockDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Stock> stocks = List.of(new Stock());
        Page<Stock> page = new PageImpl<>(stocks);

        List<StockDto> stockDtos = List.of(new StockDto());

        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();

        Mockito.when(stockRepository.findAll(pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(stocks, listType))
                .thenReturn(stockDtos);

        List<StockDto> response = stockService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Mockito.verify(stockRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(stocks, listType);
    }
}