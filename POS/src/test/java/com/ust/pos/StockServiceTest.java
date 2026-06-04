package com.ust.pos;

import com.ust.pos.dto.StockDto;
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
    void saveTestSuccess() {
        StockDto dto = new StockDto();
        dto.setProduct("P1");
        dto.setWarehouse("W1");

        Stock stock = new Stock();

        when(stockRepository.findByIdentifier("P1_W1")).thenReturn(null);
        when(modelMapper.map(dto, Stock.class)).thenReturn(stock);

        StockDto result = stockService.save(dto);

        Assertions.assertEquals("P1_W1", result.getIdentifier());
        verify(stockRepository).save(stock);
    }

    @Test
    void saveTestFailure() {
        StockDto dto = new StockDto();
        dto.setProduct("P1");
        dto.setWarehouse("W1");

        Stock stock = new Stock();

        when(stockRepository.findByIdentifier("P1_W1")).thenReturn(stock);

        StockDto result = stockService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(stockRepository, never()).save(any());
    }

    @Test
    void updateTestSuccess() {
        StockDto dto = new StockDto();
        dto.setProduct("P1");
        dto.setWarehouse("W1");

        Stock stock = new Stock();

        when(stockRepository.findByIdentifier("P1_W1")).thenReturn(stock);

        StockDto result = stockService.update(dto);

        Assertions.assertEquals("P1_W1", result.getIdentifier());
        verify(modelMapper).map(dto, stock);
        verify(stockRepository).save(stock);
    }

    @Test
    void updateTestFailure() {
        StockDto dto = new StockDto();
        dto.setProduct("P1");
        dto.setWarehouse("W1");

        when(stockRepository.findByIdentifier("P1_W1")).thenReturn(null);

        StockDto result = stockService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(stockRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        stockService.delete("P1_W1");
        verify(stockRepository).deleteByIdentifier("P1_W1");
    }

    @Test
    void findByIdentifierSuccessTest() {
        Stock stock = new Stock();
        stock.setIdentifier("P1_W1");

        StockDto dto = new StockDto();
        dto.setIdentifier("P1_W1");

        when(stockRepository.findByIdentifier("P1_W1")).thenReturn(stock);
        when(modelMapper.map(stock, StockDto.class)).thenReturn(dto);

        StockDto result = stockService.findByIdentifier("P1_W1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("P1_W1", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(stockRepository.findByIdentifier("P1_W1")).thenReturn(null);

        StockDto result = stockService.findByIdentifier("P1_W1");

        Assertions.assertNull(result);
    }

    @Test
    void findAllTest() {
        Pageable pageable = mock(Pageable.class);
        Page<Stock> page = mock(Page.class);

        List<Stock> stocks = List.of(new Stock(), new Stock());
        List<StockDto> dtoList = List.of(new StockDto(), new StockDto());

        when(stockRepository.findAll(pageable)).thenReturn(page);
        when(page.getContent()).thenReturn(stocks);
        when(modelMapper.map(eq(stocks), any(Type.class))).thenReturn(dtoList);

        List<StockDto> result = stockService.findAll(pageable);

        Assertions.assertEquals(2, result.size());

        verify(stockRepository).findAll(pageable);
        verify(page).getContent();
        verify(modelMapper).map(eq(stocks), any(Type.class));
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Stock stock = new Stock();
        stock.setStatus(true);

        when(stockRepository.findByIdentifier("P1_W1")).thenReturn(stock);

        stockService.toggleStatus("P1_W1");

        Assertions.assertFalse(stock.isStatus());
        verify(stockRepository).save(stock);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Stock stock = new Stock();
        stock.setStatus(false);

        when(stockRepository.findByIdentifier("P1_W1")).thenReturn(stock);

        stockService.toggleStatus("P1_W1");

        Assertions.assertTrue(stock.isStatus());
        verify(stockRepository).save(stock);
    }

    @Test
    void toggleStatusNullTest() {
        when(stockRepository.findByIdentifier("P1_W1")).thenReturn(null);

        stockService.toggleStatus("P1_W1");

        verify(stockRepository, never()).save(any());
    }
}