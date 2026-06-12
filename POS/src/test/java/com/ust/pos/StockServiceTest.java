package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.impl.StockServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Stock> stocks = List.of(new Stock());
        Page<Stock> page = new PageImpl<>(stocks);
        List<StockDto> dtoList = List.of(new StockDto());

        when(stockRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class))).thenReturn(dtoList);

        WsDto<StockDto> result = stockService.findAll(pageable);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(1, result.getContent().size());

        verify(stockRepository).findAll(pageable);
    }

    @Test
    void testFindByIdentifier() {
        String identifier = "P1W1";

        Stock stock = new Stock();
        StockDto dto = new StockDto();

        when(stockRepository.findByIdentifier(identifier)).thenReturn(stock);
        when(modelMapper.map(stock, StockDto.class)).thenReturn(dto);

        StockDto result = stockService.findByIdentifier(identifier);

        assertNotNull(result);
        verify(stockRepository).findByIdentifier(identifier);
    }

    @Test
    void testSaveSuccess() {
        StockDto dto = new StockDto();
        dto.setProduct("P1");
        dto.setWarehouse("W1");

        Stock mappedStock = new Stock();

        when(stockRepository.findByIdentifier("P1W1")).thenReturn(null);
        when(modelMapper.map(dto, Stock.class)).thenReturn(mappedStock);

        StockDto result = stockService.save(dto);

        assertNotNull(result);
        assertEquals("P1W1", result.getIdentifier());

        verify(stockRepository).save(mappedStock);
    }

    @Test
    void testSaveDuplicate() {
        StockDto dto = new StockDto();
        dto.setProduct("P1");
        dto.setWarehouse("W1");

        when(stockRepository.findByIdentifier("P1W1"))
                .thenReturn(new Stock());

        StockDto result = stockService.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));

        verify(stockRepository, never()).save(any());
    }

    @Test
    void testDelete() {
        String identifier = "P1W1";

        stockService.delete(identifier);

        verify(stockRepository).deleteByIdentifier(identifier);
    }

    @Test
    void testUpdateSuccess() {
        StockDto dto = new StockDto();
        dto.setIdentifier("P1W1");

        Stock existing = new Stock();

        when(stockRepository.findByIdentifier("P1W1"))
                .thenReturn(existing);

        StockDto result = stockService.update(dto);

        assertNotNull(result);

        verify(modelMapper).map(dto, existing);
        verify(stockRepository).save(existing);
    }

    @Test
    void testUpdateNotFound() {
        StockDto dto = new StockDto();
        dto.setIdentifier("NOT_FOUND");

        when(stockRepository.findByIdentifier("NOT_FOUND"))
                .thenReturn(null);

        StockDto result = stockService.update(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));

        verify(stockRepository, never()).save(any());
    }
}