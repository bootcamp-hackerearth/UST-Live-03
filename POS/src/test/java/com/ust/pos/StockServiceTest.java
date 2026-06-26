package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

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
        StockDto dto = new StockDto();
        dto.setIdentifier("STOCK_001");

        Stock stock = new Stock();

        Mockito.when(stockRepository.findByIdentifier("STOCK_001")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Stock.class)).thenReturn(stock);
        Mockito.when(stockRepository.save(stock)).thenReturn(stock);
        StockDto response = stockService.save(dto);
        Assertions.assertEquals("STOCK_001", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveFailure_existingActive() {
        StockDto dto = new StockDto();
        dto.setIdentifier("STOCK_001");

        Stock existing = new Stock();
        existing.setDeleted(false);

        Mockito.when(stockRepository.findByIdentifier("STOCK_001")).thenReturn(existing);
        StockDto response = stockService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveFailure_softDeleted() {
        StockDto dto = new StockDto();
        dto.setIdentifier("STOCK_001");

        Stock existing = new Stock();
        existing.setDeleted(true);
        Mockito.when(stockRepository.findByIdentifier("STOCK_001")).thenReturn(existing);
        StockDto response = stockService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void findByIdentifierTest() {
        Stock stock = new Stock();
        stock.setIdentifier("STOCK_001");

        StockDto dto = new StockDto();
        dto.setIdentifier("STOCK_001");

        Mockito.when(stockRepository.findByIdentifier("STOCK_001")).thenReturn(stock);
        Mockito.when(modelMapper.map(stock, StockDto.class)).thenReturn(dto);
        StockDto response = stockService.findByIdentifier("STOCK_001");
        Assertions.assertEquals("STOCK_001", response.getIdentifier());
    }

    @Test
    void updateTest() {
        StockDto dto = new StockDto();
        dto.setIdentifier("STOCK_001");

        Stock existing = new Stock();

        Mockito.when(stockRepository.findByIdentifier("STOCK_001")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(stockRepository.save(existing)).thenReturn(existing);
        StockDto response = stockService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(stockRepository).save(existing);
    }

    @Test
    void updateFailure() {
        StockDto dto = new StockDto();
        dto.setIdentifier("STOCK_001");

        Mockito.when(stockRepository.findByIdentifier("STOCK_001")).thenReturn(null);
        StockDto response = stockService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Stock stock = new Stock();
        stock.setDeleted(false);

        Mockito.when(stockRepository.findByIdentifier("STOCK_001")).thenReturn(stock);
        Mockito.when(stockRepository.save(stock)).thenReturn(stock);

        stockService.delete("STOCK_001");
        Mockito.verify(stockRepository).findByIdentifier("STOCK_001");
        Mockito.verify(stockRepository).save(stock);
        Assertions.assertTrue(stock.isDeleted());
    }

    @Test
    void findAllTest() {
        Stock stock = new Stock();
        stock.setIdentifier("STOCK_001");

        StockDto dto = new StockDto();
        dto.setIdentifier("STOCK_001");

        List<Stock> list = List.of(stock);
        Page<Stock> page = new PageImpl<>(list, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(stockRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(List.of(dto));

        WsDto<StockDto> response = stockService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("STOCK_001", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void toggleStatusSuccessTest() {
        Stock stock = new Stock();
        stock.setIdentifier("STOCK_001");
        stock.setStatus(false);

        StockDto dto = new StockDto();
        dto.setIdentifier("STOCK_001");
        dto.setStatus(true);

        Mockito.when(stockRepository.findByIdentifier("STOCK_001")).thenReturn(stock);
        Mockito.when(stockRepository.save(stock)).thenReturn(stock);
        Mockito.when(modelMapper.map(stock, StockDto.class)).thenReturn(dto);
        StockDto response = stockService.toggleStatus("STOCK_001", true);
        Assertions.assertEquals("STOCK_001", response.getIdentifier());
        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void toggleStatusFailureTest() {
        Mockito.when(stockRepository.findByIdentifier("STOCK_001")).thenReturn(null);
        StockDto response = stockService.toggleStatus("STOCK_001", true);
        Assertions.assertNull(response);
        Mockito.verify(stockRepository, Mockito.never()).save(Mockito.any());
    }
}