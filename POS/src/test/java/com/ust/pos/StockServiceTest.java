package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.impl.StockServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    void setUp() {
        stock = new Stock();
        stock.setId(1L);
        stock.setIdentifier("STK_P001_W001");
        stock.setStatus(true);

        stockDto = new StockDto();
        stockDto.setId(1L);
        stockDto.setProductIdentifier("P001");
        stockDto.setWarehouseIdentifier("W001");
        stockDto.setIdentifier("STK_P001_W001");
        stockDto.setStatus(true);
    }

    @Test
    void save_shouldSaveStock_whenNotExists() {
        when(stockRepository.findByIdentifier(anyString())).thenReturn(null);
        when(modelMapper.map(stockDto, Stock.class)).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        verify(stockRepository).save(stock);
        assertTrue(result.getIdentifier().startsWith("STK_"));
    }

    @Test
    void save_shouldFail_whenStockExists() {
        when(stockRepository.findByIdentifier(anyString())).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(stockRepository, never()).save(any());
    }

    @Test
    void update_shouldUpdateStock_whenExists_sameIdentifier() {

        stock.setIdentifier("STK_P001_W001");
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));

        StockDto result = stockService.update(stockDto);

        verify(modelMapper).map(stockDto, stock);
        verify(stockRepository).save(stock);
        assertEquals("STK_P001_W001", result.getIdentifier());
    }


    @Test
    void update_shouldFail_whenStockNotFound() {
        when(stockRepository.findById(1L)).thenReturn(Optional.empty());

        StockDto result = stockService.update(stockDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(stockRepository, never()).save(any());
    }


    @Test
    void update_shouldFail_whenDuplicateIdentifierExists() {
        stock.setIdentifier("STK_P001_W001");
        stockDto.setIdentifier("STK_DUPLICATE");

        Stock duplicate = new Stock();
        duplicate.setIdentifier("STK_DUPLICATE");

        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));
        when(stockRepository.findByIdentifier("STK_DUPLICATE")).thenReturn(duplicate);

        StockDto result = stockService.update(stockDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(stockRepository, never()).save(any());
    }

    @Test
    void update_shouldSucceed_whenIdentifierChangedAndNoDuplicate() {
        stock.setIdentifier("STK_P001_W001");
        stockDto.setIdentifier("STK_NEW_IDENTIFIER");

        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));
        // duplicate check returns null → no conflict → line 57 evaluates to FALSE
        when(stockRepository.findByIdentifier("STK_NEW_IDENTIFIER")).thenReturn(null);

        StockDto result = stockService.update(stockDto);


        verify(modelMapper).map(stockDto, stock);
        verify(stockRepository).save(stock);
        assertNotNull(result);
    }

    @Test
    void deleteByIdentifier_shouldDeleteStock() {
        doNothing().when(stockRepository).deleteByIdentifier("STK_P001_W001");

        stockService.deleteByIdentifier("STK_P001_W001");

        verify(stockRepository).deleteByIdentifier("STK_P001_W001");
    }

    @Test
    void findByIdentifier_shouldReturnStockDto() {
        when(stockRepository.findByIdentifier("STK_P001_W001")).thenReturn(stock);
        when(modelMapper.map(stock, StockDto.class)).thenReturn(stockDto);

        StockDto result = stockService.findByIdentifier("STK_P001_W001");

        assertNotNull(result);
        assertEquals("STK_P001_W001", result.getIdentifier());
    }

    @Test
    void findAllTest() {
        List<Stock> stocks = List.of(stock);
        List<StockDto> stockDtos = List.of(stockDto);

        Page<Stock> stockPage = new PageImpl<>(stocks, PageRequest.of(0, 2), stocks.size());
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));

        Mockito.when(stockRepository.findAll(pageable)).thenReturn(stockPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(stocks),
                Mockito.any(java.lang.reflect.Type.class))
        ).thenReturn(stockDtos);

        List<StockDto> response = stockService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleStatus_shouldFlipTrueToFalse() {
        stock.setStatus(true);

        StockDto toggledDto = new StockDto();
        toggledDto.setStatus(false);

        when(stockRepository.findByIdentifier("STK_P001_W001")).thenReturn(stock);
        when(modelMapper.map(stock, StockDto.class)).thenReturn(toggledDto);

        StockDto result = stockService.toggleStatus("STK_P001_W001");

        assertFalse(stock.isStatus());
        verify(stockRepository).save(stock);
        assertFalse(result.isStatus());
    }

    @Test
    void toggleStatus_shouldFlipFalseToTrue() {
        stock.setStatus(false);

        StockDto toggledDto = new StockDto();
        toggledDto.setStatus(true);

        when(stockRepository.findByIdentifier("STK_P001_W001")).thenReturn(stock);
        when(modelMapper.map(stock, StockDto.class)).thenReturn(toggledDto);

        StockDto result = stockService.toggleStatus("STK_P001_W001");

        assertTrue(stock.isStatus());
        verify(stockRepository).save(stock);
        assertTrue(result.isStatus());
    }

    @Test
    void findIfTrue_shouldReturnActiveStocks() {
        when(stockRepository.findByStatusIsTrue()).thenReturn(List.of(stock));
        when(modelMapper.map(any(), any(Type.class))).thenReturn(List.of(stockDto));

        List<StockDto> result = stockService.findIfTrue();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isStatus());
    }
}