package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.impl.StockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private StockServiceImpl stockService;

    private Stock stock;
    private StockDto stockDto;
    private final String stockId = "STK_PROD-01_WH-01";

    @BeforeEach
    void setUp() {
        stock = new Stock();
        stock.setId(1L);
        stock.setIdentifier(stockId);
        stock.setStatus(true);
        stock.setDeleted(false);

        stockDto = new StockDto();
        stockDto.setProductIdentifier("PROD-01");
        stockDto.setWarehouseIdentifier("WH-01");
    }

    @Test
    void testFindByIdentifier_Success() {
        when(stockRepository.findByIdentifier(stockId)).thenReturn(stock);

        StockDto result = stockService.findByIdentifier(stockId);

        assertNotNull(result);
        assertEquals(stockId, result.getIdentifier());
        verify(stockRepository, times(1)).findByIdentifier(stockId);
    }

    @Test
    void testFindByIdentifier_ThrowsResourceNotFoundException() {
        when(stockRepository.findByIdentifier(stockId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> stockService.findByIdentifier(stockId));
        verify(stockRepository, times(1)).findByIdentifier(stockId);
    }

    @Test
    void testSave_WhenDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> stockService.save(null));
    }

    @Test
    void testSave_WhenProductIdentifierIsNull() {
        stockDto.setProductIdentifier(null);
        assertThrows(IllegalArgumentException.class, () -> stockService.save(stockDto));
    }

    @Test
    void testSave_WhenWarehouseIdentifierIsNull() {
        stockDto.setWarehouseIdentifier(null);
        assertThrows(IllegalArgumentException.class, () -> stockService.save(stockDto));
    }

    @Test
    void testSave_WhenStockAlreadyExistsAndNotDeleted() {
        when(stockRepository.findByIdentifier(stockId)).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    void testSave_WhenStockAlreadyExistsButDeleted() {
        stock.setDeleted(true);
        when(stockRepository.findByIdentifier(stockId)).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    void testSave_Success() {
        when(stockRepository.findByIdentifier(stockId)).thenReturn(null);
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(stockId, result.getIdentifier());
        assertEquals("Stock created successfully", result.getMessage());
        verify(stockRepository, times(1)).save(any(Stock.class));
    }

    @Test
    void testUpdate_WhenStockNotFound() {
        stockDto.setIdentifier(stockId);
        when(stockRepository.findByIdentifier(stockId)).thenReturn(null);

        StockDto result = stockService.update(stockDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    void testUpdate_WhenStockDeleted() {
        stockDto.setIdentifier(stockId);
        stock.setDeleted(true);
        when(stockRepository.findByIdentifier(stockId)).thenReturn(stock);

        StockDto result = stockService.update(stockDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    void testUpdate_Success() {
        stockDto.setIdentifier(stockId);
        when(stockRepository.findByIdentifier(stockId)).thenReturn(stock);
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);

        StockDto result = stockService.update(stockDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Stock updated successfully", result.getMessage());
        verify(stockRepository, times(1)).save(any(Stock.class));
    }

    @Test
    void testDeleteByIdentifier_WhenStockNotFound() {
        when(stockRepository.findByIdentifier(stockId)).thenReturn(null);

        stockService.deleteByIdentifier(stockId);

        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    void testDeleteByIdentifier_Success() {
        when(stockRepository.findByIdentifier(stockId)).thenReturn(stock);
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);

        stockService.deleteByIdentifier(stockId);

        verify(stockRepository, times(1)).save(any(Stock.class));
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Stock> page = new PageImpl<>(Collections.singletonList(stock), pageable, 1);
        when(stockRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<StockDto> result = stockService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
        assertFalse(result.getDtoList().isEmpty());
        verify(stockRepository, times(1)).findByDeletedFalse(pageable);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testFindAllWithSpecification() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Stock> page = new PageImpl<>(Collections.singletonList(stock), pageable, 1);
        Specification<Stock> spec = mock(Specification.class);
        when(stockRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        WsDto<StockDto> result = stockService.findAll(spec, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
        assertFalse(result.getDtoList().isEmpty());
        verify(stockRepository, times(1)).findAll(spec, pageable);
    }

    @Test
    void testToggleStatus_WhenStockNotFound() {
        when(stockRepository.findByIdentifier(stockId)).thenReturn(null);

        StockDto result = stockService.toggleStatus(stockId);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    void testToggleStatus_Success() {
        when(stockRepository.findByIdentifier(stockId)).thenReturn(stock);
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);

        StockDto result = stockService.toggleStatus(stockId);

        assertNotNull(result);
        assertFalse(result.isStatus());
        verify(stockRepository, times(1)).save(any(Stock.class));
    }

    @Test
    void testFindIfTrue() {
        when(stockRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(Collections.singletonList(stock));

        List<StockDto> result = stockService.findIfTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(stockId, result.get(0).getIdentifier());
        verify(stockRepository, times(1)).findByStatusIsTrueAndDeletedFalse();
    }
}