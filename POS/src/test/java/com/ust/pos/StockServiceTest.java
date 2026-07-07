package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.Arrays;
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

    private Stock stock;
    private StockDto stockDto;

    @BeforeEach
    void setup() {
        stock = new Stock();
        stock.setIdentifier("S001");
        stock.setStatus(true);

        stockDto = new StockDto();
        stockDto.setIdentifier("S001");
    }

    @Test
    void testFindByIdentifier_Found() {
        when(stockRepository.findByIdentifierAndIsDeletedFalse("S001")).thenReturn(stock);
        when(modelMapper.map(stock, StockDto.class)).thenReturn(stockDto);

        StockDto result = stockService.findByIdentifier("S001");

        assertNotNull(result);
        assertEquals("S001", result.getIdentifier());
    }

    @Test
    void testFindByIdentifier_NotFound() {
        when(stockRepository.findByIdentifierAndIsDeletedFalse("S001")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            stockService.findByIdentifier("S001");
        });
    }

    @Test
    void testSave_AlreadyExists() {
        when(stockRepository.findByIdentifier("S001")).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertFalse(result.isSuccess());
        verify(stockRepository, never()).save(any());
    }

    @Test
    void testSave_Success() {
        when(stockRepository.findByIdentifier("S001")).thenReturn(null);
        when(modelMapper.map(stockDto, Stock.class)).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertNotNull(result);
        verify(stockRepository).save(stock);
    }

    @Test
    void testUpdate_NotFound() {
        when(stockRepository.findByIdentifier("S001")).thenReturn(null);

        StockDto result = stockService.update(stockDto);

        assertFalse(result.isSuccess());
        verify(stockRepository, never()).save(any());
    }

    @Test
    void testUpdate_Success() {
        when(stockRepository.findByIdentifier("S001")).thenReturn(stock);

        StockDto result = stockService.update(stockDto);

        verify(modelMapper).map(stockDto, stock);
        verify(stockRepository).save(stock);
    }

    @Test
    void testDelete() {
        Stock stockEntity = new Stock();
        stockEntity.setIdentifier("S001");

        when(stockRepository.findByIdentifier("S001")).thenReturn(stockEntity);

        stockService.delete("S001");

        verify(stockRepository).findByIdentifier("S001");
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Stock> stocks = Arrays.asList(stock);
        Page<Stock> stockPage = new PageImpl<>(stocks, pageable, 1);
        List<StockDto> dtoList = Arrays.asList(stockDto);

        when(stockRepository.findByIsDeletedFalse(pageable)).thenReturn(stockPage);

        doReturn(dtoList).when(modelMapper)
                .map(anyList(), any(java.lang.reflect.Type.class));

        WsDto<StockDto> result = stockService.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
    }

    @Test
    void testToggleStatus() {
        when(stockRepository.findByIdentifier("S001")).thenReturn(stock);

        stockService.toggleStatus("S001");

        assertFalse(stock.isStatus());
        verify(stockRepository).save(stock);
    }

    @Test
    void testToggleStatus_NotFound() {
        when(stockRepository.findByIdentifier("S001")).thenReturn(null);

        stockService.toggleStatus("S001");

        verify(stockRepository, never()).save(any());
    }
}