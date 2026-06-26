package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private StockServiceImpl stockService;

    private StockDto stockDto;
    private Stock stock;

    @BeforeEach
    void setUp() {
        stockDto = new StockDto();
        stockDto.setIdentifier("STK-001");

        stock = new Stock();
        stock.setIdentifier("STK-001");
        stock.setStatus(true);
        stock.setDeleted(false);
    }

    @Test
    @DisplayName("Save Stock - Success")
    void save_Success() {
        when(stockRepository.findByIdentifier("STK-001")).thenReturn(null);
        when(modelMapper.map(stockDto, Stock.class)).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Stock created successfully", result.getMessage());
        verify(stockRepository).save(stock);
    }

    @Test
    @DisplayName("Save Stock - Failure: Already Exists")
    void save_Failure_AlreadyExists() {
        stock.setDeleted(false);
        when(stockRepository.findByIdentifier("STK-001")).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    @DisplayName("Save Stock - Failure: Previously Soft-Deleted")
    void save_Failure_PreviouslyDeleted() {
        stock.setDeleted(true);
        when(stockRepository.findByIdentifier("STK-001")).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("was previously deleted"));
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    @DisplayName("Find All Stocks - Paginated Success")
    void findAll_PaginatedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Stock> stockPage = new PageImpl<>(List.of(stock));

        when(stockRepository.findByDeletedFalse(pageable)).thenReturn(stockPage);
        when(modelMapper.map(eq(stockPage.getContent()), any(Type.class))).thenReturn(List.of(stockDto));

        WsDto<StockDto> result = stockService.findAll(pageable);

        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertFalse(result.getDtoList().isEmpty());
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifier_Success() {
        when(stockRepository.findByIdentifier("STK-001")).thenReturn(stock);
        when(modelMapper.map(stock, StockDto.class)).thenReturn(stockDto);

        StockDto result = stockService.findByIdentifier("STK-001");

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Update Stock - Success")
    void update_Success() {
        when(stockRepository.findByIdentifier("STK-001")).thenReturn(stock);

        StockDto result = stockService.update(stockDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("Updated"));
        verify(stockRepository).save(stock);
    }

    @Test
    @DisplayName("Update Stock - Failure: Not Found")
    void update_Failure_NotFound() {
        when(stockRepository.findByIdentifier("STK-001")).thenReturn(null);

        StockDto result = stockService.update(stockDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    @DisplayName("Toggle Status - Success")
    void toggleStatus_Success() {
        when(stockRepository.findByIdentifier("STK-001")).thenReturn(stock);
        when(modelMapper.map(stock, StockDto.class)).thenReturn(stockDto);

        StockDto result = stockService.toggleStatus("STK-001");

        Assertions.assertFalse(stock.isStatus());
        verify(stockRepository).save(stock);
    }

    @Test
    @DisplayName("Delete Stock - Success")
    void delete_Success() {
        when(stockRepository.findByIdentifier("STK-001")).thenReturn(stock);

        boolean result = stockService.delete("STK-001");

        Assertions.assertTrue(result);
        verify(stockRepository).save(stock);
    }

    @Test
    @DisplayName("Delete Stock - Failure: Not Found")
    void delete_Failure_NotFound() {
        when(stockRepository.findByIdentifier("STK-001")).thenReturn(null);

        boolean result = stockService.delete("STK-001");

        Assertions.assertFalse(result);
        verify(stockRepository, never()).save(any(Stock.class));
    }
}