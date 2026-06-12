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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private org.modelmapper.ModelMapper modelMapper;

    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    void saveSuccessTest() {
        StockDto dto = new StockDto();
        dto.setIdentifier("STOCK1");

        Stock stock = new Stock();
        stock.setIdentifier("STOCK1");

        Mockito.when(stockRepository.findByIdentifier("STOCK1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Stock.class)).thenReturn(stock);

        StockDto result = stockService.save(dto);

        Assertions.assertEquals("STOCK1", result.getIdentifier());

        Mockito.verify(stockRepository).save(stock);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        Stock existing = new Stock();
        existing.setIdentifier("STOCK1");

        StockDto dto = new StockDto();
        dto.setIdentifier("STOCK1");

        Mockito.when(stockRepository.findByIdentifier("STOCK1")).thenReturn(existing);

        StockDto result = stockService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Stock with identifier - STOCK1 already exists", result.getMessage());

        Mockito.verify(stockRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateSuccessTest() {
        Stock existing = new Stock();
        existing.setIdentifier("STOCK2");

        StockDto dto = new StockDto();
        dto.setIdentifier("STOCK2");

        Mockito.when(stockRepository.findByIdentifier("STOCK2")).thenReturn(existing);

        StockDto result = stockService.update(dto);

        Assertions.assertEquals("STOCK2", result.getIdentifier());

        Mockito.verify(modelMapper).map(dto, existing);
        Mockito.verify(stockRepository).save(existing);
    }

    @Test
    void updateFailureNotFoundTest() {
        StockDto dto = new StockDto();
        dto.setIdentifier("UNKNOWN");

        Mockito.when(stockRepository.findByIdentifier("UNKNOWN")).thenReturn(null);

        StockDto result = stockService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Stock with identifier - UNKNOWN not found", result.getMessage());
    }

    @Test
    void findByIdentifierSuccessTest() {
        Stock stock = new Stock();
        stock.setIdentifier("STOCK3");

        StockDto dto = new StockDto();
        dto.setIdentifier("STOCK3");

        Mockito.when(stockRepository.findByIdentifier("STOCK3")).thenReturn(stock);
        Mockito.when(modelMapper.map(stock, StockDto.class)).thenReturn(dto);

        StockDto result = stockService.findByIdentifier("STOCK3");

        Assertions.assertEquals("STOCK3", result.getIdentifier());
    }

    @Test
    void deleteTest() {
        stockService.delete("STOCK4");
        Mockito.verify(stockRepository).deleteByIdentifier("STOCK4");
    }

    @Test
    void findAllSuccessTest() {
        Stock s1 = new Stock();
        s1.setIdentifier("S1");

        Stock s2 = new Stock();
        s2.setIdentifier("S2");

        List<Stock> stocks = List.of(s1, s2);

        StockDto d1 = new StockDto();
        d1.setIdentifier("S1");

        StockDto d2 = new StockDto();
        d2.setIdentifier("S2");

        List<StockDto> stockDtos = List.of(d1, d2);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Stock> stockPage = new PageImpl<>(stocks, pageable, stocks.size());

        Mockito.when(stockRepository.findAll(pageable)).thenReturn(stockPage);
        Mockito.when(modelMapper.map(Mockito.eq(stocks), Mockito.any(Type.class))).thenReturn(stockDtos);

        WsDto<StockDto> result = stockService.findAll(pageable);

        Assertions.assertEquals(2, result.getDtoList().size());
        Assertions.assertEquals("S1", result.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals("S2", result.getDtoList().get(1).getIdentifier());

        Mockito.verify(stockRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(Mockito.eq(stocks), Mockito.any(Type.class));
    }
}