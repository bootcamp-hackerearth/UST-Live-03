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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @InjectMocks
    private StockServiceImpl stockService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierSuccessTest() {
        Stock stock = new Stock();
        stock.setIdentifier("PROD1_WH1");

        StockDto dto = new StockDto();
        dto.setIdentifier("PROD1_WH1");

        when(stockRepository.findByIdentifier("PROD1_WH1"))
                .thenReturn(stock);

        when(modelMapper.map(stock, StockDto.class))
                .thenReturn(dto);

        StockDto result = stockService.findByIdentifier("PROD1_WH1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("PROD1_WH1", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(stockRepository.findByIdentifier("PROD1_WH1"))
                .thenReturn(null);

        StockDto result = stockService.findByIdentifier("PROD1_WH1");

        Assertions.assertNull(result);
    }

    @Test
    void saveSuccessTest() {
        StockDto dto = new StockDto();
        dto.setProduct("PROD1");
        dto.setWarehouse("WH1");

        Stock stock = new Stock();

        when(stockRepository.findByIdentifier("PROD1_WH1"))
                .thenReturn(null);

        when(modelMapper.map(dto, Stock.class))
                .thenReturn(stock);

        StockDto result = stockService.save(dto);

        Assertions.assertEquals("PROD1_WH1", result.getIdentifier());

        verify(stockRepository).save(stock);
    }

    @Test
    void saveAlreadyExistsTest() {
        StockDto dto = new StockDto();
        dto.setProduct("PROD1");
        dto.setWarehouse("WH1");

        Stock existingStock = new Stock();
        existingStock.setDeleted(false);

        when(stockRepository.findByIdentifier("PROD1_WH1"))
                .thenReturn(existingStock);

        StockDto result = stockService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Stock with identifier - PROD1_WH1 already exists",
                result.getMessage()
        );

        verify(stockRepository, never()).save(any());
    }

    @Test
    void saveDeletedStockTest() {
        StockDto dto = new StockDto();
        dto.setProduct("PROD1");
        dto.setWarehouse("WH1");

        Stock existingStock = new Stock();
        existingStock.setDeleted(true);

        when(stockRepository.findByIdentifier("PROD1_WH1"))
                .thenReturn(existingStock);

        StockDto result = stockService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Stock with identifier - PROD1_WH1 was deleted , Please Contact the Administrator to add.",
                result.getMessage()
        );

        verify(stockRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        StockDto dto = new StockDto();
        dto.setProduct("PROD1");
        dto.setWarehouse("WH1");

        Stock existingStock = new Stock();
        existingStock.setIdentifier("PROD1_WH1");

        when(stockRepository.findByIdentifier("PROD1_WH1"))
                .thenReturn(existingStock);

        StockDto result = stockService.update(dto);

        Assertions.assertEquals("PROD1_WH1", result.getIdentifier());

        verify(modelMapper).map(dto, existingStock);
        verify(stockRepository).save(existingStock);
    }

    @Test
    void updateFailureTest() {
        StockDto dto = new StockDto();
        dto.setProduct("PROD1");
        dto.setWarehouse("WH1");

        when(stockRepository.findByIdentifier("PROD1_WH1"))
                .thenReturn(null);

        StockDto result = stockService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Stock with identifier - PROD1_WH1 not found",
                result.getMessage()
        );

        verify(stockRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Stock stock = new Stock();

        when(stockRepository.findByIdentifier("PROD1_WH1"))
                .thenReturn(stock);

        stockService.delete("PROD1_WH1");

        verify(stockRepository).findByIdentifier("PROD1_WH1");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Stock stock1 = new Stock();
        Stock stock2 = new Stock();

        List<Stock> stocks = List.of(
                stock1,
                stock2
        );

        Page<Stock> page = new PageImpl<>(
                stocks,
                pageable,
                2
        );

        List<StockDto> dtoList = List.of(
                new StockDto(),
                new StockDto()
        );

        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();

        when(stockRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(stocks, listType))
                .thenReturn(dtoList);

        WsDto<StockDto> result = stockService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getDtoList().size());
        Assertions.assertEquals(2, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Stock stock = new Stock();
        stock.setStatus(true);

        when(stockRepository.findByIdentifier("PROD1_WH1"))
                .thenReturn(stock);

        stockService.toggleStatus("PROD1_WH1");

        Assertions.assertFalse(stock.getStatus());

        verify(stockRepository).save(stock);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Stock stock = new Stock();
        stock.setStatus(false);

        when(stockRepository.findByIdentifier("PROD1_WH1"))
                .thenReturn(stock);

        stockService.toggleStatus("PROD1_WH1");

        Assertions.assertTrue(stock.getStatus());

        verify(stockRepository).save(stock);
    }

    @Test
    void toggleStatusStockNotFoundTest() {
        when(stockRepository.findByIdentifier("PROD1_WH1"))
                .thenReturn(null);

        stockService.toggleStatus("PROD1_WH1");

        verify(stockRepository, never()).save(any());
    }
}