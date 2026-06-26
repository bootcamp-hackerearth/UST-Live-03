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
    void findByIdentifier_Found() {

        Stock stock = new Stock();
        stock.setIdentifier("P1W1");

        StockDto dto = new StockDto();
        dto.setIdentifier("P1W1");

        when(stockRepository.findByIdentifier("P1W1"))
                .thenReturn(stock);

        when(modelMapper.map(stock, StockDto.class))
                .thenReturn(dto);

        StockDto result = stockService.findByIdentifier("P1W1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("P1W1", result.getIdentifier());
    }

    @Test
    void save_NewStock() {

        StockDto dto = new StockDto();
        dto.setProduct("P1");
        dto.setWarehouse("W1");

        Stock stock = new Stock();

        when(stockRepository.findByIdentifier("P1W1"))
                .thenReturn(null);

        when(modelMapper.map(dto, Stock.class))
                .thenReturn(stock);

        when(stockRepository.save(stock))
                .thenReturn(stock);

        StockDto result = stockService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("P1W1", result.getIdentifier());
        verify(stockRepository).save(stock);
    }

    @Test
    void save_StockExists() {

        Stock existing = new Stock();

        StockDto dto = new StockDto();
        dto.setProduct("P1");
        dto.setWarehouse("W1");

        when(stockRepository.findByIdentifier("P1W1"))
                .thenReturn(existing);

        StockDto result = stockService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(stockRepository, never()).save(any());
    }

    @Test
    void update_StockExists() {

        Stock existing = new Stock();

        StockDto dto = new StockDto();
        dto.setIdentifier("P1W1");

        when(stockRepository.findByIdentifier("P1W1"))
                .thenReturn(existing);

        when(stockRepository.save(existing))
                .thenReturn(existing);

        StockDto result = stockService.update(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("P1W1", result.getIdentifier());
        verify(modelMapper).map(dto, existing);
        verify(stockRepository).save(existing);
    }

    @Test
    void update_StockNotFound() {

        StockDto dto = new StockDto();
        dto.setIdentifier("P1W1");

        when(stockRepository.findByIdentifier("P1W1"))
                .thenReturn(null);

        StockDto result = stockService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(stockRepository, never()).save(any());
    }

    @Test
    void deleteTest() {

        Stock stock = new Stock();

        when(stockRepository.findByIdentifier("P1W1"))
                .thenReturn(stock);

        stockService.delete("P1W1");

        verify(stockRepository).findByIdentifier("P1W1");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Stock stock1 = new Stock();
        Stock stock2 = new Stock();

        Page<Stock> page = new PageImpl<>(
                List.of(stock1, stock2),
                pageable,
                2
        );

        List<StockDto> dtoList =
                List.of(new StockDto(), new StockDto());

        when(stockRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(
                eq(page.getContent()), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<StockDto> result =
                stockService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2,
                result.getContent().size());
        Assertions.assertEquals(0,
                result.getPage());
        Assertions.assertEquals(10,
                result.getSizePerPage());
        Assertions.assertEquals(1,
                result.getTotalPages());
        Assertions.assertEquals(2,
                result.getTotalRecords());

        verify(stockRepository)
                .findByIsDeletedFalse(pageable);
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Stock> page =
                new PageImpl<>(List.of(), pageable, 0);

        when(stockRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(
                eq(page.getContent()), any(Type.class)))
                .thenReturn(List.of());

        WsDto<StockDto> result =
                stockService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(
                result.getContent().isEmpty());
        Assertions.assertEquals(0,
                result.getTotalRecords());

        verify(stockRepository)
                .findByIsDeletedFalse(pageable);
    }
}