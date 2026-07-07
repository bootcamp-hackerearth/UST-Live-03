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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private StockServiceImpl stockService;

    private Stock stock;
    private StockDto dto;

    @BeforeEach
    void setup() {
        stock = new Stock();
        stock.setIdentifier("ST1");
        stock.setDeleted(false);
        stock.setStatus(true);

        dto = new StockDto();
        dto.setIdentifier("ST1");
    }

    @Test
    void findByIdentifierTest() {
        when(stockRepository.findByIdentifierAndIsDeletedFalse("ST1")).thenReturn(stock);
        when(modelMapper.map(stock, StockDto.class)).thenReturn(dto);

        StockDto result = stockService.findByIdentifier("ST1");

        assertNotNull(result);
        assertEquals("ST1", result.getIdentifier());
        verify(stockRepository).findByIdentifierAndIsDeletedFalse("ST1");
    }

    @Test
    void findByIdentifierNullTest() {
        when(stockRepository.findByIdentifierAndIsDeletedFalse("ST1")).thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> stockService.findByIdentifier("ST1"));

        verify(stockRepository).findByIdentifierAndIsDeletedFalse("ST1");
    }

    @Test
    void saveNewStockTest() {
        when(stockRepository.findByIdentifier("ST1")).thenReturn(null);
        when(modelMapper.map(dto, Stock.class)).thenReturn(stock);

        StockDto result = stockService.save(dto);

        assertNotNull(result);
        verify(stockRepository).save(stock);
    }

    @Test
    void saveAlreadyExistsTest() {
        when(stockRepository.findByIdentifier("ST1")).thenReturn(stock);

        StockDto result = stockService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals("Stock with identifier - ST1 already exists", result.getMessage());
    }

    @Test
    void saveDeletedStockTest() {
        stock.setDeleted(true);

        when(stockRepository.findByIdentifier("ST1")).thenReturn(stock);

        StockDto result = stockService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals("Stock identifier - ST1 not available", result.getMessage());
    }

    @Test
    void updateSuccessTest() {
        when(stockRepository.findByIdentifier("ST1")).thenReturn(stock);

        StockDto result = stockService.update(dto);

        assertNotNull(result);
        verify(stockRepository).save(stock);
    }

    @Test
    void updateNotFoundTest() {
        when(stockRepository.findByIdentifier("ST1")).thenReturn(null);

        StockDto result = stockService.update(dto);

        assertFalse(result.isSuccess());
        assertEquals("Stock with identifier - ST1 not found", result.getMessage());
    }

    @Test
    void updateVerifyMapperCalledTest() {
        when(stockRepository.findByIdentifier("ST1")).thenReturn(stock);
        stockService.update(dto);
        verify(modelMapper).map(dto, stock);
    }

    @Test
    void deleteTest() {
        when(stockRepository.findByIdentifier("ST1")).thenReturn(stock);
        stockService.delete("ST1");
        verify(stockRepository).findByIdentifier("ST1");
    }

    @Test
    void deleteNullStockTest() {
        when(stockRepository.findByIdentifier("ST1")).thenReturn(null);
        assertThrows(NullPointerException.class, () -> stockService.delete("ST1"));
        verify(stockRepository).findByIdentifier("ST1");
        verify(stockRepository, never()).save(any());
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        when(stockRepository.findByIdentifier("ST1")).thenReturn(stock);

        stockService.toggleStatus("ST1");

        assertFalse(stock.getStatus());
        verify(stockRepository).save(stock);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        stock.setStatus(false);

        when(stockRepository.findByIdentifier("ST1")).thenReturn(stock);

        stockService.toggleStatus("ST1");

        assertTrue(stock.getStatus());
        verify(stockRepository).save(stock);
    }

    @Test
    void toggleStatusNullTest() {
        stock.setStatus(null);

        when(stockRepository.findByIdentifier("ST1")).thenReturn(stock);

        stockService.toggleStatus("ST1");

        assertTrue(stock.getStatus());
        verify(stockRepository).save(stock);
    }

    @Test
    void toggleStatusNotFoundTest() {
        when(stockRepository.findByIdentifier("ST1")).thenReturn(null);
        stockService.toggleStatus("ST1");
        verify(stockRepository, never()).save(any());
    }

    @Test
    void findAllTest() {
        Pageable pageable = mock(Pageable.class);
        Page<Stock> page = mock(Page.class);

        List<Stock> stockList = List.of(stock);
        List<StockDto> dtoList = List.of(dto);

        when(stockRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        when(page.getContent()).thenReturn(stockList);
        when(page.getTotalElements()).thenReturn(1L);
        when(page.getTotalPages()).thenReturn(1);
        when(pageable.getPageSize()).thenReturn(10);
        when(pageable.getPageNumber()).thenReturn(0);
        when(modelMapper.map(eq(stockList), any(java.lang.reflect.Type.class))).thenReturn(dtoList);

        WsDto<StockDto> result = stockService.findAll(pageable);

        assertNotNull(result);
        assertNotNull(result.getDtoList());
        assertEquals(1, result.getDtoList().size());
        assertEquals(1L, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void findAllWithSpecificationTest() {
        Pageable pageable = mock(Pageable.class);
        Page<Stock> page = mock(Page.class);

        @SuppressWarnings("unchecked")
        Specification<Stock> specification = mock(Specification.class);

        List<Stock> stockList = List.of(stock);
        List<StockDto> dtoList = List.of(dto);

        when(stockRepository.findAll(specification, pageable)).thenReturn(page);
        when(page.getContent()).thenReturn(stockList);
        when(page.getTotalElements()).thenReturn(1L);
        when(page.getTotalPages()).thenReturn(1);
        when(pageable.getPageSize()).thenReturn(10);
        when(pageable.getPageNumber()).thenReturn(0);
        when(modelMapper.map(eq(stockList), any(java.lang.reflect.Type.class))).thenReturn(dtoList);

        WsDto<StockDto> result = stockService.findAll(specification, pageable, "st1");

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1L, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
        assertEquals("st1", result.getKeyword());

        verify(stockRepository).findAll(specification, pageable);
    }
}