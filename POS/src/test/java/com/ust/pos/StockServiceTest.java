package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Stock;
import com.ust.pos.modell.StockRepository;
import com.ust.pos.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class StockServiceTest {

    @InjectMocks
    private StockServiceImpl stockService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByIdentifierSuccess() {
        Stock stock = new Stock();
        stock.setIdentifier("STK-P1-W1");
        stock.setProductIdentifier("P1");
        stock.setWarehouseIdentifier("W1");
        when(stockRepository.findByIdentifier("STK-P1-W1")).thenReturn(stock);
        StockDto result = stockService.findByIdentifier("  STK-P1-W1 ");
        Assertions.assertEquals("STK-P1-W1", result.getIdentifier());
        Assertions.assertEquals("P1", result.getProductIdentifier());
    }

    @Test
    void findByIdentifierNotFound() {
        when(stockRepository.findByIdentifier("X")).thenReturn(null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> stockService.findByIdentifier("X"));
    }

    @Test
    void findByIdSuccess() {
        Stock stock = new Stock();
        stock.setId(1L);
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));
        StockDto result = stockService.findById(1L);
        Assertions.assertEquals(1L, result.getId());
    }

    @Test
    void saveStatusTrue() {
        StockDto dto = new StockDto();
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        dto.setQuantity(10);
        dto.setMinimumStock(5);
        when(stockRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        StockDto result = stockService.save(dto);
        Assertions.assertTrue(result.getStatus());
        Assertions.assertEquals("STK-P1-W1", result.getIdentifier());
    }

    @Test
    void saveStatusFalse() {
        StockDto dto = new StockDto();
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        dto.setQuantity(5);
        dto.setMinimumStock(10);
        when(stockRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        StockDto result = stockService.save(dto);
        Assertions.assertFalse(result.getStatus());
    }

    @Test
    void updateStatusTrue() {
        Stock stock = new Stock();
        stock.setId(1L);
        StockDto dto = new StockDto();
        dto.setId(1L);
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        dto.setQuantity(20);
        dto.setMinimumStock(10);
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));
        when(stockRepository.save(any())).thenReturn(stock);
        StockDto result = stockService.update(dto);
        Assertions.assertTrue(result.getStatus());
        Assertions.assertEquals("STK-P1-W1", result.getIdentifier());
    }

    @Test
    void updateStatusFalse() {
        Stock stock = new Stock();
        stock.setId(1L);
        StockDto dto = new StockDto();
        dto.setId(1L);
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        dto.setQuantity(5);
        dto.setMinimumStock(10);
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));
        when(stockRepository.save(any())).thenReturn(stock);
        StockDto result = stockService.update(dto);
        Assertions.assertFalse(result.getStatus());
    }

    @Test
    void deleteTest() {
        stockService.delete(1L);
        verify(stockRepository).deleteById(1L);
    }

    @Test
    void findAllTest() {
        Stock stock = new Stock();
        stock.setIdentifier("STK-P1-W1");
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("STK-P1-W1");
        List<Stock> stocks = List.of(stock);
        List<StockDto> stockDtos = List.of(stockDto);
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Page<Stock> stockPage = new PageImpl<>(stocks, pageable, stocks.size());
        Mockito.when(stockRepository.findAll(pageable)).thenReturn(stockPage);
        Mockito.when(modelMapper.map(Mockito.eq(stocks), Mockito.any(java.lang.reflect.Type.class))).thenReturn(stockDtos);
        WsDto<StockDto> response = stockService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("STK-P1-W1", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPage());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
        Mockito.verify(stockRepository, Mockito.times(1)).findAll(pageable);
        Mockito.verify(modelMapper, Mockito.times(1)).map(Mockito.eq(stocks), Mockito.any(java.lang.reflect.Type.class));
    }

}